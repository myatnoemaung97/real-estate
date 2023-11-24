package com.example.Online.Housing.Show.repositories;

import com.example.Online.Housing.Show.models.Housing;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HousingRepository extends JpaRepository<Housing, Long>, QueryByExampleExecutor<Housing> {
    List<Housing> findByOwnerId(Long ownerId);

    /*@Query("SELECT h FROM Housing h WHERE " +
            "(:city is null or h.city = :city) " +
            "and (:township is null or h.township = :township) " +
            "and (:rent is null or h.rent = :rent) " +
            "and (:sale is null or h.sale = :sale)")
    List<Housing> searchHousing(
            @Param("city") String city,
            @Param("township") String township,
            @Param("rent") Boolean rent,
            @Param("sale") Boolean sale);*/

    @Query("SELECT h FROM Housing h WHERE h.city = :city")
    List<Housing> customFindByCity(String city);

    @Modifying
    @Transactional
    @Query("UPDATE Housing h SET h.imageUrl = null WHERE h.id = :id")
    void deleteImage(@Param("id") Long id);
}
