/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;

/**
 * @author Donald G. Dunne
 */
public class TransactionEvent implements FrameworkEvent, HasNetworkSender {

   private TransactionEventType eventType;
   private NetworkSender networkSender;
   private final List<TransactionChange> transactions = new ArrayList<>();

   /**
    * Gets the value of the artifacts property.
    * <p>
    * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
    * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
    * the artifacts property.
    * <p>
    * For example, to add a new item, do as follows:
    *
    * <pre>
    * getArtifacts().add(newItem);
    * </pre>
    * <p>
    * Objects of the following type(s) are allowed in the list {@link DefaultBasicGuidArtifact }
    */
   public List<TransactionChange> getTransactionChanges() {
      return transactions;
   }

   public void addTransactionChange(TransactionChange txChange) {
      transactions.add(txChange);
   }

   /**
    * Gets the value of the networkSender property.
    *
    * @return possible object is {@link NetworkSender }
    */
   @Override
   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   /**
    * Sets the value of the networkSender property.
    *
    * @param value allowed object is {@link NetworkSender }
    */
   @Override
   public void setNetworkSender(NetworkSender value) {
      this.networkSender = value;
   }

   public TransactionEventType getEventType() {
      return eventType;
   }

   public void setEventType(TransactionEventType eventType) {
      this.eventType = eventType;
   }

   @Override
   public String toString() {
      return "TransactionEvent [type=" + eventType + ", sender=" + networkSender + ", txs=" + transactions + "]";
   }

}
