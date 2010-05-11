/*
 * Created on Aug 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

/**
 * @author Roberto E. Escobar
 */
public enum SystemTopic implements MessageID {

	JMS_HEALTH_STATUS("topic:jms.health.status"),
	KILL_TEST_JMS_BROKER("jms.kill.broker");

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

   @Override
   public boolean isTopic() {
      return true;
   }
}
