package org.eclipse.osee.coverage.event;

import org.eclipse.osee.framework.messaging.MessageID;

public enum CoverageMessages implements MessageID {
   CoveragePackageSave(true, "Aylfa1rRxx6NQf4MfNwA", "topic:org.eclipse.osee.coverage.msgs.CoveragePackageSave", org.eclipse.osee.coverage.msgs.CoveragePackageSave.class, false);

   private String name;
   private Class<?> clazz;
   boolean isReplyRequired;
   private String guid;
   private String destination;
   private boolean isTopic;

   CoverageMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired) {
      this.guid = guid;
      this.name = name;
      this.clazz = clazz;
      this.isReplyRequired = isReplyRequired;
      this.isTopic = isTopic;
      if (isTopic) {
         destination = "topic:" + guid;
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

   public boolean isTopic() {
      return isTopic;
   }
}
