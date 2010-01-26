/*
 * Created on Aug 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

/**
 * @author b1122182
 */
public enum SystemTopic implements MessageName {

	JMS_HEALTH_STATUS("topic:jms.health.status"),
	KILL_TEST_JMS_BROKER("topic:jms.kill.broker");

	private String name;

	SystemTopic(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}
}
