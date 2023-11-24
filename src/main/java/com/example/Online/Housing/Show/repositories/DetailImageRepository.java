package com.example.Online.Housing.Show.repositories;

import com.example.Online.Housing.Show.models.DetailImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailImageRepository extends JpaRepository<DetailImage, Long> {

    List<DetailImage> findByHousingId(Long housingId);
}
