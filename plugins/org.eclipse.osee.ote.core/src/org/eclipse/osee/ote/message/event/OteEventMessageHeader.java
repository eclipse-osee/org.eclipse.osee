package org.eclipse.osee.ote.message.event;

import org.eclipse.osee.ote.message.IMessageHeader;
import org.eclipse.osee.ote.message.data.HeaderData;
import org.eclipse.osee.ote.message.data.MemoryResource;
import org.eclipse.osee.ote.message.elements.Element;
import org.eclipse.osee.ote.message.elements.IntegerElement;
import org.eclipse.osee.ote.message.elements.LongIntegerElement;
import org.eclipse.osee.ote.message.elements.StringElement;

public class OteEventMessageHeader implements IMessageHeader{

	public static final int HEADER_SIZE = 90; 
	public static final int MARKER_VALUE = 0xFADE;
	private final HeaderData headerData;
	private final Object[] paths;

	public final IntegerElement MARKER;
	public final StringElement TOPIC;
//	public final IntegerElement MESSAGE_ID;
	public final LongIntegerElement UUID_LOW;
	public final LongIntegerElement UUID_HIGH;
	public final IntegerElement TTL;
	private String name;

	public OteEventMessageHeader(OteEventMessage msg, String topic, MemoryResource data) {
	  this.name = msg.getName();
	  headerData = new HeaderData("OteByteMessageHeader", data);
      paths = new Object[]{(msg == null ? "message" : msg.getClass().getName()), "HEADER(OteByteMessageHeader)"};
      MARKER = new IntegerElement(msg, "MARKER", headerData, 0, 0, 15);
      TOPIC = new StringElement(msg, "TOPIC", headerData, 2, 0, 8*64);
//      MESSAGE_ID = new IntegerElement(msg, "MESSAGE_ID", headerData, 66, 0, 31);
      UUID_LOW = new LongIntegerElement(msg, "UUID_LOW", headerData, 70, 0, 63);
      UUID_HIGH = new LongIntegerElement(msg, "UUID_HIGH", headerData, 78, 0, 63);
      TTL = new IntegerElement(msg, "TTL", headerData, 86, 0, 31);
      TOPIC.setValue(topic);
      MARKER.setValue(MARKER_VALUE);
//      MESSAGE_ID.setValue(messageId);
      addElement(MARKER);
      addElement(TOPIC);
//      addElement(MESSAGE_ID);
	}
	
	private <T extends Element> T addElement(T instance) {
		instance.addPath(paths);
		return instance;
	}

	@Override
	public int getHeaderSize() {
		return HEADER_SIZE;
	}

	@Override
	public byte[] getData() {
		return headerData.toByteArray();
	}

	@Override
	public Element[] getElements() {
		return new Element[]{MARKER, TOPIC};
	}

	@Override
	public void setNewBackingBuffer(byte[] data) {
		headerData.setNewBackingBuffer(data);
	}

	@Override
	public String toXml() {
      StringBuilder builder = new StringBuilder(256);
      builder.append("<OteByteMessageHeader> ").
         append("MARKER=\"").append(MARKER.getValue()).append("\" ").
         append("TOPIC=\"").append(TOPIC.getValue()).append("\" ").
//         append("MESSAGE_ID=\"").append(MESSAGE_ID.getValue()).append("\" ").
      append("</OteByteMessageHeader>");
      return builder.toString();
	}

   @Override
   public String getMessageName() {
      return name;
   }

}
