package com.sanbhu.event.handlers;

import com.sanbhu.event.message.Message;

public interface Handler<E extends Message> {
	public String dispatch(E message);
}
