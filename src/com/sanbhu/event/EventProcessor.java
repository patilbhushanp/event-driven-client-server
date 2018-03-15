package com.sanbhu.event;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sanbhu.event.handlers.Handler;
import com.sanbhu.event.message.Message;

public class EventProcessor implements Processor<Message> {

	@SuppressWarnings("rawtypes")
	private Map<Class<? extends Message>, Handler> handlers;

	@SuppressWarnings("rawtypes")
	public EventProcessor() {
		handlers = new ConcurrentHashMap<Class<? extends Message>, Handler>();
	}

	@Override
	public void registerHandlers(Class<? extends Message> messageType, Handler<? extends Message> handler) {
		handlers.put(messageType, handler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public String dispatchEvent(Message message) {
		return handlers.get(message.getClass()).dispatch(message);
	}

}
