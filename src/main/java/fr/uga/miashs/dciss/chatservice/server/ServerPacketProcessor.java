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

package fr.uga.miashs.dciss.chatservice.server;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

import fr.uga.miashs.dciss.chatservice.common.Packet;

public class ServerPacketProcessor implements PacketProcessor {
	private final static Logger LOG = Logger.getLogger(ServerPacketProcessor.class.getName());
	private ServerMsg server;

	public ServerPacketProcessor(ServerMsg server) {
		this.server = server;
	}

	@Override
	public void process(Packet packet) {
		ByteBuffer buffer = ByteBuffer.wrap(packet.data);
//		byte type = buffer.get();

//		if (type == 1) { // Caso de creación de grupo
//			createGroup(packet.srcId, buffer);
//		} else if (type == 2) { // Caso de envío de mensaje
			int destId = buffer.getInt();
			// Verificar la existencia del usuario antes de procesar el paquete
			if (server.getUser(destId) != null) {
				sendMessage(packet.type,packet.srcId, destId, buffer);
			} else {
				LOG.warning("Usuario con ID=" + destId + " no encontrado para enviar el mensaje.");
			}
//		} else {
//			LOG.warning("Mensaje de servidor de tipo=" + type + " no manejado por el procesador.");
//		}
	}

	public void sendMessage(int type, int senderId, int destId, ByteBuffer data) {

		// Obtener el usuario remitente y destinatario del servidor
		UserMsg sender = server.getUser(senderId);
		UserMsg recipient = server.getUser(destId);

		// Verificar que ambos usuarios existen y están conectados
		if (sender != null && recipient != null && sender.isConnected() && recipient.isConnected()) {
			// Crear un nuevo paquete con los datos del mensaje
			byte[] messageData = new byte[data.remaining()];
			data.get(messageData);
			Packet messagePacket = new Packet(type,senderId, destId, messageData);

			// Agregar el paquete a la cola de envío del destinatario
			recipient.process(messagePacket);
		} else {
			LOG.warning("Error al enviar el mensaje: usuario no encontrado o no conectado.");
		}
	}


	public void createGroup(int ownerId, ByteBuffer data) {
		int numMembers = data.getInt();
		GroupMsg group = server.createGroup(ownerId);
		for (int i = 0; i < numMembers; i++) {
			int memberId = data.getInt();
			UserMsg user = server.getUser(memberId);
			if (user != null) {
				group.addMember(user);
			} else {
				LOG.warning("Usuario con ID=" + memberId + " no encontrado para agregar al grupo.");
			}
		}
	}
}
