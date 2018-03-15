package com.sanbhu.event.message;

public class TextMessage implements Message {

	private String message;

	public TextMessage(final String message) {
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
