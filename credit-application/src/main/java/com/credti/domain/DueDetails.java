package com.credti.domain;

import com.credti.constants.DueStatus;

/**Pojo for DueDetails which is composite in card*/
public class DueDetails {

	private byte pendingDueCount;

	private DueStatus dueStatus = DueStatus.NA;

	private double oldCashDue;
	private double oldCreditDue;

	private double currCashDue;
	private double currCreditDue;

	private double interest;
	private double lateFee;

	public byte getPendingDueCount() {
		return pendingDueCount;
	}

	public void setPendingDueCount(byte pendingDueCount) {
		this.pendingDueCount = pendingDueCount;
	}

	public DueStatus getDueStatus() {
		return dueStatus;
	}

	public void setDueStatus(DueStatus dueStatus) {
		this.dueStatus = dueStatus;
	}

	public double getOldCashDue() {
		return oldCashDue;
	}

	public void setOldCashDue(double oldCashDue) {
		this.oldCashDue = oldCashDue;
	}

	public double getOldCreditDue() {
		return oldCreditDue;
	}

	public void setOldCreditDue(double oldCreditDue) {
		this.oldCreditDue = oldCreditDue;
	}

	public double getCurrCashDue() {
		return currCashDue;
	}

	public void setCurrCashDue(double currCashDue) {
		this.currCashDue = currCashDue;
	}

	public double getCurrCreditDue() {
		return currCreditDue;
	}

	public void setCurrCreditDue(double currCreditDue) {
		this.currCreditDue = currCreditDue;
	}

	public double getInterest() {
		return interest;
	}

	public void setInterest(double interest) {
		this.interest = interest;
	}

	public double getLateFee() {
		return lateFee;
	}

	public void setLateFee(double lateFee) {
		this.lateFee = lateFee;
	}

	@Override
	public String toString() {
		return System.lineSeparator() + "DueDetails ***************" + System.lineSeparator() + "PendingDueCount= " + pendingDueCount
				+ ", DueStatus= " + dueStatus + System.lineSeparator() + "OldCashDue= Rs." + oldCashDue
				+ ", OldCreditDue= Rs." + oldCreditDue + System.lineSeparator() + "CurrCashDue= Rs." + currCashDue
				+ ", CurrCreditDue= Rs." + currCreditDue + System.lineSeparator() + "Interest= Rs." + interest + ", LateFee= Rs."
				+ lateFee + System.lineSeparator();
	}

}
