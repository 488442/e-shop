package com.eshop.ordering.api.application.commands;

import an.awesome.pipelinr.Command;
import com.eshop.ordering.domain.aggregatesmodel.order.Order;
import com.eshop.ordering.domain.aggregatesmodel.order.OrderId;
import com.eshop.ordering.domain.aggregatesmodel.order.OrderRepository;
import com.eshop.shared.rest.error.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ShipOrderCommandHandler implements Command.Handler<ShipOrderCommand, Boolean> {
  private final OrderRepository orderRepository;

  /**
   * Handler which processes the command when administrator executes ship order from app
   */
  @Transactional
  @Override
  public Boolean handle(ShipOrderCommand command) {
    final var order = findOrder(command.orderNumber());
    order.setShippedStatus();
    orderRepository.save(order);
    return true;
  }

  private Order findOrder(String orderNumber) {
    return orderRepository.findById(OrderId.of(orderNumber))
        .orElseThrow(() -> new NotFoundException("Order %s not found".formatted(orderNumber)));
  }
}
