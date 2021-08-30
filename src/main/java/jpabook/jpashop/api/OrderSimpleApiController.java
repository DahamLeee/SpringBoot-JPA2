package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * xToOne (ManyToOne, OneToOne)
 * Order
 * Order -> Member(Lazy)
 * Order -> Delivery(Lazy)
 * 1:N 은 기본적으로 Lazy
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        for (Order order : all) {
            order.getMember().getName(); // Lazy 강제 초기화
            order.getDelivery().getAddress(); // Lazy 강제 초기화
        }

        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() { // result 로 한 번 감싸야 함 ResponseEntity<Result<List<SimpleOrderDto>>>
        // ORDER 2개
        // 1 번째 ORDER 돌 때도 QUERY 를 날리고, 2 번째 ORDER 를 돌 때도 QUERY 를 날리는거임 . .. . . .N 번째 ORDER 를 돌 때도 QUERY 를 날리는거임.
        // => 1(처음 Order 를 가지고 오는 쿼리) + N(처음 Order 를 가지고 온 결과 값이 N개임[지금은 2개])
        // => 1 + N(회원) + N(배달)
        return orderRepository.findAllByString(new OrderSearch()).stream() // 이렇게 하는 습관을 들이자, 익숙해지자
                .map(SimpleOrderDto::new) // lambda reference
                .collect(toList());
    }

    /**
     * v2 에서는 one query 에 n 개의 query 가 나가는 1 + n 문제가 발생하여서
     * v3 에서는 fetch join 을 사용함으로서 쿼리를 최적화 하였다.
     * @return
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        List<SimpleOrderDto> result = orders.stream()
                .map(SimpleOrderDto::new)
                .collect(toList());

        return result;
    }

    @GetMapping("/api/v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }

    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address; // Value Object (Not Entity)

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName(); // LAZY 초기화 => 영속성 컨텍스트가 이 멤버(orderId) ID를 가지고 영속성 컨텍스트를 찾아보고 없으면 쿼리를 날림
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress(); // LAZY 초기화 => 똑같이 나감
        }
    }
}
