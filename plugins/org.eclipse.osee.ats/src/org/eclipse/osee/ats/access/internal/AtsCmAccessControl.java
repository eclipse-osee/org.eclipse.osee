/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.access.internal;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.access.AtsBranchAccessManager;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * ATS currently only provides access control for artifacts on a Team Workflow's working branch.
 *
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class AtsCmAccessControl implements CmAccessControl {

   private final AtsBranchAccessManager atsBranchAccessManager;

   public AtsCmAccessControl(AtsBranchAccessManager atsBranchObjectManager) {
      this.atsBranchAccessManager = atsBranchObjectManager;
   }

   @Override
   public boolean isApplicable(ArtifactToken user, Object object) {
      boolean result = false;
      if (object != null) {
         if (object instanceof Artifact && !((Artifact) object).isOnBranch(AtsClientService.get().getAtsBranch())) {
            result = atsBranchAccessManager.isApplicable(((Artifact) object).getBranch());
         }
         if (object instanceof BranchId) {
            result = atsBranchAccessManager.isApplicable((BranchId) object);
         }
      }
      return result;
   }

   @Override
   public Collection<? extends IAccessContextId> getContextId(ArtifactToken user, Object object) {
      if (object != null) {
         if (object instanceof Artifact && !((Artifact) object).isOnBranch(AtsClientService.get().getAtsBranch())) {
            return atsBranchAccessManager.getContextId(((Artifact) object).getBranch());
         }
         if (object instanceof BranchId) {
            return atsBranchAccessManager.getContextId((BranchId) object);
         }
      }
      return Collections.emptyList();
   }
}
