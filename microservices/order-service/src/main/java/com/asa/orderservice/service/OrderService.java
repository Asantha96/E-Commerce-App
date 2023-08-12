package com.asa.orderservice.service;

import com.asa.orderservice.dto.InventoryResponse;
import com.asa.orderservice.dto.OrderLineItemsDto;
import com.asa.orderservice.dto.OrderRequest;
import com.asa.orderservice.model.Order;
import com.asa.orderservice.model.OrderLineItems;
import com.asa.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient webClient;

    /*public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }*/ //same by the @RequiredArgsConstructor only for final fields

    public void placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList().stream().map(this::mapToDto).toList();
        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList().stream().map(OrderLineItems::getSkuCode).toList();

        //call inventory service and place the order if it is in stock
        InventoryResponse[] inventoryResponseArray = webClient
                .get()
                .uri("http://localhost:8082/api/inventory", uriBuilder -> uriBuilder
                .queryParam("skuCode", skuCodes)
                .build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();//this is an asynchronous, by using block() part it becomes synchronous

        boolean allProductsInStock = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);

        if (allProductsInStock) orderRepository.save(order);
        else throw new IllegalArgumentException("Product is not in stock, please try again later");



    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setQuantity(orderLineItems.getQuantity());
        orderLineItems.setSkuCode(orderLineItems.getSkuCode());
        return orderLineItems;
    }
}
