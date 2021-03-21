package com.eshop.ordering.api.application.domaineventhandlers.orderstockconfirmed;

import com.eshop.ordering.api.application.domaineventhandlers.DomainEventHandler;
import com.eshop.ordering.api.application.integrationevents.OrderingIntegrationEventService;
import com.eshop.ordering.api.application.integrationevents.events.OrderStatusChangedToStockConfirmedIntegrationEvent;
import com.eshop.ordering.domain.aggregatesmodel.buyer.BuyerRepository;
import com.eshop.ordering.domain.aggregatesmodel.order.OrderRepository;
import com.eshop.ordering.domain.aggregatesmodel.order.OrderStatus;
import com.eshop.ordering.domain.events.OrderStatusChangedToStockConfirmedDomainEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStatusChangedToStockConfirmedDomainEventHandler implements DomainEventHandler<OrderStatusChangedToStockConfirmedDomainEvent> {
  private static final Logger logger = LoggerFactory.getLogger(OrderStatusChangedToStockConfirmedDomainEventHandler.class);

  private final OrderRepository orderRepository;
  private final BuyerRepository buyerRepository;
  private final OrderingIntegrationEventService orderingIntegrationEventService;
  @Value("${spring.kafka.consumer.topic.stockConfirmed}")
  private String stockConfirmedTopic;

  @EventListener
  public void handle(OrderStatusChangedToStockConfirmedDomainEvent event) {
    logger.info(
        "Order with Id: {} has been successfully updated to status {} ({})",
        event.orderId(),
        OrderStatus.StockConfirmed,
        OrderStatus.StockConfirmed.getId()
    );

    var order = orderRepository.findById(event.orderId()).orElse(null);
    var buyer = buyerRepository.findById(order.getBuyerId()).orElse(null);

    var orderStatusChangedToStockConfirmedIntegrationEvent = new OrderStatusChangedToStockConfirmedIntegrationEvent(
        order.getId(), order.getOrderStatus().getName(), buyer.getName());
    orderingIntegrationEventService.addAndSaveEvent(stockConfirmedTopic, orderStatusChangedToStockConfirmedIntegrationEvent);
  }
}
