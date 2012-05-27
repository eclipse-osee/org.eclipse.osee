package org.eclipse.osee.ote.message.event;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

public class OteEventMessageUtil {
	
	public final static String BYTE_KEY = "oteeventbytes";
	
	public static void sendEvent(OteEventMessage message, EventAdmin eventAdmin) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(BYTE_KEY, message.getData());
		Event newevent = new Event(message.getHeader().TOPIC.getValue(), data);
		eventAdmin.sendEvent(newevent);
	}
	
	public static UUID getUUID(OteEventMessage msg){
		return new UUID(msg.getHeader().UUID_HIGH.getValue(), msg.getHeader().UUID_LOW.getValue());
	}
	
	public static void setUUID(OteEventMessage msg, UUID id){
		msg.getHeader().UUID_HIGH.setValue(id.getMostSignificantBits());
		msg.getHeader().UUID_LOW.setValue(id.getLeastSignificantBits());
	}

	public static OteEventMessage getOteByteMessage(Event event) {
		Object obj = event.getProperty(BYTE_KEY);
		if(obj != null && obj instanceof byte[]){
			return new OteEventMessage((byte[])obj);
		}
		return null;
	}

	public static byte[] getBytes(Event event) {
		Object obj = event.getProperty(BYTE_KEY);
		if(obj != null && obj instanceof byte[]){
			return (byte[])obj;
		} else {
			return null;
		}
	}

}
