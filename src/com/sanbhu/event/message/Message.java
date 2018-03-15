package com.sanbhu.event.message;

public interface Message {
	public Class<? extends Message> getType();
}
