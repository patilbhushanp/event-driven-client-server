package com.sanbhu.event.handlers;

import com.sanbhu.event.message.TextMessage;

public class TextHandler implements Handler<TextMessage> {
	@Override
	public String dispatch(TextMessage textMessage) {
		return textMessage.getClass().getName();
	}
}