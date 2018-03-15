package com.sanbhu.event.handlers;

import com.sanbhu.event.message.ObjectMessage;

public class ObjectHandler implements Handler<ObjectMessage> {
	@Override
	public String dispatch(ObjectMessage objectMessage) {
		return objectMessage.getClass().getName();
	}
}