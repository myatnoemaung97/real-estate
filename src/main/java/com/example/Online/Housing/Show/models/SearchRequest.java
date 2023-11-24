package com.example.Online.Housing.Show.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SearchRequest {

    private String city;
    private String township;
    private Double width;
    private Double length;
    private String type;
    /*private Integer floor;
    private Integer master;
    private Integer single;*/
    private BigDecimal rentFee;
    private BigDecimal salePrice;
    private String parking;
}
