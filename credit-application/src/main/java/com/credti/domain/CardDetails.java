package com.credti.domain;

import java.util.Date;

import com.credti.constants.CardStatus;
/**Pojo for CardDetails which is composite in card*/
public class CardDetails {

	private Date validFrom;
	private Date validUpto;

	private double creditLimit;
	private double cashLimit;
	private CardStatus status = CardStatus.ACTIVE;

	public CardDetails(Date validFrom, Date validUpto, double creditLimit, double cashLimit, CardStatus status) {
		this.validFrom = validFrom;
		this.validUpto = validUpto;
		this.creditLimit = creditLimit;
		this.cashLimit = cashLimit;
		this.status = status;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		this.validFrom = validFrom;
	}

	public Date getValidUpto() {
		return validUpto;
	}

	public void setValidUpto(Date validUpto) {
		this.validUpto = validUpto;
	}

	public double getCreditLimit() {
		return creditLimit;
	}

	public void setCreditLimit(double creditLimit) {
		this.creditLimit = creditLimit;
	}

	public double getCashLimit() {
		return cashLimit;
	}

	public void setCashLimit(double cashLimit) {
		this.cashLimit = cashLimit;
	}

	public CardStatus getStatus() {
		return status;
	}

	public void setStatus(CardStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return System.lineSeparator() + "CardDetails ***************" + System.lineSeparator() + "ValidFrom= " + validFrom
				+ System.lineSeparator() + "ValidUpto= " + validUpto + System.lineSeparator() + "\nCreditLimit= Rs."
				+ creditLimit + ", CashLimit= Rs." + cashLimit + ", Status=" + status + System.lineSeparator();
	}

}
