package com.credti.service;

import com.credti.common.InstanceFactory;
import com.credti.constants.CardStatus;
import com.credti.constants.DueStatus;
import com.credti.datastore.CardsDB;
import com.credti.domain.CreditCard;

public class PaymentService {

	public boolean payDue(final CreditCard card, double amt) {
		if (amt <= 0d)
			return false;
		AccountService accService = (AccountService) InstanceFactory.get(AccountService.class);
		if (accService.isValidToPay(card) && amt > 0d) {
			boolean updateUsage = false;
			double bal = payInterest(card, amt);
			if (bal > 0d)
				bal = payLateFee(card, bal);
			if (bal > 0d) {
				amt = bal;
				updateUsage = true;
				bal = payPastCashDue(card, bal);
			}
			if (bal > 0d)
				bal = payPastCreditDue(card, bal);
			if (bal > 0d)
				bal = payCurrCashDue(card, bal);
			if (bal > 0d)
				bal = payCurrCreditDue(card, bal);
			if (bal > 0d)
				addInExcessAmt(card, bal);
			if (updateUsage) {
				bal = updateCashUsageDetails(card, amt);

				if (bal > 0d)
					updateCreditUsageDetails(card, bal);

				updatePendingDueCount(card);
				updateDueStatus(card);
				updateCardStatus(card);
			}
			return true;
		}
		return false;
	}

	private double payInterest(final CreditCard card, final double amt) {
		final double intrst = card.getDueDetails().getInterest();
		if (amt >= intrst) {
			card.getDueDetails().setInterest(0d);
			return (amt - intrst);
		}
		card.getDueDetails().setInterest(intrst - amt);
		return 0d;

	}

	private double payLateFee(final CreditCard card, final double amt) {
		final double fee = card.getDueDetails().getLateFee();
		if (amt >= fee) {
			card.getDueDetails().setLateFee(0d);
			return (amt - fee);
		}
		card.getDueDetails().setLateFee(fee - amt);
		return 0d;
	}

	private double payPastCashDue(final CreditCard card, final double amt) {
		final double due = card.getDueDetails().getOldCashDue();
		if (amt >= due) {
			card.getDueDetails().setOldCashDue(0d);
			payPastCreditDue(card, due);
			return (amt - due);
		}
		payPastCreditDue(card, amt);
		card.getDueDetails().setOldCashDue(due - amt);
		return 0d;
	}

	private double payPastCreditDue(final CreditCard card, final double amt) {
		final double due = card.getDueDetails().getOldCreditDue();
		if (amt >= due) {
			card.getDueDetails().setOldCreditDue(0d);
			return (amt - due);
		}
		card.getDueDetails().setOldCreditDue(due - amt);
		return 0d;
	}

	private double payCurrCashDue(final CreditCard card, final double amt) {
		final double due = card.getDueDetails().getCurrCashDue();
		if (amt >= due) {
			card.getDueDetails().setCurrCashDue(0d);
			payCurrCreditDue(card, due);
			return (amt - due);
		}
		payCurrCreditDue(card, amt);
		card.getDueDetails().setCurrCashDue(due - amt);
		return 0d;
	}

	private double payCurrCreditDue(final CreditCard card, final double amt) {
		final double due = card.getDueDetails().getCurrCreditDue();
		if (amt >= due) {
			card.getDueDetails().setCurrCreditDue(0d);
			return (amt - due);
		}
		card.getDueDetails().setCurrCreditDue(due - amt);
		return 0d;
	}

	private double updateCashUsageDetails(final CreditCard card, double amt) {
		final double cashUsage = card.getUsageDetails().getTotCashUsage();
		if (amt >= cashUsage) {
			card.getUsageDetails().setTotCashUsage(0d);
			updateCreditUsageDetails(card, cashUsage);
			return (amt - cashUsage);
		}
		updateCreditUsageDetails(card, amt);
		card.getUsageDetails().setTotCashUsage(cashUsage - amt);
		return 0d;
	}

	private double updateCreditUsageDetails(final CreditCard card, double amt) {
		final double creditUsage = card.getUsageDetails().getTotCreditUsage();
		if (amt >= creditUsage) {
			card.getUsageDetails().setTotCreditUsage(0d);
			return (amt - creditUsage);
		}
		card.getUsageDetails().setTotCreditUsage(creditUsage - amt);
		return 0d;
	}

	private void updatePendingDueCount(final CreditCard card) {
		byte dueCount = card.getDueDetails().getPendingDueCount();
		if (dueCount > 0 && card.getDueDetails().getOldCashDue() == 0d)
			dueCount--;
		if (dueCount > 0 && card.getDueDetails().getOldCreditDue() == 0d)
			dueCount = 0;
		card.getDueDetails().setPendingDueCount(dueCount);
	}

	private void addInExcessAmt(final CreditCard card, final double amt) {
		card.setExcessAmt(card.getExcessAmt() + amt);
	}

	private void updateDueStatus(final CreditCard card) {
		if (card.getUsageDetails().getTotCreditUsage() > 0d) {
			card.getDueDetails().setDueStatus(DueStatus.PENDING);
		} else {
			card.getDueDetails().setDueStatus(DueStatus.NA);
			CardsDB.removeFromDueCards(card.getId());
		}
	}

	private void updateCardStatus(final CreditCard card) {
		if (card.getCardDetails().getStatus().equals(CardStatus.DELIQUIENT)
				&& card.getDueDetails().getPendingDueCount() < 2)
			card.getCardDetails().setStatus(CardStatus.ACTIVE);
	}
}
