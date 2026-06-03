package com.fitway.fitmatch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "body_parts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyPart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // למשל: "בטן", "גב", "רגליים", "ידיים"
}
