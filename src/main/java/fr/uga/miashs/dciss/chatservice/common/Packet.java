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

package fr.uga.miashs.dciss.chatservice.common;

/*
 * Data structure to represent a packet
 */



public class Packet {
	public final int srcId;
	public final int destId;
	public final byte[] data;

	public Packet(int srcId, int destId, byte[] data) {
		if (!isValidId(srcId) || !isValidId(destId)) {
			throw new IllegalArgumentException("Invalid source or destination ID");
		}
		this.srcId = srcId;
		this.destId = destId;
		this.data = data;
	}

	private boolean isValidId(int id) {
		// Realiza la validación del ID aquí
		// Por ejemplo, podrías verificar si el ID está dentro de un rango válido
		return id > 0;
	}
}