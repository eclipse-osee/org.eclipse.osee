/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.ev;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JaxWorkPackageData {
   String asUserId;
   List<Long> workItemIds = new ArrayList<>();

   public JaxWorkPackageData() {
   }

   public List<Long> getWorkItemIds() {
      return workItemIds;
   }

   public void setWorkItemIds(List<Long> workItemIds) {
      this.workItemIds = workItemIds;
   }

   public String getAsUserId() {
      return asUserId;
   }

   public void setAsUserId(String asUserId) {
      this.asUserId = asUserId;
   }

}
