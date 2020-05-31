package com.credti.service;

import com.credti.common.InstanceFactory;
import com.credti.common.Logger;
import com.credti.constants.DueStatus;
import com.credti.datastore.CardsDB;
import com.credti.domain.CreditCard;
import com.credti.domain.DueDetails;
import com.credti.domain.UsageDetails;

public class TransactionService {

	public boolean onlinePay(final CreditCard card, double amt) {
		return swipe(card, amt);
	}

	public boolean swipe(final CreditCard card, double amt) {
		if (amt <= 0d)
			return false;
		AccountService accService = (AccountService) InstanceFactory.get(AccountService.class);
		if (accService.isEligibleToSwipe(card)) {
			final Double bal = accService.getBalanceCredit(card);
			if (null != bal && bal >= amt) {
				if (card.getExcessAmt() > 0d) {
					amt = updateExcessAmt(card, amt);
					if (amt == 0d)
						return true;
				}
				updateCreditDueDetailsSUB(card, amt);
				updateCreditUsageDetailsSUB(card, amt);
				CardsDB.addInDueCards(card.getId());
				return true;
			}
			Logger.log("Swipe failed for " + card.getId());
		}
		return false;
	}

	public boolean withdraw(final CreditCard card, double amt) {
		if (amt <= 0d)
			return false;
		AccountService accService = (AccountService) InstanceFactory.get(AccountService.class);
		if (accService.isEligibleToWithdraw(card)) {
			final Double bal = accService.getBalanceCash(card);
			if (null != bal && bal >= amt) {
				if (card.getExcessAmt() > 0d) {
					amt = updateExcessAmt(card, amt);
					if (amt == 0d)
						return true;
				}
				updateCreditDueDetailsSUB(card, amt);
				updateCreditUsageDetailsSUB(card, amt);
				updateCashDueDetailsSUB(card, amt);
				updateCashUsageDetailsSUB(card, amt);
				CardsDB.addInDueCards(card.getId());
				return true;
			}
			Logger.log("Withdraw failed for " + card.getId());
		}
		return false;
	}

	// ------------------------
	private double updateExcessAmt(final CreditCard card, double amt) {
		if (card.getExcessAmt() >= amt) {
			card.setExcessAmt(card.getExcessAmt() - amt);
			return 0d;
		}
		amt = amt - card.getExcessAmt();
		card.setExcessAmt(0d);
		return amt;
	}

	private void updateCreditDueDetailsSUB(final CreditCard card, final double amt) {
		final DueDetails due = card.getDueDetails();
		due.setCurrCreditDue(due.getCurrCreditDue() + amt);
		due.setDueStatus(DueStatus.PENDING);
	}

	private void updateCreditUsageDetailsSUB(final CreditCard card, final double amt) {
		final UsageDetails usage = card.getUsageDetails();
		usage.setTotCreditUsage(usage.getTotCreditUsage() + amt);
	}

	// ------------------------

	private void updateCashDueDetailsSUB(final CreditCard card, final double amt) {
		final DueDetails due = card.getDueDetails();
		due.setCurrCashDue(due.getCurrCashDue() + amt);
		due.setDueStatus(DueStatus.PENDING);
	}

	private void updateCashUsageDetailsSUB(final CreditCard card, final double amt) {
		final UsageDetails usage = card.getUsageDetails();
		usage.setTotCashUsage(usage.getTotCashUsage() + amt);
	}
}
