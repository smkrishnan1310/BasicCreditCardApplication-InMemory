package com.credti.domain;

import com.credti.Card;
import com.credti.common.IdGenerator;

/**
 * Pojo for Card
 * 
 * card has CustomerDetails, Usage Details, Due Details, Due Details and its own
 */
public class CreditCard implements Card {

	private final long id;

	private final CardDetails cardDetails;

	private final CustomerDetails customerDetails;

	private final UsageDetails usageDetails;

	private final DueDetails dueDetails;

	private double excessAmt;

	public CreditCard(CardDetails cardDetails, CustomerDetails customerDetails) {
		this.id = IdGenerator.getCardId();
		this.cardDetails = cardDetails;
		this.customerDetails = customerDetails;
		this.usageDetails = new UsageDetails();
		this.dueDetails = new DueDetails();
	}

	public long getId() {
		return id;
	}

	public CardDetails getCardDetails() {
		return cardDetails;
	}

	public CustomerDetails getCustomerDetails() {
		return customerDetails;
	}

	public UsageDetails getUsageDetails() {
		return usageDetails;
	}

	public DueDetails getDueDetails() {
		return dueDetails;
	}

	public double getExcessAmt() {
		return excessAmt;
	}

	public void setExcessAmt(double excessAmt) {
		this.excessAmt = excessAmt;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Card ID : ").append(id).append(System.lineSeparator()).append("Excess Amount : Rs.")
				.append(excessAmt).append(customerDetails).append(cardDetails).append(usageDetails).append(dueDetails);
		return sb.toString();
	}

}
