package org.mifosplatform.finance.chargeorder.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargeRepository
		extends JpaRepository<Charge, Long>, JpaSpecificationExecutor<Charge> {

}
