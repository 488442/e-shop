package com.eshop.ordering.api.application.integrationevents.eventhandling;

import an.awesome.pipelinr.Pipeline;
import com.eshop.ordering.api.application.commands.SetStockConfirmedOrderStatusCommand;
import com.eshop.ordering.api.application.integrationevents.events.OrderStockConfirmedIntegrationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderStockConfirmedIntegrationEventHandler {
  private final Pipeline pipeline;

  @KafkaListener(groupId = "catalogGroup", topics = "${spring.kafka.consumer.topic.catalog}")
  public void handle(OrderStockConfirmedIntegrationEvent event) {
    System.out.printf("----- Handling integration event: {%s} - (%s})", event.getId(), event.getClass().getSimpleName());
    var command = new SetStockConfirmedOrderStatusCommand(event.getOrderId());
    command.execute(pipeline);
  }
}
