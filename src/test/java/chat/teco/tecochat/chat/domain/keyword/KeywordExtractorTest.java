package chat.teco.tecochat.chat.domain.keyword;

import static chat.teco.tecochat.chat.domain.GptModel.GPT_4;
import static chat.teco.tecochat.chat.domain.SettingMessage.BACK_END_SETTING;
import static chat.teco.tecochat.chat.exception.KeywordExceptionType.CAN_NOT_EXTRACTED_KEYWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import chat.teco.tecochat.chat.domain.Answer;
import chat.teco.tecochat.chat.domain.Chat;
import chat.teco.tecochat.chat.domain.GptClient;
import chat.teco.tecochat.chat.domain.Question;
import chat.teco.tecochat.chat.domain.QuestionAndAnswer;
import chat.teco.tecochat.chat.exception.KeywordException;
import chat.teco.tecochat.common.exception.BaseExceptionType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("KeywordExtractor 은(는)")
class KeywordExtractorTest {

    private final GptClient gptClient = mock(GptClient.class);

    private KeywordExtractor extractor = new KeywordExtractor(gptClient);

    private Chat chat = new Chat(GPT_4, BACK_END_SETTING, "제목", 1L);

    @Test
    void 채팅_생성_이벤트를_받아_해당_채팅의_키워드를_추출하여_저장한다() {
        // given
        given(gptClient.ask(any(), any())).willReturn(
                new QuestionAndAnswer(Question.question("질문"), Answer.answer("답변1||답변2||답변3"), 1));

        // when
        final List<Keyword> keywordList = extractor.extractKeywords(chat);

        // then
        assertThat(keywordList)
                .extracting(Keyword::keyword)
                .containsExactly("답변1", "답변2", "답변3");
    }

    @Test
    void 키워드가_3개가_나오지_않느다면_예외처리한다() {
        // given
        given(gptClient.ask(any(), any())).willReturn(
                new QuestionAndAnswer(Question.question("질문"), Answer.answer("답변1||답변2||답변3||답변4"), 1));

        // when
        final BaseExceptionType baseExceptionType = assertThrows(KeywordException.class, () ->
                extractor.extractKeywords(chat)
        ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(CAN_NOT_EXTRACTED_KEYWORD);
    }
}