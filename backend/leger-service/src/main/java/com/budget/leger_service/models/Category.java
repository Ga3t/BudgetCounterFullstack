package com.budget.leger_service.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name="CATEGORY")
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ID")
	private Long id;
	
	@Column(name="NAME", nullable = false)
	private String name;
	
	@Enumerated(value=EnumType.STRING)
	@Column(name = "CATEGORY_TYPE", nullable = false)
	CategoryType categoryType;
	
	
	@OneToMany(mappedBy = "category", cascade=CascadeType.ALL, orphanRemoval = true )
	@JsonIgnore
	private List<LedgerEntity> ledger;


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public CategoryType getCategoryType() {
		return categoryType;
	}


	public void setCategoryType(CategoryType categoryType) {
		this.categoryType = categoryType;
	}


	public List<LedgerEntity> getLedger() {
		return ledger;
	}


	public void setLedger(List<LedgerEntity> ledger) {
		this.ledger = ledger;
	}
	
}
