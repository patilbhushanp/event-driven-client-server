package com.sanbhu.event;

import com.sanbhu.event.handlers.Handler;
import com.sanbhu.event.message.Message;

public interface Processor<E extends Message> {
	public void registerHandlers(Class<? extends E> handlerType, Handler<? extends E> handler);

	public abstract String dispatchEvent(E message);
}
