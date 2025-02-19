package com.swiggy.walletapp.mapper;

import com.swiggy.walletapp.dto.TransactionDto;
import com.swiggy.walletapp.entity.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TransactionMapper {

    List<TransactionDto> toDtoList(List<Transaction> transactions);
}
