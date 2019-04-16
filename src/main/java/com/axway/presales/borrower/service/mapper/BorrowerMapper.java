package com.axway.presales.borrower.service.mapper;

import com.axway.presales.borrower.domain.*;
import com.axway.presales.borrower.service.dto.BorrowerDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Borrower and its DTO BorrowerDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface BorrowerMapper extends EntityMapper<BorrowerDTO, Borrower> {



    default Borrower fromId(Long id) {
        if (id == null) {
            return null;
        }
        Borrower borrower = new Borrower();
        borrower.setId(id);
        return borrower;
    }
}
