package com.sanbhu.event.handlers;

import com.sanbhu.event.message.BinaryMessage;

public class BinaryHandler implements Handler<BinaryMessage> {
	@Override
	public String dispatch(BinaryMessage binaryMessage) {
		return binaryMessage.getClass().getName();
	}
}