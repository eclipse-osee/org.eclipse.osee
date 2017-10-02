/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal;

import java.util.List;
import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.access.internal.data.BranchAccessObject;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Jeff C. Phillips
 */
public class AccessControlCacheHandler {

   public void updateAccessListForBranchObject(AccessControlService service, BranchId branch)  {
      BranchAccessObject branchAccessObject = BranchAccessObject.getBranchAccessObject(branch);
      if (branchAccessObject != null) {
         updateAccessList(service, branchAccessObject);
      }
   }

   public void updateAccessList(AccessControlService service, AccessObject accessObject)  {
      List<AccessControlData> acl = service.generateAccessControlList(accessObject);
      for (AccessControlData accessControlData : acl) {
         service.removeAccessControlDataIf(false, accessControlData);
      }
   }

   public void reloadCache(AccessControlService service)  {
      service.reloadCache();
   }
}
