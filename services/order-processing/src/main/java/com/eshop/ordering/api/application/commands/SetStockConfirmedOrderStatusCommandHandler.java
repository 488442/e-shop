package com.eshop.ordering.api.application.commands;

import an.awesome.pipelinr.Command;
import com.eshop.ordering.domain.aggregatesmodel.order.OrderId;
import com.eshop.ordering.domain.aggregatesmodel.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SetStockConfirmedOrderStatusCommandHandler implements Command.Handler<SetStockConfirmedOrderStatusCommand, Boolean> {
  private final OrderRepository orderRepository;

  /**
   * Handler which processes the command when Stock service confirms the request.
   */
  @SneakyThrows
  @Override
  @Transactional
  public Boolean handle(SetStockConfirmedOrderStatusCommand command) {
    // Simulate a work time for confirming the stock
    Thread.sleep(10000);

    final var orderToUpdate = orderRepository.findById(OrderId.of(command.orderNumber())).orElse(null);
    if (orderToUpdate == null) {
      return false;
    }

    orderToUpdate.setStockConfirmedStatus();
    orderRepository.save(orderToUpdate);
    return true;
  }
}
