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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;

public class TransactionChange implements FrameworkEvent {

   private long branchUuid;
   private int transactionId;
   private final Set<DefaultBasicGuidArtifact> artifacts = new HashSet<>();

   /**
    * Gets the value of the branchUuid property.
    *
    * @return possible object is {@link String }
    */
   public long getBranchUuid() {
      return branchUuid;
   }

   /**
    * Sets the value of the branchUuid property.
    *
    * @param value allowed object is {@link String }
    */
   public void setBranchUuid(long value) {
      this.branchUuid = value;
   }

   /**
    * Gets the value of the transactionId property.
    */
   public int getTransactionId() {
      return transactionId;
   }

   /**
    * Sets the value of the transactionId property.
    */
   public void setTransactionId(int value) {
      this.transactionId = value;
   }

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
   public Collection<DefaultBasicGuidArtifact> getArtifacts() {
      return artifacts;
   }

   @Override
   public String toString() {
      return "Transaction [branchId=" + branchUuid + ", transId=" + transactionId + ", arts=" + artifacts + "]";
   }
}
