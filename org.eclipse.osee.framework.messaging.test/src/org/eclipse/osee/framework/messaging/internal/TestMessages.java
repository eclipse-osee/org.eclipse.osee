/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.messaging.MessageName;

/**
 * @author b1528444
 *
 */
public enum TestMessages implements MessageName {
	TestTopic("topic:someTopic"),
	JMS_TOPIC("topic:test.topic.Mynewthing.removeme"),
	VM_TOPIC("topic:inThisJVM"),
	test("test"),
	test2("test2");
	
	private String name;
	
	TestMessages(String name){
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

}
