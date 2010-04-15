package org.eclipse.osee.framework.messaging.event.res;

import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAccessControlArtifactsEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteAttributeChange1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBasicModifiedGuidArtifact1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchRenameEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBroadcastEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteChangeTypeArtifactsEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemotePurgedArtifactsEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionDeletedEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;

public enum ResMessages implements MessageID {
   RemoteAccessControlArtifactsEvent1(true, "Aylfa1sC4iArrIaXxugA", "topic:org.eclipse.osee.coverage.msgs.RemoteAccessControlArtifactsEvent1", RemoteAccessControlArtifactsEvent1.class, false),
   RemoteAttributeChange1(true, "Aylfa1tqjhm0dvCeWVQA", "topic:org.eclipse.osee.coverage.msgs.RemoteAttributeChange1", RemoteAttributeChange1.class, false),
   RemoteBasicGuidArtifact1(true, "Aylfa1uaknpjHWHAw8QA", "topic:org.eclipse.osee.coverage.msgs.RemoteBasicGuidArtifact1", RemoteBasicGuidArtifact1.class, false),
   RemoteBasicModifiedGuidArtifact1(true, "Aylfa1vJfz6i30DiUMQA", "topic:org.eclipse.osee.coverage.msgs.RemoteBasicModifiedGuidArtifact1", RemoteBasicModifiedGuidArtifact1.class, false),
   RemoteBranchEvent1(true, "Aylfa1wlKXIbX2gOrVgA", "topic:org.eclipse.osee.coverage.msgs.RemoteBranchEvent1", RemoteBranchEvent1.class, false),
   RemoteBranchRenameEvent1(true, "Aylfa1yHYFczc6m9GoAA", "topic:org.eclipse.osee.coverage.msgs.RemoteBranchRenameEvent1", RemoteBranchRenameEvent1.class, false),
   RemoteBroadcastEvent1(true, "Aylfa1y3ZBSIGbVU3JgA", "topic:org.eclipse.osee.coverage.msgs.RemoteBroadcastEvent1", RemoteBroadcastEvent1.class, false),
   RemoteChangeTypeArtifactsEvent1(true, "Aylfa1znaBzUzmyid9gA", "topic:org.eclipse.osee.coverage.msgs.RemoteChangeTypeArtifactsEvent1", RemoteChangeTypeArtifactsEvent1.class, false),
   RemoteNetworkSender1(true, "Aylfa10VPjPhpdOzeLQA", "topic:org.eclipse.osee.coverage.msgs.RemoteNetworkSender1", RemoteNetworkSender1.class, false),
   RemotePurgedArtifactsEvent1(true, "Aylfa11DE1DsqNVGkEgA", "topic:org.eclipse.osee.coverage.msgs.RemotePurgedArtifactsEvent1", RemotePurgedArtifactsEvent1.class, false),
   RemoteTransactionDeletedEvent1(true, "AISIbRbFbXOVTBRJsqQA", "topic:org.eclipse.osee.coverage.msgs.RemoteTransactionDeletedEvent1", RemoteTransactionDeletedEvent1.class, false),
   RemoteTransactionEvent1(true, "AISIbRj0KGBv62x2pMAA", "topic:org.eclipse.osee.coverage.msgs.RemoteTransactionEvent1", RemoteTransactionEvent1.class, false);

   private String name;
   private Class<?> clazz;
   boolean isReplyRequired;
   private String guid;
   private String destination;
   private boolean isTopic;

   ResMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired) {
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
