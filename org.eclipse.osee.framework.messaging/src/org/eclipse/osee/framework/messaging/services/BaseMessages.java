/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services;

import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealthRequest;

/**
 * @author b1528444
 */
public class BaseMessages extends NamedIdentity implements MessageID {
   public static final BaseMessages ServiceHealth =
         new BaseMessages(true, "ABjyjamBQRvvAGcWpRQA", "topic:osee.message.core.ServiceHealth", ServiceHealth.class,
               false);
   public static final BaseMessages ServiceHealthRequest =
         new BaseMessages(true, "ABkAHOSFQ3VUZcfzsAgA", "topic:osee.message.core.ServiceHealthRequest",
               ServiceHealthRequest.class, true);

   private final Class<?> clazz;
   boolean isReplyRequired;
   private String destination;

   private BaseMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired) {
      super(guid, name);
      this.clazz = clazz;
      this.isReplyRequired = isReplyRequired;
      if (isTopic) {
         destination = "topic:" + guid;
      } else {
         destination = guid;
      }
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
   public String getMessageDestination() {
      return destination;
   }
}