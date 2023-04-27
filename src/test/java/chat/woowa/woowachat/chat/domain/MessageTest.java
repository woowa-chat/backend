package chat.woowa.woowachat.chat.domain;

import static chat.woowa.woowachat.chat.domain.Role.ASSISTANT;
import static chat.woowa.woowachat.chat.domain.Role.SYSTEM;
import static chat.woowa.woowachat.chat.domain.Role.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Message 은")
class MessageTest {

    @Test
    void 역할별로_정적_팩터리가_존재한다() {
        // when
        final Message 유저 = Message.user("유저", 1);
        final Message 시스템 = Message.system("시스템", 1);
        final Message 어시스턴트 = Message.assistant("어시스턴트", 1);

        // then
        assertAll(
                () -> assertThat(유저.roleName()).isEqualTo(USER.roleName()),
                () -> assertThat(시스템.roleName()).isEqualTo(SYSTEM.roleName()),
                () -> assertThat(어시스턴트.roleName()).isEqualTo(ASSISTANT.roleName())
        );
    }
}
