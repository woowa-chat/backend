package chat.woowa.woowachat.member.application;

import chat.woowa.woowachat.member.domain.Member;
import chat.woowa.woowachat.member.domain.MemberRepository;
import chat.woowa.woowachat.member.dto.SignUpDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void signUp(final SignUpDto signUpDto) {
        final Member member = signUpDto.toDomain();
        memberRepository.findByName(member.name())
                .ifPresentOrElse(
                        saved -> saved.changeCourse(member.course()),
                        () -> memberRepository.save(member)
                );
    }
}
