package chat.teco.tecochat.comment.application;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.chat.domain.chat.ChatRepository;
import chat.teco.tecochat.comment.application.service.CommentService;
import chat.teco.tecochat.comment.domain.Comment;
import chat.teco.tecochat.comment.domain.CommentRepository;
import chat.teco.tecochat.member.domain.Member;
import chat.teco.tecochat.member.domain.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;

@SuppressWarnings("NonAsciiCharacters")
public class CommentCommandUseCaseTest {

    protected final MemberRepository memberRepository = mock(MemberRepository.class);
    protected final ChatRepository chatRepository = mock(ChatRepository.class);
    protected final CommentRepository commentRepository = mock(CommentRepository.class);

    protected final CommentService commentService = new CommentService(
            memberRepository, chatRepository, commentRepository
    );

    protected void 회원을_저장한다(Member 회원) {
        given(memberRepository.findById(회원.id())).willReturn(Optional.of(회원));
    }

    protected void 회원이_저장되지_않았다(Member 회원) {
        given(memberRepository.findById(회원.id())).willReturn(Optional.empty());
    }

    protected void 채팅을_저장한다(Chat 채팅) {
        given(chatRepository.findById(채팅.id())).willReturn(Optional.of(채팅));
    }

    protected void 채팅이_저장되지_않았다(Chat 채팅) {
        given(chatRepository.findById(채팅.id())).willReturn(Optional.empty());
    }

    protected void 댓글을_저장한다(Comment 댓글) {
        given(commentRepository.findById(댓글.id())).willReturn(Optional.of(댓글));
    }

    protected void 댓글이_저장되지_않았다(Comment 댓글) {
        given(commentRepository.findById(댓글.id())).willReturn(Optional.empty());
    }

    @AfterEach
    void tearDown() {
        reset(memberRepository, chatRepository, commentRepository);
    }
}