package com.sanbhu.event.message;

public class ObjectMessage implements Message {

	private String message;

	public ObjectMessage(final String message) {
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
