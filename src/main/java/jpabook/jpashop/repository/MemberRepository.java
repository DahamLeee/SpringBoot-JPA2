package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository  {

    /**
     * EntityManager 는 원래 @Autowired 로 의존성이 주입되지 않는다.
     * "@PersistenceContext" 라는 표준 애노테이션이 있어야 인젝션이 가능함
     * 그런데, Spring Boot 가 @Autowired 로도 인젝션이 가능하도록 지원을 해주는거임.
     *
     * 테스트를 하는데 있어서 무엇을 테스트 하는지 알아야 한다.
     * 현재 우리는 사용자에 관한 기능에 있어서 회원가입, 그리고 중복 처리를 구현했다.
     * 그렇기 때문에 회원가입을 성공해야 하고,
     * 회원가입 할 때 같은 이름이 있다면 예외가 발생해야 한다.
     */

    public void save(Member member) {
        em.persist(member);
    }

    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }

    private final EntityManager em; // EntityManager 를 생성자로 인젝션 한거임.
}
