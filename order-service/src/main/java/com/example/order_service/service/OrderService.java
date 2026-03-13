package com.example.order_service.service;

import com.example.order_service.dto.ProductResponse;
import com.example.order_service.model.Order;
import com.example.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    @Value("${product-service.url}")
    private String productServiceUrl;

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Order createOrder(Long productId, Integer quantity) {
        String url = productServiceUrl + "/api/products/" + productId;
        ProductResponse product = restTemplate.getForObject(url, ProductResponse.class);

        if (product == null) {
            throw new RuntimeException("Producto no encontrado con id: " + productId);
        }

        if (product.getStock() < quantity) {
            throw new RuntimeException("Stock insuficiente. Stock disponible: " + product.getStock());
        }

        double totalPrice = product.getPrice() * quantity;

        Order order = new Order();
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus("CREATED");

        return orderRepository.save(order);
    }

    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}
