/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.test.msg.TestMessage;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public enum TestMessages implements MessageID {
	TestTopic(true, "ABVlXX3B9UaWogL++MgA", "topic:someTopic", null),
	JMS_TOPIC(true, "ABWApt8OtWlAnz5CJXQA", "topic:test.topic.Mynewthing.removeme", TestMessage.class),
	VM_TOPIC(true, "ABWHSTHuTlQb5xWueMAA", "topic:inThisJVM", TestMessage.class),
	test(true, "ABWoNNdp0RnrO5T5bWwA", "test", null),
	test2(true, "ABWpvHZTpBTR+PhVrwgA","test2", null),
	replyTopic(true, "ABWswvHPoR6RpnW9oGAA","topic:someTopicThatNeedsAReply", null, true);
	
	private String name;
	private Class<?> clazz;
	boolean isReplyRequired;
	private String guid;	
	private String destination;
	
	TestMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired){
		this.guid = guid;
		this.name = name;
		this.clazz = clazz;
		this.isReplyRequired = isReplyRequired;
		if(isTopic){
			destination = "topic:"+guid;
		} else {
			destination = guid;
		}
	}
	
	TestMessages(boolean isTopic, String guid,String name, Class<?> clazz){
		this.name = name;
		this.clazz = clazz;
		this.isReplyRequired = false;
		this.guid = guid;
		if(isTopic){
			destination = "topic:"+guid;
		} else {
			destination = guid;
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getSerializationClass() {
		return clazz;
	}

	@Override
	public boolean isReplyRequired() {
		return isReplyRequired;
	}

	@Override
	public String getGuid() {
		return guid;
	}

	@Override
	public String getMessageDestination() {
		return destination;
	}
	
   @Override
   public boolean isTopic() {
      return true;
   }
}
