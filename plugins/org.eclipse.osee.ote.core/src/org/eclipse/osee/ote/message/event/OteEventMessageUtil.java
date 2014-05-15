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
package org.eclipse.osee.ote.message.event;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.UUID;

import org.eclipse.osee.ote.core.ServiceUtility;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;

public class OteEventMessageUtil {
	
	public final static String BYTE_KEY = "oteeventbytes";
	
	private static EventAdmin eventAdmin;
	
	private static EventAdmin getEventAdmin(){
	   if(eventAdmin == null){
	      eventAdmin = ServiceUtility.getService(EventAdmin.class);
	   }
	   return eventAdmin;
	}
	
   public static void sendEvent(OteEventMessage message) {
      sendEvent(message, getEventAdmin());
   }
   
   public static void postEvent(OteEventMessage message) {
      postEvent(message, getEventAdmin());
   }

   public static void sendEvent(OteEventMessage message, EventAdmin eventAdmin) {
      message.getHeader().UUID_HIGH.setNoLog((long) 0x0);
      message.getHeader().UUID_LOW.setNoLog((long) 0x0);
      Map<String, Object> data = new HashMap<String, Object>();
      data.put(BYTE_KEY, message.getData());
      Event newevent = new Event(message.getHeader().TOPIC.getValue(), data);
      eventAdmin.sendEvent(newevent);
   }
   
   public static void postEvent(OteEventMessage message, EventAdmin eventAdmin) {
      message.getHeader().UUID_HIGH.setNoLog((long) 0x0);
      message.getHeader().UUID_LOW.setNoLog((long) 0x0);
      Map<String, Object> data = new HashMap<String, Object>();
      data.put(BYTE_KEY, message.getData());
      Event newevent = new Event(message.getHeader().TOPIC.getValue(), data);
      eventAdmin.postEvent(newevent);
   }

   public static UUID getUUID(OteEventMessage msg) {
      return new UUID(msg.getHeader().UUID_HIGH.getValue(), msg.getHeader().UUID_LOW.getValue());
   }
   
   public static UUID getUUID(byte[] data) {
      long low = getLong(data, 74);
      long high = getLong(data, 82);
      return new UUID(high, low);
   }

   private static long getLong(byte[] data, int index){
      return
      (long)(0xff & data[index]) << 56  |
      (long)(0xff & data[index+1]) << 48  |
      (long)(0xff & data[index+2]) << 40  |
      (long)(0xff & data[index+3]) << 32  |
      (long)(0xff & data[index+4]) << 24  |
      (long)(0xff & data[index+5]) << 16  |
      (long)(0xff & data[index+6]) << 8   |
      (long)(0xff & data[index+7]) << 0;
   }
   
   public static void setUUID(OteEventMessage msg, UUID id) {
      msg.getHeader().UUID_HIGH.setValue(id.getMostSignificantBits());
      msg.getHeader().UUID_LOW.setValue(id.getLeastSignificantBits());
   }

   public static OteEventMessage getOteEventMessage(Event event) {
      Object obj = event.getProperty(BYTE_KEY);
      if (obj != null && obj instanceof byte[]) {
         return new OteEventMessage((byte[]) obj);
      }
      return null;
   }
   
   public static byte[] getBytes(Event event) {
      Object obj = event.getProperty(BYTE_KEY);
      if (obj != null && obj instanceof byte[]) {
         return (byte[]) obj;
      } else {
         return null;
      }
   }

   public static void putBytes(Event event, OteEventMessage signal) {
      signal.getDefaultMessageData().getMem().setData(getBytes(event));
   }

   public static ServiceRegistration<EventHandler> subscribe(String topic, EventHandler handler){
      Dictionary<String, String> props = new Hashtable<String, String>();
      props.put("event.topics", topic);
      return ServiceUtility.getContext().registerService(EventHandler.class, handler, props);
   }
   
   public static ServiceRegistration<EventHandler> subscribe(OteEventMessage signal, EventHandler eventHandler) {
      BundleContext context = ServiceUtility.getContext();
      if (context == null) {
         return null;
      }
      Hashtable<String, Object> props = new Hashtable<String, Object>();
      props.put("event.topics", signal.getHeader().TOPIC.getValue());
      return context.registerService(EventHandler.class, eventHandler, props);
   }

}
