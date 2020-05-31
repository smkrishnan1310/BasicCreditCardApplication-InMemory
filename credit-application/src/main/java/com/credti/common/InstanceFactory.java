package com.credti.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.credti.datastore.CardsDB;
import com.credti.processor.EOMPProcessor;
import com.credti.service.AccountService;
import com.credti.service.PaymentService;
import com.credti.service.TransactionService;

/**
 * Similar to bean containers to create instances and deliver to caller [get()]
 * based on the ask
 */
public class InstanceFactory {

	private static final Map<Class, Object> INSTANCE_MAP = new ConcurrentHashMap<>();
	static {
		INSTANCE_MAP.put(AccountService.class, new AccountService());
		INSTANCE_MAP.put(CardsDB.class, new CardsDB());
		INSTANCE_MAP.put(PaymentService.class, new PaymentService());
		INSTANCE_MAP.put(TransactionService.class, new TransactionService());
		INSTANCE_MAP.put(EOMPProcessor.class, new EOMPProcessor());
	}

	public static final Object get(Class c) {
		return INSTANCE_MAP.get(c);
	}

}
