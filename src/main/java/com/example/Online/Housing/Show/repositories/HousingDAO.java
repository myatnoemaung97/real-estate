package com.example.Online.Housing.Show.repositories;

import com.example.Online.Housing.Show.models.Housing;
import com.example.Online.Housing.Show.models.SearchRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class HousingDAO {

    private final EntityManager entityManager;

    public List<Housing> findHousingsWithCriteria(SearchRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Housing> criteriaQuery = criteriaBuilder.createQuery(Housing.class);
        Root<Housing> root = criteriaQuery.from(Housing.class);

        List<Predicate> predicates = getPredicates(request, criteriaBuilder, root);

        criteriaQuery.where(
                criteriaBuilder.and(predicates.toArray(new Predicate[0]))
        );
        TypedQuery<Housing> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }

    private List<Predicate> getPredicates(
            SearchRequest request,
            CriteriaBuilder criteriaBuilder,
            Root<Housing> root
    ) {
        List<Predicate> predicates = new ArrayList<>();

        if (request.getCity() != null) {
            Predicate cityPredicate = criteriaBuilder
                    .like(root.get("city"), "%" + request.getCity() + "%");
            predicates.add(cityPredicate);
        }
        if (request.getTownship() != null) {
            Predicate townshipPredicate = criteriaBuilder
                    .like(root.get("township"), "%" + request.getTownship() + "%");
            predicates.add(townshipPredicate);
        }
        if (request.getType() != null) {
            Predicate typePredicate = criteriaBuilder
                    .equal(root.get("type"), request.getType());
            predicates.add(typePredicate);
        }
        /*if (request.getLength() != null) {
            Predicate lenghtPredicate = criteriaBuilder
                    .lessThanOrEqualTo(root.get("length"), request.getLength());
            predicates.add(lenghtPredicate);
        }
        if (request.getWidth() != null) {
            Predicate widthPredicate = criteriaBuilder
                    .lessThanOrEqualTo(root.get("width"), request.getWidth());
            predicates.add(widthPredicate);
        }*/

        if (request.getSalePrice() != null) {
            Predicate salePredicate = criteriaBuilder
                    .lessThanOrEqualTo(root.get("salePrice"), request.getSalePrice());
            predicates.add(salePredicate);
        }
        if (request.getRentFee() != null) {
            Predicate rentPredicate = criteriaBuilder
                    .lessThanOrEqualTo(root.get("rentFee"), request.getRentFee());
            predicates.add(rentPredicate);
        }
        if (request.getParking() != null) {
            Predicate parkingPredicate = criteriaBuilder
                    .equal(root.get("parking"), request.getParking());
            predicates.add(parkingPredicate);
        }
        return predicates;
    }
}
