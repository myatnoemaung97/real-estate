package com.example.Online.Housing.Show.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "housing")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Housing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String township;
    private Double width;
    private Double length;
    private String type;
    private Integer floor;
    private Integer master;
    private Integer single;
    private BigDecimal rentFee;
    private BigDecimal salePrice;
    private Long ownerId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private String imageUrl;
    private String parking;

}
