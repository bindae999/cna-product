package com.example.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

import javax.persistence.*;

@Entity
public class Product {

    @Id @GeneratedValue
    Long id;
    String name;
    int stock;

    //저장후에 호출하게 하는 Annotation
    @PostPersist @PostUpdate
    public void onPostPersist(){
        //이벤트 발행
        ProductChanged productChanged = new ProductChanged();
        productChanged.setProductName(this.getName());
        productChanged.setProductId(this.getId());
        productChanged.setProductStock(this.getStock());

        //해당클래스를 json으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        String json  = null;

        try {
            json = objectMapper.writeValueAsString(productChanged);
        }catch (JsonProcessingException e){
            throw new RuntimeException("JSON format excetption", e);
        }
        //System.out.println(json);

        //메시지큐에 Publish
        Processor processor = ProductApplication.applicationContext.getBean(Processor.class);
        MessageChannel outputChannel = processor.output();

        outputChannel.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());


    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
