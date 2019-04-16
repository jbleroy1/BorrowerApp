package com.axway.presales.borrower.repository;

import com.axway.presales.borrower.domain.Borrower;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Borrower entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BorrowerRepository extends JpaRepository<Borrower, Long> {

}
