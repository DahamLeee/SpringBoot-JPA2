package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

// @RunWith(SpringRunner.class) Spring 과 Junit 이 버전업 되면서 생략
@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;


    @Test
    public void 회원가입() throws Exception {
        // given
        Member member = new Member();
        member.setName("Lee");

        // when
        Long saveId = memberService.join(member);

        // then
        em.flush();
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("Lee");

        Member member2 = new Member();
        member2.setName("Lee");

        // when
        IllegalStateException illegalStateException = assertThrows(IllegalStateException.class, () -> {
            memberService.join(member1);
            memberService.join(member2); // 예외가 발생해야 한다!!!
        });

        // then
        assertEquals(IllegalStateException.class, illegalStateException.getClass());
    }
}