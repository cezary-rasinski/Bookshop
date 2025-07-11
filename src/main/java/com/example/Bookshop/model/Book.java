package com.example.Bookshop.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {
    @Id
    @Column(nullable = false, unique = true)
    private String id;
    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "NUMERIC(10,2)", nullable = false)
    private double price;
    private String author;
    private int quantity;

    @Builder.Default
    private boolean isActive = true;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> attributes = new HashMap<>();
    public Object getAttributes(String key){
        return attributes.get(key);
    }
    public void addAttribute(String key, Object value){
        attributes.put(key,value);
    }
    public void removeAttribute(String key){
        attributes.remove(key);
    }
}
