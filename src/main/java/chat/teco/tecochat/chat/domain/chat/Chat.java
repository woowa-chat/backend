package chat.teco.tecochat.chat.domain.chat;

import static chat.teco.tecochat.chat.exception.chat.ChatExceptionType.NO_AUTHORITY_CHANGE_TITLE;
import static jakarta.persistence.EnumType.STRING;

import chat.teco.tecochat.chat.exception.chat.ChatException;
import chat.teco.tecochat.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Chat extends BaseEntity {

    public static final int FREE_TOKEN = 2000;

    @Enumerated(STRING)
    @Column(nullable = false)
    private GptModel model;

    @Enumerated(STRING)
    private SettingMessage settingMessage;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Long memberId;

    private int likeCount;

    @Embedded
    private QuestionAndAnswers questionAndAnswers = new QuestionAndAnswers();

    protected Chat() {
    }

    public Chat(Long id,
                GptModel model,
                SettingMessage settingMessage,
                String title,
                Long memberId) {
        super(id);
        this.model = model;
        this.settingMessage = settingMessage;
        this.title = title;
        this.memberId = memberId;
    }

    public Chat(final GptModel model,
                final SettingMessage settingMessage,
                final String title,
                final Long memberId) {
        this.model = model;
        this.settingMessage = settingMessage;
        this.title = title;
        this.memberId = memberId;
    }

    public void addQuestionAndAnswer(final QuestionAndAnswer questionAndAnswer) {
        this.questionAndAnswers.add(questionAndAnswer);
    }

    /**
     * [모델의 최대 토큰 - FREE_TOKEN(2000)] 반환
     */
    public QuestionAndAnswers qnaWithFreeToken() {
        return questionAndAnswers.lessOrEqualThan(model.maxTokens() - FREE_TOKEN);
    }

    public void decreaseLike() {
        likeCount--;
    }

    public void increaseLike() {
        likeCount++;
    }

    public void changeTitle(Long memberId, String title) {
        if (!this.memberId().equals(memberId)) {
            throw new ChatException(NO_AUTHORITY_CHANGE_TITLE);
        }
        this.title = title;
    }

    public String modelName() {
        return model.modelName();
    }

    public String title() {
        return title;
    }

    public SettingMessage settingMessage() {
        return settingMessage;
    }

    public Long memberId() {
        return memberId;
    }

    public List<QuestionAndAnswer> questionAndAnswers() {
        return new ArrayList<>(questionAndAnswers.questionAndAnswers());
    }

    public int likeCount() {
        return likeCount;
    }
}
