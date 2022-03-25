package jpabook.jpashop.domain;

import lombok.*;
import org.aspectj.weaver.ast.Or;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;

@Entity
@Table(name = "Orders")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)  // OrderItem 의 필드 order 에 의해 매핑됨
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate; // 주문 시간

    @Enumerated(EnumType.STRING)
    private OrderStatus status;  // 주문 상태 [order,cancel]


    //    여기서부터는 연관관계 편의 메서드
    public void setMember(Member member){
        this.member = member;
        member.getOrders().add(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    // ==== 생성 메서드 ====
    // 밖에서 set 을 통해 값을 세팅하는게 아닌, 맨처음 주문을 생성할때부터 전부 값을 생성을 해야함.
    // 여기서 모든 주문을 완결
    public static Order createOrder(Member member, Delivery delivery, OrderItem... orderItems){
        Order order = new Order();
        order.setMember(member);
        order.setDelivery(delivery);
        for(OrderItem orderItem: orderItems){
            order.addOrderItem(orderItem);
        } // 여기까지 order에 멤버, 주문, 주문한 아이템 넣음
        order.setStatus(OrderStatus.ORDER);             // order 하면 주문 상태를 ORDER 를 디폴트로 가짐
        order.setOrderDate(LocalDateTime.now());        // 주문 시각은 현재로
        return order;
    }

    // === 비즈니스 로직 ===
    /**
     * 주문 취소
     */
    public void cancel(){
        if (delivery.getDeliveryStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송이 시작된 상품은 주문 취소가 불가능합니다.");
        }   // 배송 상태 점검
        this.setStatus(OrderStatus.CANCEL);
        for (OrderItem orderItem : this.orderItems) {
            orderItem.cancel();         // 주문한 상품 돌면서 모두 캔슬하기
        }
    }

    // === 조회 로직 ===


    /**
     * 전체 주문 가격 조회
     */
    public int getTotalPrice(){
        int totalPrice = 0;
        for (OrderItem orderItem : this.orderItems) {       // A책 3권, B책 5권이면 각각의 주문상품 A,B에 대해 루프돌면서 더함
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;

//        return orderItems.stream()
//        .mapToInt(OrderItem::getTotalPrice)
//        .sum();           얘랑 같은 로직 (자바8 문법 잘 사용)



        }


}
