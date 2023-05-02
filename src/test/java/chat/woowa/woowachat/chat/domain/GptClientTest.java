package chat.woowa.woowachat.chat.domain;

import static chat.woowa.woowachat.chat.domain.Answer.answer;
import static chat.woowa.woowachat.chat.domain.GptModel.GPT_3_5_TURBO;
import static chat.woowa.woowachat.chat.domain.Question.question;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionRequest;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionRequest.MessageRequest;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionResponse;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionResponse.ChoiceResponse;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionResponse.MessageResponse;
import chat.woowa.woowachat.chat.domain.GptClient.ChatCompletionResponse.UsageResponse;
import chat.woowa.woowachat.chat.fixture.Chat2Fixture;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("GptClient 은(는)")
class GptClientTest {

    private static final String OVER_MAX_TOKEN_CODE = "context_length_exceeded";
    private final HttpHeaders apiKeySettingHeader = new HttpHeaders();
    private final RestTemplate restTemplate = mock(RestTemplate.class);
    private final GptClient client = new GptClient(restTemplate, apiKeySettingHeader, "");

    @Test
    void Chat_의_messagesWithFreeToken_을_호출해야_한다() {
        // given
        final ChatCompletionResponse response = new ChatCompletionResponse("", ",", "",
                List.of(new ChoiceResponse(1L,
                        new MessageResponse("assistant", "답변"),
                        "stop")),
                new UsageResponse(1, 2, 3));

        given(restTemplate.postForEntity(any(String.class), any(), any())).willReturn(
                ResponseEntity.status(200).body(response));
        final Chat chat = mock(Chat.class);

        // when
        client.ask(chat, Question.question("질문"));

        // then
        verify(chat, times(1))
                .messagesWithFreeToken();
    }


    @Test
    void 받아온_응답을_바탕으로_QnA의_토큰_수를_계산하여_반환한다() {
        // given
        final Chat chat = mock(Chat.class);
        final ChatCompletionResponse response = new ChatCompletionResponse("", ",", "",
                List.of(new ChoiceResponse(1L,
                        new MessageResponse("assistant", "답변"),
                        "stop")),
                new UsageResponse(2500, 500, 3000));

        given(restTemplate.postForEntity(any(String.class), any(), any())).willReturn(
                ResponseEntity.status(200).body(response));

        given(chat.totalToken()).willReturn(2000);

        // when
        final QuestionAndAnswer qna = client.ask(chat, question("질문"));

        // then
        // 기존 채팅 [2000], API 결과 전체 토큰 [3000] -> [3000] - [2000] = 1000
        assertThat(qna.token()).isEqualTo(1000);
    }

    /**
     * Caused by: org.springframework.web.client.HttpClientErrorException$BadRequest: 400 Bad Request: "{<EOL> "error":
     * {<EOL> "message": "This model's maximum context length is 4097 tokens. However, your messages resulted in 8436
     * tokens. Please reduce the length of the messages.",
     * <EOL> "type": "invalid_request_error",<EOL>
     * "param": "messages",<EOL> "code": "context_length_exceeded"<EOL> }<EOL>}<EOL>”
     */
    @Test
    void 질문의_토큰이_허용치를_넘어_GPT_API_에서_오류가_반환된다면_이를_처리한다() {
        // given
        final Chat chat = mock(Chat.class);
        given(restTemplate.postForEntity(any(String.class), any(), any()))
                .willThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, OVER_MAX_TOKEN_CODE));

        // when & then
        assertThatThrownBy(
                () -> client.ask(chat, question("질문"))
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void GPT_API_에_문제가_있는_경우_예외처리() {
        final Chat chat = mock(Chat.class);
        given(restTemplate.postForEntity(any(String.class), any(), any()))
                .willThrow(new RestClientException("some problem"));

        // when & then
        assertThatThrownBy(
                () -> client.ask(chat, question("질문"))
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void 최대_토큰에서_채팅의_총합_토큰을_뺐을_때_2000이상_남지_않는다면_이에_맞추어_메세지를_제외한다() {
        // given
        final List<QuestionAndAnswer> messages = List.of(
                new QuestionAndAnswer(  // 제외
                        question("Q1"),
                        answer("A1"),
                        1000
                ),
                new QuestionAndAnswer(  // 제외
                        question("Q2"),
                        answer("A2"),
                        1000
                ),
                new QuestionAndAnswer(  // 제외
                        question("Q3"),
                        answer("A3"),
                        1000
                ),
                new QuestionAndAnswer(
                        question("Q4"),
                        answer("A4"),
                        1000
                ),
                new QuestionAndAnswer(
                        question("Q5"),
                        answer("A5"),
                        1000
                )
        );
        final Chat chat = ChatFixture.chatWithModel(GPT_3_5_TURBO, messages);

        // when
        final ChatCompletionRequest from = ChatCompletionRequest.of(chat, question("질문"));

        // then
        assertThat(from.messages()).extracting(MessageRequest::content)
                .containsExactly(
                        SettingMessage.BACK_END_SETTING.message(),
                        "Q4",
                        "A4",
                        "Q5",
                        "A5",
                        "질문");
    }
}
