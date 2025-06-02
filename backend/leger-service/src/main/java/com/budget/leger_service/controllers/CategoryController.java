package com.budget.leger_service.controllers;


import com.budget.leger_service.models.Category;
import com.budget.leger_service.services.LedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/category")
public class CategoryController {

    private LedgerService ledgerService;

    @Autowired
    public CategoryController(LedgerService ledgerService) {
        this.ledgerService = ledgerService;
    }

    @GetMapping("/getallcategory")
    public ResponseEntity<List<Category>> getAllCategory(){

        List<Category> categoryList= ledgerService.findAllCategories();
        return new ResponseEntity<>(categoryList, HttpStatus.OK);
    }
}
