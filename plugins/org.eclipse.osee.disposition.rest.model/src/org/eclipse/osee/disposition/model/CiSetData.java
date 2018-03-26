/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CiSetData")
public class CiSetData {

   private String ciSetName;
   private String dispoSetId;
   private String branchId;

   public String getCiSetName() {
      return ciSetName;
   }

   public void setCiSetName(String setName) {
      this.ciSetName = setName;
   }

   public String getDispoSetId() {
      return dispoSetId;
   }

   public void setDispoSetId(String dispoSetId) {
      this.dispoSetId = dispoSetId;
   }

   public String getBranchId() {
      return branchId;
   }

   public void setBranchId(String branchId) {
      this.branchId = branchId;
   }

}
