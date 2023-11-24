package com.example.Online.Housing.Show.repositories;

import com.example.Online.Housing.Show.models.Account;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findByUsername(String username);

    @Modifying
    @Query("UPDATE Account a SET a.name = :name, a.email= :email, a.phone = :phone WHERE a.id = :id")
    @Transactional
    void updateAccount(
            @Param("name") String name,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("id") Long id);



}
