package com.credti.processor;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.credti.common.Logger;
import com.credti.constants.CardStatus;
import com.credti.constants.DueStatus;
import com.credti.constants.EompConstants;
import com.credti.datastore.CardsDB;
import com.credti.domain.CardDetails;
import com.credti.domain.CreditCard;
import com.credti.domain.DueDetails;
import com.credti.domain.UsageDetails;

/**
 * (End of month processing) EOMP-Processor which will run every 28 days ones
 * (end of month) to calculate interest, late fee and other params
 */
public class EOMPProcessor {

	public static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
	public static final ExecutorService WORKER = Executors
			.newFixedThreadPool(1 + Runtime.getRuntime().availableProcessors());

	public EOMPProcessor() {
		Runnable r = () -> process();
		SCHEDULER.scheduleAtFixedRate(r, 1, 28, TimeUnit.DAYS);
	}

	public static final void shutDownAllServices() {
		Logger.log("Shutdown triggered for all Services running in EOMP Processor");
		SCHEDULER.shutdownNow();
		WORKER.shutdownNow();
		Logger.log("All Services running in EOMP Processor are shutdown successfully");
	}

	public void process() {
		final List<Long> dueCards = CardsDB.getCopyOfAllDueCards();
		if (null != dueCards && dueCards.size() > 0) {
			List<Long> temp = new LinkedList<Long>();
			int i = 0;
			for (Long id : dueCards) {
				if (i == 30) {
					i = 0;
					process(temp);
					temp = new LinkedList<Long>();
				}
				temp.add(id);
				i++;
			}
			process(temp);
		}
	}

	public void process(List<Long> dueIds) {
		Runnable r = () -> dueIds.forEach(id -> updateDueDetails(id));
		WORKER.submit(r);
	}

	public void updateDueDetails(final long id) {
		final CreditCard card = CardsDB.selectCard(id);
		synchronized (card) {
			updateDueDetails(card);
			updateDueCards(card);
			updateCardStatus(card);
		}
	}

	private void updateDueDetails(final CreditCard card) {
		updateLateFee(card);
		updateInterest(card);
		final DueDetails due = card.getDueDetails();
		if (DueStatus.PENDING.equals(due.getDueStatus())) {
			due.setOldCashDue(due.getOldCashDue() + due.getCurrCashDue());
			due.setOldCreditDue(due.getOldCreditDue() + due.getCurrCreditDue());
			due.setCurrCashDue(0d);
			due.setCurrCreditDue(0d);
		}
	}

	private void updateLateFee(final CreditCard card) {
		final DueDetails due = card.getDueDetails();
		if (due.getPendingDueCount() > 0 && CardStatus.ACTIVE.equals(card.getCardDetails().getStatus())) {
			due.setLateFee(due.getLateFee() + EompConstants.LATE_FEE);
		}

	}

	private void updateCardStatus(final CreditCard card) {
		final CardDetails cd = card.getCardDetails();
		final DueDetails due = card.getDueDetails();
		if (due.getPendingDueCount() >= 3) {
			cd.setStatus(CardStatus.DELIQUIENT);
		} else
			cd.setStatus(CardStatus.ACTIVE);
	}

	private void updateInterest(final CreditCard card) {
		if (null == card)
			return;

		final UsageDetails usage = card.getUsageDetails();
		if (!CardStatus.CLOSED.equals(card.getCardDetails().getStatus()) && usage.getTotCreditUsage() > 0d) {
			final DueDetails due = card.getDueDetails();
			if (DueStatus.NA.equals(due.getDueStatus()))
				return;
			double total = due.getInterest();
			if (usage.getTotCashUsage() > 0d) {
				if (CardStatus.DELIQUIENT.equals(card.getCardDetails().getStatus()))
					total += (usage.getTotCashUsage() * EompConstants.DELIQUIENT_INTEREST);
				else
					total += (usage.getTotCashUsage() * EompConstants.ACTIVE_INTEREST);
			}
			if (due.getPendingDueCount() > 1) {
				if (CardStatus.DELIQUIENT.equals(card.getCardDetails().getStatus()))
					total += ((usage.getTotCreditUsage() - usage.getTotCashUsage())
							* EompConstants.DELIQUIENT_INTEREST);
				else
					total += ((usage.getTotCreditUsage() - usage.getTotCashUsage()) * EompConstants.ACTIVE_INTEREST);
			}
			if (total < EompConstants.LATE_FEE)
				total = EompConstants.LATE_FEE;
			due.setInterest(total);
			if (due.getPendingDueCount() < 3)
				due.setPendingDueCount((byte) (due.getPendingDueCount() + 1));
		} else
			CardsDB.removeFromDueCards(card.getId());
	}

	private void updateDueCards(final CreditCard card) {
		if (DueStatus.NA.equals(card.getDueDetails().getDueStatus()))
			CardsDB.removeFromDueCards(card.getId());
		else if (card.getUsageDetails().getTotCreditUsage() == 0d) {
			card.getDueDetails().setDueStatus(DueStatus.NA);
			CardsDB.removeFromDueCards(card.getId());
		}
	}
}
