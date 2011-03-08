/*
 * Created on Mar 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.access.internal;

import java.util.List;

import org.eclipse.osee.framework.access.AccessControlData;
import org.eclipse.osee.framework.access.AccessObject;
import org.eclipse.osee.framework.access.internal.data.BranchAccessObject;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Jeff C. Phillips
 */
public class AccessControlCacheHandler {

   public void updateAccessListForBranchObject(AccessControlService service, final String branchGuid) throws OseeCoreException {
      BranchAccessObject branchAccessObject = BranchAccessObject.getBranchAccessObject(branchGuid);
      updateAccessList(service, branchAccessObject);
   }

   public void updateAccessList(AccessControlService service, AccessObject accessObject) throws OseeCoreException {
      List<AccessControlData> acl = service.generateAccessControlList(accessObject);
      for (AccessControlData accessControlData : acl) {
         service.removeAccessControlDataIf(false, accessControlData);
      }
   }

   public void reloadCache(AccessControlService service) throws OseeCoreException {
      service.reloadCache();
   }
}
