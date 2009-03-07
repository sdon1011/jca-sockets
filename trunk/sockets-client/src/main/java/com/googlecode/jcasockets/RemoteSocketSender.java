package com.googlecode.jcasockets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class RemoteSocketSender implements SocketSender, SocketSenderFactory {

	public RemoteSocketSender() {
	}

	private Socket socket;
	private Integer port;
	private String ipAddress;

	public RemoteSocketSender(String ipAddress, Integer port) {
		this.ipAddress = ipAddress;
		this.port = port;
		final SocketAddress socketAddress = new InetSocketAddress(ipAddress, port);
		socket = new Socket();

		final int timeoutMs = 0;
		try {
			socket.connect(socketAddress, timeoutMs);
		} catch (IOException e) {
			throw new RuntimeException("Exception while connecting: " + ipAddress + ":" + port, e);
		}

	}

	@Override
	public String send(String sendMessage) {
		StringBuilder sb = new StringBuilder(sendMessage.length());
		OutputStream outputStream;
		try {
			outputStream = socket.getOutputStream();
			outputStream.write(sendMessage.getBytes());
			socket.shutdownOutput();

			final InputStream inputStream = socket.getInputStream();
			final BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));

			String str;
			while ((str = rd.readLine()) != null) {
				sb.append(str);
			}
			rd.close();
		} catch (IOException e) {
			throw new RuntimeException("Exception while sending: " + ipAddress + ":" + port, e);
		}
		return sb.toString();
	}

	@Override
	public SocketSender createSocketSender(String ipAddress, Integer port) {
		return new RemoteSocketSender(ipAddress, port);
	}

}
