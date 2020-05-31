# BasicCreditCardApplication-InMemory

Please read comments in Bank.java for more info about solution

Question

Design a credit card application in
Java that implements the given
process

Definitions and Steps

Credit Card: A card used to obtain consumer credit at the time of
purchasing an article or service. 

Issuance:  Any person who wants to use a credit card needs to first apply
to the credit card issuer (primarily a bank) giving details such as name,
address and phone number. When the credit card issuer issues a credit card,
the card is assigned a credit limit for transactions, a cash limit for
withdrawals, and a date on which the card expires. Generally, the cash
limit is less than the credit limit. In some cases, there might be no cash
withdrawal facility, though a credit facility is a must. 

Transact/ Withdraw : Upon receiving the card, the user can use it to
transact or to withdraw money. As the user transacts or withdraws money,
the available credit and cash limits change. The total amount transacted
should be less than the credit limit and the total cash withdrawals should
be less than the cash limit. The cash available for withdrawal should never
exceed the credit available. 

End of month Processing (EOMP): At the end of the month, an interest
accrued on the unpaid amount, and a statement is generated that details the
total amount to be paid by the user. The monthly interest is 2%. Interest
on cash withdrawal starts to accrue, in the immediate EOMP cycle, while
transactions get credit for one cycle. 

Payments: When the user pays, the outstanding balance reduces and
availability increases. The hierarchy of payments is as follows

 Past cash due, followed by past credit due, followed by current cash
due, followed by current credit due. If there is still money left in
the account, it is adjusted subsequently.

 The minimum amount due is typically a small percentage of the total
amount due, and if the user does not pay the minimum amount before
the next EOMP, a late charge accrues, along with an interest. Minimum
amount due is 5% of the total amount due or $100, whichever is more. 

 Late charges are $100/-. 

Credit card can have three states: 

Active:  When a card is issued it falls in the active state.  

Delinquent:  If the amount due is at least $100, and 3 consecutive late
charges accrue, the card moves from the active state to the delinquent
state. In the delinquent state, the transact and withdraw operations are
disabled, and the late charge does not accrue. However, the interest
continues to accrue. Once a card is in a delinquent state, at least 25% of
the amount due needs to be paid up for it to get back to the active
state.. 

Closed: A card can be closed at any point, as long as it is in the active
state and amount to be paid is zero. During the closed state, none of the
operations are valid.
