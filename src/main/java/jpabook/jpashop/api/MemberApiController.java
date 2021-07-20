package jpabook.jpashop.api;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller, @ResponseBody 를 합쳐 놓은 것임.
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<Member> membersV1() {
        return memberService.findMembers();
    }

    @GetMapping("/api/v2/members")
    public Result<List<MemberDto>> memberV2() {
        List<Member> findMembers = memberService.findMembers();

        List<MemberDto> collect = findMembers.stream() // 이런 구문은 꼭 숙지하자 (이거 매핑하는거임) Member -> MemberDto
                .map(m -> new MemberDto(m.getName()))
                .collect(Collectors.toList());

        return new Result<>(collect);
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);

        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request) {

        /*
            밑에 update 함수에서 Member 를 return 하게끔 할 수 있지만 그렇게 되면 command (update 하는 것과) query (find one) 이 공존하는 거임
            그렇기 때문에 그냥 command(update 하는 것)는 그냥 그 기능만 하게 끔 수행하고
            해당 id를 통해 수정된 member 를 PK를 통해서(query) 그냥 찾자. 어차피 PK를 통해서 찾는 것은 그렇게 큰 트래픽을 유발하지는 않는다.
         */
        memberService.update(id, request.getName()); // command
        // Transaction 이 완벽하게 끝남
        Member findMember = memberService.findOne(id); // query 를 분리한다.

        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    /*
        Entity 에는 lombok annotation 을 최대한 자제
     */

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data
    @AllArgsConstructor
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        private String name;
    }

    @Data
    static class CreateMemberResponse {
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

}
