package com.budget.leger_service.repository;

import com.budget.leger_service.dto.CategorySumDto;
import com.budget.leger_service.models.Category;
import com.budget.leger_service.models.LedgerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntity, Long>, FilterLedgerRepostory {
    Optional<LedgerEntity> findById(Long id);
    Page<LedgerEntity> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT SUM(l.price) FROM LedgerEntity l JOIN l.category c WHERE l.userId = :userId AND c.categoryType = 'INCOME' AND EXTRACT(YEAR FROM l.date) = :year AND EXTRACT(MONTH FROM l.date) = :month")
    Optional<BigDecimal> sumIncomeForMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(l.price) FROM LedgerEntity l JOIN l.category c WHERE l.userId = :userId AND c.categoryType = 'EXPENSES' AND EXTRACT(YEAR FROM l.date) = :year AND EXTRACT(MONTH FROM l.date) = :month")
    Optional<BigDecimal> sumExpenseForMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT MAX(l.price) FROM LedgerEntity l JOIN l.category c WHERE l.userId = :userId AND c.categoryType = 'INCOME' AND EXTRACT(YEAR FROM l.date) = :year AND EXTRACT(MONTH FROM l.date) = :month")
    Optional<BigDecimal> findBiggestIncome(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT MAX(l.price) FROM LedgerEntity l JOIN l.category c WHERE l.userId = :userId AND c.categoryType = 'EXPENSES' AND EXTRACT(YEAR FROM l.date) = :year AND EXTRACT(MONTH FROM l.date) = :month")
    Optional<BigDecimal> findBiggestExpense(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);

    @Query("SELECT new com.budget.leger_service.dto.CategorySumDto(c.name, SUM(l.price), c.categoryType) " +
            "FROM LedgerEntity l " +
            "JOIN l.category c " +
            "WHERE l.userId = :userId AND EXTRACT(YEAR FROM l.date) = :year AND EXTRACT(MONTH FROM l.date) = :month " +
            "GROUP BY c.id, c.name, c.categoryType")
    List<CategorySumDto> findCategorySumsForMonth(@Param("userId") Long userId,
                                                  @Param("year") int year,
                                                  @Param("month") int month);


//    @Query("SELECT MIN(YEAR(l.date)) FROM Ledger l WHERE l.user.id = :user_Id")
//    Optional<Integer> findFirstTransactionYearByUserId(@Param("userId") Long userId);
//
//    @Query("SELECT l FROM Ledger l WHERE l.user.id = :user_Id AND EXTRACT(YEAR FROM l.date) = :year AND EXTRACT(MONTH FROM l.date) = :month ORDER BY l.date DESC LIMIT 1")
//    Optional<LedgerEntity> findLastByUser_IdAndYearAndMonthOrderByDateDesc(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
//
//    @Query("SELECT l FROM Ledger l JOIN l.category c WHERE l.user.id = :user_Id AND EXTRACT(YEAR FROM l.date) = :year AND EXTRACT(MONTH FROM l.date) = :month")
//    List<LedgerEntity> findAllByUserIdAndYearAndMonth(@Param("userId") Long userId, @Param("year") int year, @Param("month") int month);
}
