/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.ev;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class JaxWorkPackageData {
   String asUserId;
   List<Long> workItemUuids = new ArrayList<>();

   public JaxWorkPackageData() {
   }

   public List<Long> getWorkItemUuids() {
      return workItemUuids;
   }

   public void setWorkItemUuids(List<Long> workItemUuids) {
      this.workItemUuids = workItemUuids;
   }

   public String getAsUserId() {
      return asUserId;
   }

   public void setAsUserId(String asUserId) {
      this.asUserId = asUserId;
   }

}
