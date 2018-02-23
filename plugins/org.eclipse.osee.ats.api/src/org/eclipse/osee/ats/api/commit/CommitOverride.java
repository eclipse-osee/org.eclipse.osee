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
package org.eclipse.osee.ats.api.commit;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;

/**
 * @author Donald G. Dunne
 */
public class CommitOverride {

   private UserId user;
   private String branchId;
   private String reason;

   public String getReason() {
      return reason;
   }

   public void setReason(String reason) {
      this.reason = reason;
   }

   public UserId getUser() {
      return user;
   }

   public void setUser(UserId user) {
      this.user = user;
   }

   public String getBranchId() {
      return branchId;
   }

   public void setBranchId(String branchId) {
      this.branchId = branchId;
   }

   public static CommitOverride valueOf(UserId user, BranchId branch, String reason) {
      CommitOverride co = new CommitOverride();
      co.setUser(user);
      co.setBranchId(branch.getIdString());
      co.setReason(reason);
      return co;
   }

}
