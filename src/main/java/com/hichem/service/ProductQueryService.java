package com.hichem.service;

import com.hichem.dto.ProductEvent;
import com.hichem.entity.Product;
import com.hichem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductQueryService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProducts(){
        return productRepository.findAll();
    }


    @KafkaListener(topics = "product-event-topic", groupId = "product-event-group")
    public void processProductEvent(ProductEvent productEvent){
        if(productEvent.getEventType().equals("createdProduct")){
            productRepository.save(productEvent.getProduct());
        }
        else if(productEvent.getEventType().equals("updatedProduct")){
            Product existantProduct = productRepository.findById(productEvent.getProduct().getId()).get();
            existantProduct.setDescription(productEvent.getProduct().getDescription());
            existantProduct.setPrice(productEvent.getProduct().getPrice());
            existantProduct.setName(productEvent.getProduct().getName());
            productRepository.save(existantProduct);
        }
    }
}
