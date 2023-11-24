package com.example.Online.Housing.Show.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "detailImages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DetailImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private Long housingId;
    private String url;

}
