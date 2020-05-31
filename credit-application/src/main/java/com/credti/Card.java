package com.credti;

import com.credti.domain.CardDetails;
import com.credti.domain.CustomerDetails;

public interface Card {

	CardDetails getCardDetails();

	CustomerDetails getCustomerDetails();

	long getId();
}
