package labcqrs.domain;

import javax.persistence.*;
import labcqrs.DeliveryApplication;
import labcqrs.domain.DeliveryStarted;
import lombok.Data;

@Entity
@Table(name = "Delivery_table")
@Data
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String address;
    private String customerId;
    private Integer quantity;
    private Long orderId;
    private String status;

    @PostPersist
    public void onPostPersist() {
        DeliveryStarted deliveryStarted = new DeliveryStarted(this);
        deliveryStarted.setStatus(DeliveryStarted.class.getSimpleName());
        deliveryStarted.publishAfterCommit();
    }

    public static DeliveryRepository repository() {
        DeliveryRepository deliveryRepository = DeliveryApplication.applicationContext.getBean(
            DeliveryRepository.class
        );
        return deliveryRepository;
    }

    public static void addToDeliveryList(OrderPlaced orderPlaced) {
        Delivery delivery = new Delivery();
        delivery.setAddress(orderPlaced.getAddress());
        delivery.setQuantity(orderPlaced.getQty());
        delivery.setCustomerId(orderPlaced.getCustomerId());
        delivery.setOrderId(orderPlaced.getId());
        repository().save(delivery);

        DeliveryStarted deliveryStarted  = new DeliveryStarted();
        deliveryStarted.publishAfterCommit();
    }

}