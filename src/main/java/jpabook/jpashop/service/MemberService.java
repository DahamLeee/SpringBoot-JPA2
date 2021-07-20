package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor // final 이 있는 필드만 가지고 생성자를 만들어준다.
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원 가입
      */
    @Transactional
    public Long join(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        // EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    @Transactional
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    @Transactional
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    /**
     *
     * @param id
     * @param name
     *  영속성 컨텍스트에서 올린걸 반환 해주고,
     *  영속성 컨텍스트에 있는 Member(영속 상태) 의 name 을 변경해주면
     *  종료되는 시점에 Spring 의 AOP 가 동작하면서 Transaction commit 이 된다.
     *  그때 JPA 가 영속성 컨텍스트 Flush 하고 DB COMMIT 함
     */
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id); // update 를 하는 게 아니라 변경 감지를 이용함 (영속성 상태)
        member.setName(name);
    }
}
