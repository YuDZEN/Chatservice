/*
 * Copyright (c) 2024.  Jerome David. Univ. Grenoble Alpes.
 * This file is part of DcissChatService.
 *
 * DcissChatService is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * DcissChatService is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */

package fr.uga.miashs.dciss.chatservice.client;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;


import fr.uga.miashs.dciss.chatservice.common.Packet;
import fr.uga.miashs.dciss.chatservice.common.db.DatabaseManager;


/**
 * Manages the connection to a ServerMsg. Method startSession() is used to
 * establish the connection. Then messages can be sent by a call to sendPacket.
 * The reception is done asynchronously (internally by the method receiveLoop())
 * and the reception of a message is notified to MessageListeners. To register
 * a MessageListener, the method addMessageListener has to be called. Sessions
 * are closed using the method closeSession().
 */
public class ClientMsg {

	private String serverAddress;
	private int serverPort;

	private Socket s;
	private DataOutputStream dos;
	private DataInputStream dis;

	private int identifier;

	private List<MessageListener> mListeners;
	private List<ConnectionListener> cListeners;
	private String username;

	/**
	 * Create a client with an existing ID, that will connect to the server at the
	 * given address and port.
	 *
	 * @param username The username of the client
	 * @param address  The server address or hostname
	 * @param port     The port number
	 */
	public ClientMsg(String username, String address, int port) {
		if (port <= 0)
			throw new IllegalArgumentException("Server port must be greater than 0");

		this.username = username;
		serverAddress = address;
		serverPort = port;
		mListeners = new ArrayList<>();
		cListeners = new ArrayList<>();
	}

	/**
	 * Register a MessageListener to the client. It will be notified each time a
	 * message is received.
	 *
	 * @param l The MessageListener to register
	 */
	public void addMessageListener(MessageListener l) {
		if (l != null)
			mListeners.add(l);
	}
	protected void notifyMessageListeners(Packet p) {
		mListeners.forEach(x -> x.messageReceived(p));
	}

	/**
	 * Register a ConnectionListener to the client. It will be notified if the connection starts or ends.
	 *
	 * @param l The ConnectionListener to register
	 */
	public void addConnectionListener(ConnectionListener l) {
		if (l != null)
			cListeners.add(l);
	}
	protected void notifyConnectionListeners(boolean active) {
		cListeners.forEach(x -> x.connectionEvent(active));
	}

	public int getIdentifier() {
		return identifier;
	}

	/**
	 * Method to be called to establish the connection.
	 */
	/**
	 * Method to be called to establish the connection.
	 */
	public void startSession() {
		if (s == null || s.isClosed()) {
			try {
				s = new Socket(serverAddress, serverPort);
				dos = new DataOutputStream(s.getOutputStream());
				dis = new DataInputStream(s.getInputStream());

				// obtain the identifier from the database
				try {
					identifier = DatabaseManager.getUserIdByUsername(username);
				} catch (SQLException e) {
					e.printStackTrace();
					// handle the exception, for example by logging it and returning
					return;
				}

				dos.writeInt(identifier);
				dos.flush();

				System.out.println(this.identifier);
				if (identifier == 0) {
					identifier = dis.readInt();
				}
				// start the receive loop
				new Thread(() -> receiveLoop()).start();
				notifyConnectionListeners(true);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				// handle UnknownHostException, for example by logging it and returning
				return;
			} catch (EOFException e) {
				// handle EOFException
				e.printStackTrace();
				closeSession();
			} catch (IOException e) {
				e.printStackTrace();
				// error, close session
				closeSession();
			}
		}
	}



	/**
	 * Send a packet to the specified destination (either a userId or groupId).
	 *
	 * @param destId The destination ID
	 * @param data   The data to be sent
	 */
	public void sendPacket(int destId, byte[] data) {
		try {
			synchronized (dos) {
				dos.writeInt(destId);
				dos.writeInt(data.length);
				dos.write(data);
				dos.flush();
			}
		} catch (IOException e) {
			// Error occurred, connection closed
			closeSession();
		}
	}

	/**
	 * Start the receive loop. This method has to be called only once.
	 */
	private void receiveLoop() {
		try {
			while (s != null && !s.isClosed()) {
				int sender = dis.readInt();
				int dest = dis.readInt();
				int length = dis.readInt();
				byte[] data = new byte[length];
				dis.readFully(data);
				notifyMessageListeners(new Packet(sender, dest, data));
			}
		} catch (IOException e) {
			// Error occurred, connection closed
		}
		closeSession();
	}

	/**
	 * Close the session.
	 */
	public void closeSession() {
		try {
			if (s != null)
				s.close();
		} catch (IOException e) {
			// Error occurred while closing the session
		}
		s = null;
		notifyConnectionListeners(false);
	}

	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		ClientMsg c = new ClientMsg("username", "localhost", 1666);

		// Add a dummy listener that prints the content of a message as a string
		c.addMessageListener(p -> System.out.println(p.srcId + " says to " + p.destId + ": " + new String(p.data)));

		// Add a connection listener that exits the application when the connection is closed
		c.addConnectionListener(active -> { if (!active) System.exit(0); });

		c.startSession();
		System.out.println("Your identifier: " + c.getIdentifier());

		Scanner sc = new Scanner(System.in);
		String lu = null;
		while (!"\\quit".equals(lu)) {
			try {
				System.out.println("Whom do you want to write to? ");
				int dest = Integer.parseInt(sc.nextLine());

				System.out.println("Your message? ");
				lu = sc.nextLine();
				c.sendPacket(dest, lu.getBytes());
			} catch (InputMismatchException | NumberFormatException e) {
				System.out.println("Invalid format");
			}
		}

		c.closeSession();
	}
}
