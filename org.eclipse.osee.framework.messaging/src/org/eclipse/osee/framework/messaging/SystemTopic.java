/*
 * Created on Aug 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

/**
 * @author b1122182
 */
public enum SystemTopic implements MessageID {

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

	@Override
	public String getMessageDestination() {
		return name;
	}

	@Override
	public Class<?> getSerializationClass() {
		return null;
	}

	@Override
	public boolean isReplyRequired() {
		return false;
	}

	@Override
	public String getGuid() {
		return name;
	}
}
