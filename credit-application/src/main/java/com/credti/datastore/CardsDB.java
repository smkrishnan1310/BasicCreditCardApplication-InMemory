package com.credti.datastore;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.credti.Exception.AccountException;
import com.credti.common.Logger;
import com.credti.domain.CreditCard;

/** Simulate DB for cards */
public class CardsDB {

	private static final Map<Long, CreditCard> CARDS = new ConcurrentHashMap<Long, CreditCard>();

	private static final Set<Long> DUE_CARDS = new HashSet<Long>();

	public static final CreditCard selectCard(final long id) {
		return CARDS.get(id);
	}

	public static final void insertCard(final CreditCard card) {
		if (null == CARDS.get(card.getId()))
			CARDS.put(card.getId(), card);
		else {
			Logger.log("Credit Card creation failed. Duplicate Account creation with ID = " + card.getId());
			throw new AccountException("Duplicate Card creation");
		}
	}

	public static final void addInDueCards(final long id) {
		if (null == CARDS.get(id)) {
			Logger.log("Invalid credit card with ID = " + id);
			throw new AccountException("Invalid credit card");
		} else {
			DUE_CARDS.add(id);
		}
	}

	public static final List<Long> getCopyOfAllDueCards() {
		return new LinkedList<Long>(DUE_CARDS);
	}

	public static final boolean isDueCard(final long id) {
		return DUE_CARDS.contains(id);
	}

	public static final void removeFromDueCards(final long id) {
		Logger.log("Credit card with ID: " + id + " remove from due cards list");
		DUE_CARDS.remove(id);
	}
}
