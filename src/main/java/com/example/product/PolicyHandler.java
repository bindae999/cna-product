package com.example.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PolicyHandler {

    @StreamListener(Processor.INPUT)
    public void onEventByString(@Payload String productChanged){
        System.out.println(productChanged);
    }

//    @StreamListener(Processor.INPUT)
//    public void onEventByObject(@Payload ProductChanged productChanged){
//        if("ProductChanged".equals(productChanged.getEventType())){
//            System.out.println("onEventByObject getEventType : " + productChanged.getEventType());
//            System.out.println("onEventByObject getProductName : " + productChanged.getProductName());
//        }
//    }

    @Autowired
    ProductRepository productRepository;

    @StreamListener(Processor.INPUT)
    public void onEventByObject(@Payload OrderPlaced orderPlaced){

        //주문이 생성되었을때만..
        if("OrderPlaced".equals(orderPlaced.getEventType())){

            System.out.println("onEventByObject getEventType : " + orderPlaced.getEventType());
            System.out.println("onEventByObject getProductName : " + orderPlaced.getProductName());
            System.out.println("onEventByObject getProductId : " + orderPlaced.getProductId());

            //저장
//            Product p = new Product();
//            p.setId(orderPlaced.getProductId());
//            p.setStock(orderPlaced.getProductStock());
//            productRepository.save(p);

            //재고량 변경
            Optional<Product> productById = productRepository.findById(orderPlaced.getProductId());
            Product p = productById.get();
            p.setStock(p.getStock()-orderPlaced.getQty());
            productRepository.save(p);

        }
    }


}
