package com.credti.service;

import com.credti.constants.CardStatus;
import com.credti.datastore.CardsDB;
import com.credti.domain.CardDetails;
import com.credti.domain.CreditCard;
import com.credti.domain.CustomerDetails;
import com.credti.domain.UsageDetails;

public class AccountService {

	public long createAcccount(final CardDetails cd, final CustomerDetails c) {
		final CreditCard card = new CreditCard(cd, c);
		CardsDB.insertCard(card);
		return card.getId();
	}

	public CreditCard getCreditCard(final long id) {
		return CardsDB.selectCard(id);
	}

	public boolean closeCard(final CreditCard card) {
		if (isEligibleToClose(card)) {
			card.getCardDetails().setStatus(CardStatus.CLOSED);
			return true;
		}
		return false;
	}

	public boolean isValidToPay(final CreditCard card) {
		if (null != card) {
			final CardDetails cd = card.getCardDetails();
			if (null != cd)
				return ((CardStatus.DELIQUIENT.equals(cd.getStatus())) || (CardStatus.ACTIVE.equals(cd.getStatus())));
		}
		return false;
	}

	public boolean isValidToSwipe(final CreditCard card) {
		if (null != card) {
			final CardDetails cd = card.getCardDetails();
			if (null != cd)
				return (CardStatus.ACTIVE.equals(cd.getStatus()));
		}
		return false;
	}

	public Double getBalanceCash(final CreditCard card) {
		final CardDetails cd = card.getCardDetails();
		final UsageDetails ud = card.getUsageDetails();
		return (card.getExcessAmt() + cd.getCashLimit() - ud.getTotCashUsage());
	}

	public boolean isEligibleToWithdraw(final CreditCard card) {
		if (isEligibleToSwipe(card)) {
			final Double bal = getBalanceCash(card);
			return (null != bal && bal > 0d);
		}
		return false;
	}

	public Double getBalanceCredit(final CreditCard card) {
		final CardDetails cd = card.getCardDetails();
		final UsageDetails ud = card.getUsageDetails();
		return (card.getExcessAmt() + cd.getCreditLimit() - ud.getTotCreditUsage());
	}

	public boolean isEligibleToSwipe(final CreditCard card) {
		if (isValidToSwipe(card)) {
			final Double bal = getBalanceCredit(card);
			return (null != bal && bal > 0d);
		}
		return false;
	}

	public boolean isEligibleToClose(final CreditCard card) {
		if (null != card)
			return !CardsDB.isDueCard(card.getId());
		return true;
	}
}
