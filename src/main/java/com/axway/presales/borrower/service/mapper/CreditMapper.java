package com.axway.presales.borrower.service.mapper;

import com.axway.presales.borrower.domain.*;
import com.axway.presales.borrower.service.dto.CreditDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Credit and its DTO CreditDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface CreditMapper extends EntityMapper<CreditDTO, Credit> {



    default Credit fromId(Long id) {
        if (id == null) {
            return null;
        }
        Credit credit = new Credit();
        credit.setId(id);
        return credit;
    }
}
