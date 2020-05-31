package com.credti.domain;

/**Pojo for CustomerDetails which is composite in card*/
public class CustomerDetails {

	private final String name;
	private final String address;
	private final String phone;

	public CustomerDetails(String name, String address, String phone) {
		this.name = name;
		this.address = address;
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public String getPhone() {
		return phone;
	}

	@Override
	public String toString() {
		return System.lineSeparator() + "CustomerDetails ***************" + System.lineSeparator() + "Name=" + name + ", Address="
				+ address + ", Phone=" + phone + System.lineSeparator();
	}

}
