/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message.commands;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;



/**
 * @author Ken J. Aguilar
 */
public class RecordCommand implements Serializable {

	public static final class MessageRecordDetails implements Serializable {

		private static final long serialVersionUID = 2954398510075588584L;
		private final String name;
		private final MemType type;
		private final List<List<Object>> headerElementNames;
		private final List<List<Object>> bodyElementNames;
		private final boolean headerDump;
		private final boolean bodyDump;
		
		public MessageRecordDetails(final String name, final MemType type, boolean headerDump, final List<List<Object>> headerElementNames, boolean bodyDump, final List<List<Object>> bodyElementNames ) {
			super();
			this.name = name;
			this.type = type;
			this.headerDump = headerDump;
			this.bodyDump = bodyDump;
			this.headerElementNames = headerElementNames;
			this.bodyElementNames = bodyElementNames;
		}

		public static long getSerialVersionUID() {
			return serialVersionUID;
		}

		public List<List<Object>> getBodyElementNames() {
			return bodyElementNames;
		}
		
	   public List<List<Object>> getHeaderElementNames() {
	      return headerElementNames;
	   }

	   public boolean getHeaderDump(){
	      return this.headerDump;
	   }
	   
	   public boolean getBodyDump(){
	      return this.bodyDump;
	   }
	   
		public String getName() {
			return name;
		}

		public MemType getType() {
			return type;
		}
		
	}
	private static final long serialVersionUID = -1000973301709084337L;
	
	private final List<MessageRecordDetails> list;
	private final InetSocketAddress destAddress;
	private final IMsgToolServiceClient client;
	
	public RecordCommand(
			final IMsgToolServiceClient client, 
			InetSocketAddress destAddress,
			List<MessageRecordDetails> list) {
		this.client = client;
		this.list = list;
		this.destAddress = destAddress;
	}

	/**
	 * @return the destAddress
	 */
	public InetSocketAddress getDestAddress() {
		return destAddress;
	}

	public Collection<MessageRecordDetails> getMsgsToRecord() {
		return list;
	}


	public IMsgToolServiceClient getClient() {
		return client;
	}


}
