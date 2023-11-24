package com.example.Online.Housing.Show.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class HousingWithOwnerInfo {

    private Long id;

    private String City;
    private String Township;
    private double width;
    private double length;
    private String type;
    private int floor;
    private int master;
    private int single;
    private BigDecimal rentFee;
    private BigDecimal salePrice;
    private String imageUrl;
    private String parking;
    private String ownerName;
    private String ownerEmail;
    private String ownerPhone;

}
