package chat.teco.tecochat.comment.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("Comment(댓글) 은(는)")
class CommentTest {

    private final Long 작성자_ID = 1L;
    private final Long 질문_ID = 10L;
    private final Comment 댓글 = new Comment(질문_ID, 작성자_ID, "댓글");

    @Test
    void 작성자는_수정할_수_있다() {
        // when
        댓글.update(작성자_ID, "변경");

        // then
        assertThat(댓글.content()).isEqualTo("변경");
    }

    @Test
    void 작성자가_아닌데_수정하려는_경우_예외() {
        // when & then
        assertThrows(IllegalStateException.class, () ->
                댓글.update(작성자_ID + 1L, "변경")
        );
    }

    @Test
    void 작성자는_제거할_수_있다() {
        // when & then
        assertDoesNotThrow(() -> 댓글.validateDelete(작성자_ID));
    }

    @Test
    void 작성자가_아니면_제거할_수_없다() {
        // when & then
        assertThrows(IllegalStateException.class, () ->
                댓글.validateDelete(작성자_ID + 1)
        );
    }
}
