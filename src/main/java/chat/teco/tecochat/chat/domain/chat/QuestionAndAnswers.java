package chat.teco.tecochat.chat.domain.chat;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class QuestionAndAnswers {

    @OneToMany(fetch = LAZY, cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "chat_id")
    private List<QuestionAndAnswer> questionAndAnswers = new ArrayList<>();

    protected QuestionAndAnswers() {
    }

    public QuestionAndAnswers(final QuestionAndAnswer... questionAndAnswers) {
        this(Arrays.asList(questionAndAnswers));
    }

    public QuestionAndAnswers(final List<QuestionAndAnswer> questionAndAnswers) {
        this.questionAndAnswers.addAll(questionAndAnswers);
    }

    public void add(final QuestionAndAnswer questionAndAnswer) {
        questionAndAnswers.add(questionAndAnswer);
    }

    public QuestionAndAnswers last3QuestionAndAnswers() {
        int size = questionAndAnswers.size();
        if (size < 3) {
            return this;
        }
        return new QuestionAndAnswers(questionAndAnswers.subList(size - 3, size));
    }

    public List<Message> messagesWithSettingMessage(final SettingMessage settingMessage) {
        final List<Message> result = new ArrayList<>();
        result.add(settingMessage);
        for (final QuestionAndAnswer qna : questionAndAnswers) {
            result.add(qna.question());
            result.add(qna.answer());
        }
        return result;
    }

    public List<QuestionAndAnswer> questionAndAnswers() {
        return new ArrayList<>(questionAndAnswers);
    }
}
