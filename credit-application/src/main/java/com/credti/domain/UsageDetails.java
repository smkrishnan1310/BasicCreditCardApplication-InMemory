package com.credti.domain;

/**Pojo for UsageDetails which is composite in card*/
public class UsageDetails {

	private double totCashUsage;
	private double totCreditUsage;

	public double getTotCashUsage() {
		return totCashUsage;
	}

	public void setTotCashUsage(double totCashUsage) {
		this.totCashUsage = totCashUsage;
	}

	public double getTotCreditUsage() {
		return totCreditUsage;
	}

	public void setTotCreditUsage(double totCreditUsage) {
		this.totCreditUsage = totCreditUsage;
	}

	@Override
	public String toString() {
		return System.lineSeparator() + "UsageDetails ***************\nTotal CashUsage= Rs." + totCashUsage + ", Total CreditUsage= Rs."
				+ totCreditUsage + System.lineSeparator();
	}

}
