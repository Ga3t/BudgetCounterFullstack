package com.budget.leger_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class LedgerResponse {

    List<InfoLedgerDTO> infoLedgerDTO;
    int pageNo;
    int pageSize;
    private Long totalElements;
    private int totalPages;
    boolean isLast;
}
