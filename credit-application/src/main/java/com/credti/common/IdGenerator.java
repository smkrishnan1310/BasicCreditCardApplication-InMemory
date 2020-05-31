package com.credti.common;

import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

	private static final AtomicLong CARD_ID = new AtomicLong();

	/**Will return next unique Card IDs*/
	public static final long getCardId() {
		return CARD_ID.incrementAndGet();
	}
}
