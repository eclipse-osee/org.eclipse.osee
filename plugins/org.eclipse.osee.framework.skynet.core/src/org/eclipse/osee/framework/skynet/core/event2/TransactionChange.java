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
package org.eclipse.osee.framework.skynet.core.event2;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;

public class TransactionChange extends FrameworkEvent {

   private String branchGuid;
   private int transactionId;
   private List<DefaultBasicGuidArtifact> artifacts;

   /**
    * Gets the value of the branchGuid property.
    * 
    * @return possible object is {@link String }
    */
   public String getBranchGuid() {
      return branchGuid;
   }

   /**
    * Sets the value of the branchGuid property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setBranchGuid(String value) {
      this.branchGuid = value;
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
   public List<DefaultBasicGuidArtifact> getArtifacts() {
      if (artifacts == null) {
         artifacts = new ArrayList<DefaultBasicGuidArtifact>();
      }
      return this.artifacts;
   }

}
