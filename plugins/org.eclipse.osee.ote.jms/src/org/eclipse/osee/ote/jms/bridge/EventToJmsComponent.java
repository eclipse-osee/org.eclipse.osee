/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.jms.bridge;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.ote.jms.OteServerJmsNodeProvider;
import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class EventToJmsComponent extends OseeMessagingListener implements EventHandler {

	private OteServerJmsNodeProvider connecitonNode;
	private EventAdmin eventAdmin;
	private List<Event> eventQueue;
	private final UUID MYID;
	
	public EventToJmsComponent(){
		eventQueue = new CopyOnWriteArrayList<Event>();
		MYID = UUID.randomUUID();
	}

	void start(){
		
	}
	
	public synchronized void bindConnectionNode(OteServerJmsNodeProvider connectionNode){
		this.connecitonNode = connectionNode;
		connecitonNode.getConnectionNode().subscribe(BridgeMessages.BYTE_MESSAGE, this);
		for(Event event:eventQueue){
			sendEvent(event);
		}
		eventQueue.clear();
	}
	
	public synchronized void unbindConnectionNode(OteServerJmsNodeProvider connectionNode){
		connecitonNode.getConnectionNode().unsubscribe(BridgeMessages.BYTE_MESSAGE, this);
		this.connecitonNode = null;
	}

	public void bindEventAdmin(EventAdmin eventAdmin){
		this.eventAdmin = eventAdmin;
	}
	
	public void unbindEventAdmin(EventAdmin eventAdmin){
		this.eventAdmin = null;
	}
	
	@Override
	public synchronized void handleEvent(Event event) {
		if(connecitonNode != null){
			sendEvent(event);
		} else {
			eventQueue.add(event);
		}
	}
	
	private void sendEvent(Event event){
		OteEventMessage message = OteEventMessageUtil.getOteByteMessage(event);
		if(message != null){
			try {
				UUID id = OteEventMessageUtil.getUUID(message);
				if(!id.equals(MYID)){
					OteEventMessageUtil.setUUID(message, MYID);
					connecitonNode.getConnectionNode().send(BridgeMessages.BYTE_MESSAGE, message.getData());
				}
			} catch (OseeCoreException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
		if(message instanceof byte[]){
			OteEventMessage msg = new OteEventMessage((byte[])message);
			UUID id = OteEventMessageUtil.getUUID(msg);
			if(!id.equals(MYID)){
				OteEventMessageUtil.setUUID(msg, MYID);
				Map<String, Object> data = new HashMap<String, Object>();
				data.put(OteEventMessageUtil.BYTE_KEY, msg.getData());
				Event newevent = new Event(msg.getHeader().TOPIC.getValue(), data);
				eventAdmin.sendEvent(newevent);
			}
		} else {
			OseeLog.log(EventToJmsComponent.class, Level.SEVERE, "not a recognized message" + message.getClass());
		}
	}
}
