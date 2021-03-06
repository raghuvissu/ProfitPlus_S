package org.mifosplatform.portfolio.contract.domain;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.springframework.data.jpa.domain.AbstractPersistable;

@Entity
@Table(name = "b_contract_period",uniqueConstraints ={ @UniqueConstraint(name = "contract_period_key", columnNames = { "contract_period" }),
		@UniqueConstraint(name="dur_uni_code",columnNames = {"contract_duration","contract_type"}) })
public class Contract extends AbstractPersistable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "contract_period", nullable = false)
	private String subscriptionPeriod;

	@Column(name = "is_deleted", nullable = false)
	private char deleted = 'N';

	@Column(name = "contract_type", length = 100)
	private String subscriptionType;

	@Column(name = "contract_duration")
	private Long units;

	public Contract() {
	}

	public Contract(final String subscriptionPeriod, final Long units,
			final String subscriptionType) {

		this.subscriptionPeriod = subscriptionPeriod;
		this.subscriptionType = subscriptionType;
		this.units = units;

	}

	public String getSubscriptionPeriod() {
		return subscriptionPeriod;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public Long getUnits() {
		return units;
	}

	

	public void delete() {
		    this.subscriptionPeriod=this.getId()+"_DEL_"+this.subscriptionPeriod;
		    this.subscriptionType=this.getId()+"_DEL_"+this.subscriptionType;
			this.deleted = 'Y';
	}
	public static Contract fromJson(final JsonCommand command) {
	    final String subscriptionPeriod = command.stringValueOfParameterNamed("subscriptionPeriod");
	    final String subscriptionType = command.stringValueOfParameterNamed("subscriptionType");
	    final Long units = command.longValueOfParameterNamed("units");
	    return new Contract(subscriptionPeriod,units,subscriptionType);
	}

	public Map<String, Object> update(final JsonCommand command) {
		 final Map<String, Object> actualChanges = new LinkedHashMap<String, Object>(1);
		  final String subscriptionPeriod = "subscriptionPeriod";
	        if (command.isChangeInStringParameterNamed(subscriptionPeriod, this.subscriptionPeriod)) {
	            final String newValue = command.stringValueOfParameterNamed(subscriptionPeriod);
	            actualChanges.put(subscriptionPeriod, newValue);
	            this.subscriptionPeriod = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        
	        final String subscriptionType = "subscriptionType";
	        if (command.isChangeInStringParameterNamed(subscriptionType, this.subscriptionType)) {
	            final String newValue = command.stringValueOfParameterNamed(subscriptionType);
	            actualChanges.put(subscriptionType, newValue);
	            this.subscriptionType = StringUtils.defaultIfEmpty(newValue, null);
	        }
	        final String units = "units";
			if (command.isChangeInLongParameterNamed(units,this.units)) {
				final Long newValue = command
						.longValueOfParameterNamed(units);
				actualChanges.put(units, newValue);
				this.units=newValue;
			}
	        
	        return actualChanges;
	}

}
