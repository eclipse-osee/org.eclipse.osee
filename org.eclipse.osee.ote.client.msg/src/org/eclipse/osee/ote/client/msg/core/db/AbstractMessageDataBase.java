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
package org.eclipse.osee.ote.client.msg.core.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.core.internal.MessageReference;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.message.tool.MessageMode;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.EntityFactory;
import org.eclipse.osee.ote.service.IMessageDictionary;

/**
 * @author author Ken J. Aguilar
 *
 */
public abstract class AbstractMessageDataBase {

	private final HashMap<MessageReference, MessageInstance> referenceToMsgMap = new HashMap<MessageReference, MessageInstance>();
	private final ConcurrentHashMap<Integer, MessageInstance> idToMsgMap = new ConcurrentHashMap<Integer, MessageInstance>();

	private final IMessageDictionary dictionary;
	private IMsgToolServiceClient client;
	private IRemoteMessageService service;
	private final DataReader reader = new DataReader(null, null, true, null, new EntityFactory() {

		@Override
		public boolean isEnabled() {
			return true;
		}

	});
	
	protected AbstractMessageDataBase(IMessageDictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	public MessageInstance findInstance(String name, MessageMode mode, MemType type) {
		MessageReference reference = new MessageReference(type, mode, name);
		return referenceToMsgMap.get(reference);	
	}
	
	public MessageInstance acquireInstance(String name) throws Exception {
		return acquireInstance(name, MessageMode.READER, null);
	}
	
	public MessageInstance acquireInstance(String name, MessageMode mode, MemType type) throws Exception {
		if (type == null) {
			Class<? extends Message> msgClass = dictionary.lookupMessage(name);
			Message msg = createMessage(msgClass);
			for (ArrayList<MessageData> dataList : (Collection<ArrayList<MessageData>>) msg.getAllData()) {
				for (MessageData data : dataList) {
					data.setReader(reader);
				}
			}
			type = msg.getDefaultMessageData().getType();
		}
		MessageReference reference = new MessageReference(type, mode, name);
		MessageInstance instance = referenceToMsgMap.get(reference);
		if (instance == null) {
			Class<? extends Message> msgClass = dictionary.lookupMessage(name);
			Message msg = createMessage(msgClass);
			for (ArrayList<MessageData> dataList : (Collection<ArrayList<MessageData>>) msg.getAllData()) {
				for (MessageData data : dataList) {
					data.setReader(reader);
				}
			}
			msg.setMemSource(type);
			instance = new MessageInstance(msg, mode, type);
			referenceToMsgMap.put(reference, instance);
		}
		instance.incrementReferenceCount();
		if (service != null && !instance.isAttached()) {
			doInstanceAttach(instance, service);
		}
		return instance;
	}

	public void releaseInstance(MessageInstance instance) throws Exception {
		instance.decrementReferenceCount();
		if (!instance.hasReferences()) {
			if (instance.isAttached()) {
				doInstanceDetach(instance, service);
			}
			MessageReference reference = new MessageReference(instance.getType(), instance.getMode(), instance.getMessage().getClass().getName());
			referenceToMsgMap.remove(reference);
			destroyMessage(instance.getMessage());
		}
		
	}
	
	protected abstract Message createMessage(Class<? extends Message> msgClass) throws Exception;
	protected abstract void destroyMessage(Message message) throws Exception;
	
	public void attachToService(IRemoteMessageService service, IMsgToolServiceClient client) {
		this.service = service;
		this.client = client;
		for (MessageInstance instance : referenceToMsgMap.values()) {
			try {
				doInstanceAttach(instance, service);
			} catch (Exception e) {
				OseeLog.log(AbstractMessageDataBase.class, Level.SEVERE, "could not attach instance for " + instance.toString(), e);
			}
		}
	}

	public void detachService(IRemoteMessageService service) {
		this.service = null;
		this.client = null;
		for (MessageInstance instance : referenceToMsgMap.values()) {
			doInstanceDetach(instance, service);
		}
		
	}
	
	public MessageInstance findById(int id) {
		return idToMsgMap.get(id);
	}
	
	private boolean doInstanceAttach(MessageInstance instance, IRemoteMessageService service) throws Exception {
		Integer id = instance.attachToService(service, client);
		if (id == null) {
			// can't subscribe because environment does not support this type
			return false;
		}
		idToMsgMap.put(id, instance);
		return true;
	}
	
	private void doInstanceDetach(MessageInstance instance, IRemoteMessageService service) {
		try {
			Integer id = instance.getId();
			if (id != null) {
				idToMsgMap.remove(id);
			}
			instance.detachService(service, client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected IMessageDictionary getDictionary() {
		return dictionary;
	}
}
