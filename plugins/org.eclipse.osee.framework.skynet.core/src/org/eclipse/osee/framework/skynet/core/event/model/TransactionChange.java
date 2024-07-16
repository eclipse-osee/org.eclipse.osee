/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.skynet.core.event.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.event.FrameworkEvent;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;

/**
 * @author Donald G. Dunne
 */
public class TransactionChange implements FrameworkEvent {

   private BranchId branch;
   private TransactionId transactionId;
   private final Set<DefaultBasicGuidArtifact> artifacts = new HashSet<>();

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   /**
    * Gets the value of the transactionId property.
    */
   public TransactionId getTransactionId() {
      return transactionId;
   }

   /**
    * Sets the value of the transactionId property.
    */
   public void setTransactionId(TransactionId value) {
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
      return "Transaction [branchId=" + branch + ", transId=" + transactionId + ", arts=" + artifacts + "]";
   }
}
