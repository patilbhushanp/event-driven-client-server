package com.sanbhu.event.driven.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import com.sanbhu.event.EventProcessor;
import com.sanbhu.event.handlers.BinaryHandler;
import com.sanbhu.event.handlers.ObjectHandler;
import com.sanbhu.event.handlers.TextHandler;
import com.sanbhu.event.message.BinaryMessage;
import com.sanbhu.event.message.ObjectMessage;
import com.sanbhu.event.message.TextMessage;

public class EventServer implements Runnable {
	private final Integer PORT = 30680;
	private ServerSocketChannel serverSocketChannel;
	private Selector selector;
	private static final Integer BUFFER_SIZE = 1024;
	private ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
	private static final EventProcessor eventProcessor = new EventProcessor();

	public EventServer() throws IOException {
		this.serverSocketChannel = ServerSocketChannel.open();
		this.serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
		this.serverSocketChannel.configureBlocking(false);
		this.selector = Selector.open();
		this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
	}

	@Override
	public void run() {
		try {
			System.out.println("Server starting on port " + PORT);

			Iterator<SelectionKey> iterator;
			SelectionKey selectionKey;
			StringBuilder messageBuilder = new StringBuilder();
			while (this.serverSocketChannel.isOpen()) {
				selector.select();
				iterator = this.selector.selectedKeys().iterator();
				while (iterator.hasNext()) {
					selectionKey = iterator.next();
					iterator.remove();

					if (selectionKey.isAcceptable())
						this.handleAccept(selectionKey);
					if (selectionKey.isReadable())
						this.handleRead(selectionKey, messageBuilder);
				}
			}
		} catch (IOException exception) {
			System.out.println("IOException, server of port " + PORT + " terminating. Stack trace:");
			exception.printStackTrace();
		}
	}

	private void handleAccept(SelectionKey selectionKey) throws IOException {
		SocketChannel socketChannel = ((ServerSocketChannel) selectionKey.channel()).accept();
		String address = (new StringBuilder(socketChannel.socket().getInetAddress().toString())).append(":")
				.append(socketChannel.socket().getPort()).toString();
		socketChannel.configureBlocking(false);
		socketChannel.register(selector, SelectionKey.OP_READ, address);
		ByteBuffer messageOptionBuffer = ByteBuffer.wrap("Welcome to Event Drivent Server.\n".getBytes());
		socketChannel.write(messageOptionBuffer);

		messageOptionBuffer = ByteBuffer.wrap("Message follows below standards:\n".getBytes());
		socketChannel.write(messageOptionBuffer);

		messageOptionBuffer = ByteBuffer.wrap("MESSAGE_TYPE|MESSAGE\n".getBytes());
		socketChannel.write(messageOptionBuffer);

		messageOptionBuffer = ByteBuffer.wrap("Example,- TextMessage|Welcome to message driven world\n".getBytes());
		socketChannel.write(messageOptionBuffer);
		messageOptionBuffer.rewind();
		System.out.println("accepted connection from: " + address);
	}

	private void handleRead(SelectionKey selectionKey, StringBuilder messageBuilder) throws IOException {
		SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
		StringBuilder stringBuffer = new StringBuilder();
		String partialMessage;
		byteBuffer.clear();
		int read = 0;
		while ((read = socketChannel.read(byteBuffer)) > 0) {
			byteBuffer.flip();
			byte[] bytes = new byte[byteBuffer.limit()];
			byteBuffer.get(bytes);
			stringBuffer.append(new String(bytes));
			byteBuffer.clear();
		}
		if (read < 0) {
			partialMessage = selectionKey.attachment() + " left the session.\n";
			socketChannel.close();
			System.out.println(partialMessage);
		} else {
			partialMessage = stringBuffer.toString();
			if (partialMessage != null && partialMessage.length() > 1 && (short) partialMessage.charAt(1) == 10) {
				System.out.println("Input : " + messageBuilder.toString());
				String responseMessage = processMessage(messageBuilder);
				ByteBuffer messageOptionBuffer = ByteBuffer.wrap(("Response : " + responseMessage + "\n").getBytes());
				socketChannel.write(messageOptionBuffer);
				messageOptionBuffer.rewind();
				messageBuilder = new StringBuilder();
			} else {
				messageBuilder.append(partialMessage);
			}
		}

	}

	private String processMessage(final StringBuilder stringBuilder) {
		String responseMessage = "";
		String inputMessage = stringBuilder.toString();
		String[] inputStringArray = inputMessage.split("\\|");
		if (inputStringArray != null && inputStringArray.length >= 2) {
			switch (inputStringArray[0]) {
				case "TextMessage":
					responseMessage = eventProcessor.dispatchEvent(new TextMessage(inputStringArray[1]));
					break;
				case "BinaryMessage":
					responseMessage = eventProcessor.dispatchEvent(new BinaryMessage(inputStringArray[1]));
					break;
				case "ObjectMessage":
					responseMessage = eventProcessor.dispatchEvent(new ObjectMessage(inputStringArray[1]));
					break;
				default:
					responseMessage = "No Event Handler Registered for this message";
			}
		} else {
			responseMessage = "No Event Handler Registered for this message";
		}
		return responseMessage;
	}

	public static void main(String[] args) throws IOException {
		EventServer eventServer = new EventServer();
		registerHandlers();
		Thread eventServerThread = new Thread(eventServer);
		eventServerThread.start();
	}

	private static synchronized void registerHandlers() {
		eventProcessor.registerHandlers(TextMessage.class, new TextHandler());
		eventProcessor.registerHandlers(BinaryMessage.class, new BinaryHandler());
		eventProcessor.registerHandlers(ObjectMessage.class, new ObjectHandler());
	}
}
