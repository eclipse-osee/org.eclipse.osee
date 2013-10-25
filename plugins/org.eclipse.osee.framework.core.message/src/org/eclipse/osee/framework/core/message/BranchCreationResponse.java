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
package org.eclipse.osee.framework.core.message;

/**
 * @author Roberto E. Escobar
 */
public class BranchCreationResponse {

   private long branchId;

   public BranchCreationResponse(long branchId) {
      this.branchId = branchId;
   }

   public long getBranchId() {
      return branchId;
   }

   public void setBranchId(long branchId) {
      this.branchId = branchId;
   }

   @Override
   public String toString() {
      return "BranchCreationResponse [branchId=" + branchId + "]";
   }
}
