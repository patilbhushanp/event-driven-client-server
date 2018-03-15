package com.sanbhu.event.message;

public class BinaryMessage implements Message {

	private String message;

	public BinaryMessage(final String message) {
		this.message = message;
	}

	@Override
	public Class<? extends Message> getType() {
		return getClass();
	}

	public String getMessage() {
		return message;
	}
}