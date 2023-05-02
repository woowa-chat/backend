package chat.woowa.woowachat.chat.application;

import chat.woowa.woowachat.chat.domain.Chat;
import chat.woowa.woowachat.chat.domain.ChatRepository;
import chat.woowa.woowachat.chat.domain.GptClient;
import chat.woowa.woowachat.chat.domain.GptModel;
import chat.woowa.woowachat.chat.domain.Question;
import chat.woowa.woowachat.chat.domain.QuestionAndAnswer;
import chat.woowa.woowachat.chat.domain.SettingMessage;
import chat.woowa.woowachat.chat.dto.AskCommand;
import chat.woowa.woowachat.chat.dto.MessageDto;
import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

@Service
public class AskChatService {

    private final MemberRepository memberRepository;
    private final ChatRepository chatRepository;
    private final GptClient gptClient;
    private final TransactionTemplate transactionTemplate;

    public AskChatService(final MemberRepository memberRepository, final ChatRepository chatRepository,
                          final GptClient gptClient, final TransactionTemplate transactionTemplate) {
        this.memberRepository = memberRepository;
        this.chatRepository = chatRepository;
        this.gptClient = gptClient;
        this.transactionTemplate = transactionTemplate;
    }

    public MessageDto createAsk(final AskCommand command) {
        final Question question = command.question();
        final Member member = findMemberById(command.memberId());
        Chat chat = new Chat(GptModel.GPT_3_5_TURBO,
                SettingMessage.byCourse(member.course()),
                question.content(),
                command.memberId()
        );

        final QuestionAndAnswer qna = gptClient.ask(chat, question);

        return transactionTemplate.execute(status -> {
            chatRepository.save(chat);
            chat.addQuestionAndAnswer(qna);
            return new MessageDto(chat.id(), qna.answer().content());
        });
    }

    public MessageDto ask(final Long id, final AskCommand command) {
        final Chat chat = findChatWithQuestionAndAnswersById(id);

        final QuestionAndAnswer qna = gptClient.ask(chat, command.question());

        return transactionTemplate.execute(status -> {
            findChatWithQuestionAndAnswersById(id).addQuestionAndAnswer(qna);
            return new MessageDto(chat.id(), qna.answer().content());
        });
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new IllegalArgumentException("일치하는 회원 ID가 없습니다. (TODO: 변경)"));
    }

    private Chat findChatWithQuestionAndAnswersById(final Long id) {
        return chatRepository.findWithQuestionAndAnswersById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이디가 %d인 채팅이 없습니다."));
    }
}
