/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.messaging.event.res;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteNetworkSender1;

/**
 * @author Hugo Trejo, David Miller, Torin Grenda
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemoteArtifactTopicEvent", propOrder = {"topic", "properties", "networkSender", "transactionId"})
public class RemoteArtifactTopicEvent extends RemoteEvent {
   public static final String BRANCH_ID = "branch";
   public static final String ARTIFACTS = "artifacts";
   public static final String RELATIONS = "relations";
   public static final String RELATION_REORDER_RECORDS = "relation.reorder.records";
   public static final String RELOAD_EVENT = "reload.event";

   @XmlElement(required = true)
   protected String topic;
   protected Map<String, String> properties = new HashMap<>();
   @XmlElement(required = true)
   protected RemoteNetworkSender1 networkSender;
   protected Long transactionId;

   @Override
   public RemoteNetworkSender1 getNetworkSender() {
      return networkSender;
   }

   public void setNetworkSender(RemoteNetworkSender1 value) {
      this.networkSender = value;
   }

   public Long getTransactionId() {
      return transactionId;
   }

   public void setTransactionId(Long transactionId) {
      this.transactionId = transactionId;
   }

   public String getTopic() {
      return topic;
   }

   public void setTopic(String topic) {
      this.topic = topic;
   }

   public Map<String, String> getProperties() {
      return properties;
   }

   public void setProperties(Map<String, String> properties) {
      this.properties = properties;
   }

}
