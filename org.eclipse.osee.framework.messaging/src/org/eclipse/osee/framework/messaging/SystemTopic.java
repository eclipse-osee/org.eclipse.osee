/*
 * Created on Aug 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author b1122182
 */
public class SystemTopic extends NamedIdentity implements MessageID {
   public static final SystemTopic JMS_HEALTH_STATUS = new SystemTopic("topic:jms.health.status");
   public static final SystemTopic KILL_TEST_JMS_BROKER = new SystemTopic("topic:jms.kill.broker");

   SystemTopic(String name) {
      super(name, name);
   }

   @Override
   public String getMessageDestination() {
      return getName();
   }

   @Override
   public Class<?> getSerializationClass() {
      return null;
   }

   @Override
   public boolean isReplyRequired() {
      return false;
   }
}