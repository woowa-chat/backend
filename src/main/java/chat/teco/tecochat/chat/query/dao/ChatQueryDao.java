package chat.teco.tecochat.chat.query.dao;

import static chat.teco.tecochat.chat.domain.chat.QChat.chat;
import static chat.teco.tecochat.member.domain.QMember.member;
import static org.springframework.util.StringUtils.hasText;

import chat.teco.tecochat.chat.domain.chat.Chat;
import chat.teco.tecochat.common.entity.BaseEntity;
import chat.teco.tecochat.member.domain.Course;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ChatQueryDao {

    private final JPAQueryFactory query;

    public ChatQueryDao(final JPAQueryFactory query) {
        this.query = query;
    }

    public Page<Chat> search(final ChatSearchCond cond, final Pageable pageable) {
        final List<Long> longs = nameAndCourseMatchMemberIds(cond);
        final List<Chat> contents = query.selectFrom(chat)
                .where(
                        titleLikeIgnoreCase(cond.title),
                        chat.memberId.in(longs)
                ).orderBy(chat.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = query.select(chat.count()).from(chat);

        return PageableExecutionUtils.getPage(contents, pageable, countQuery::fetchOne);
    }

    /* 이름과 코스에 해당하는 회원 id 조회 */
    private List<Long> nameAndCourseMatchMemberIds(final ChatSearchCond cond) {
        return query.selectFrom(member)
                .where(
                        nameLikeIgnoreCase(cond.name),
                        courseEquals(cond.course)).fetch()
                .stream()
                .map(BaseEntity::id)
                .toList();
    }

    private BooleanExpression nameLikeIgnoreCase(final String name) {
        return hasText(name) ? member.name.likeIgnoreCase("%" + name.strip() + "%") : null;
    }

    private BooleanExpression courseEquals(final Course course) {
        return (course == null) ? null : member.course.eq(course);
    }

    private BooleanExpression titleLikeIgnoreCase(final String title) {
        return hasText(title) ? chat.title.likeIgnoreCase("%" + title.strip() + "%") : null;
    }

    public record ChatSearchCond(
            String name,
            String title,
            Course course
    ) {
    }
}