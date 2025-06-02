package com.budget.leger_service.repository;

import com.budget.leger_service.dto.TransactionFilter;
import com.budget.leger_service.models.Category;
import com.budget.leger_service.models.LedgerEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
public class LedgerRepositoryImpl implements FilterLedgerRepostory{

    private final EntityManager entityManager;

    @Override
    public Page<LedgerEntity> findWithCustomFilter(Long userId, Pageable pageable, TransactionFilter transactionFilter) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<LedgerEntity> mainQuery = cb.createQuery(LedgerEntity.class);
        Root<LedgerEntity> ledgerEntityRoot = mainQuery.from(LedgerEntity.class);

        List<Predicate> predicates = buildPredicates(cb, ledgerEntityRoot, userId, transactionFilter);

        mainQuery.select(ledgerEntityRoot).where(predicates.toArray(new Predicate[0]));

        TypedQuery<LedgerEntity> typedQuery =entityManager.createQuery(mainQuery);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());


        List<LedgerEntity> results = typedQuery.getResultList();

        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<LedgerEntity> countRoot = countQuery.from(LedgerEntity.class);

        countQuery.select(cb.count(countRoot)).where(buildPredicates(cb, countRoot, userId, transactionFilter).toArray(new Predicate[0]));

        Long total = entityManager.createQuery(countQuery).getSingleResult();


        return new PageImpl<>(results, pageable, total);
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root <LedgerEntity> ledgerEntityRoot, Long userId, TransactionFilter transactionFilter){
        List<Predicate> predicates = new ArrayList<>();
        Join<LedgerEntity, Category> categoryJoin = ledgerEntityRoot.join("category", JoinType.LEFT);

        predicates.add(cb.equal(ledgerEntityRoot.get("userId"), userId));


        if(transactionFilter.startDate() != null)
            predicates.add(cb.greaterThanOrEqualTo(ledgerEntityRoot.get("date"), transactionFilter.startDate()));
        if(transactionFilter.endDate() !=null)
            predicates.add(cb.lessThanOrEqualTo(ledgerEntityRoot.get("date"), transactionFilter.endDate()));
        if(transactionFilter.startPrice() != null)
            predicates.add(cb.greaterThanOrEqualTo(ledgerEntityRoot.get("price"), transactionFilter.startPrice()));
        if(transactionFilter.endPrice() != null)
            predicates.add(cb.lessThanOrEqualTo(ledgerEntityRoot.get("price"), transactionFilter.endPrice()));
        if(transactionFilter.CategoryName() !=null)
            predicates.add(cb.equal(categoryJoin.get("name"), transactionFilter.CategoryName()));
        if(transactionFilter.CategoryType() !=null)
            predicates.add(cb.equal(categoryJoin.get("categoryType"), transactionFilter.CategoryType()));

        return predicates;
    }

}
