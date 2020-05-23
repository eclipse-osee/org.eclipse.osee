/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.disposition.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Angel Avila
 */
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
