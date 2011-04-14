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
package org.eclipse.osee.ats.internal;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.ats.access.AtsBranchAccessManager;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;

/**
 * ATS currently only provides access control for artifacts on a Team Workflow's working branch.
 * 
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class AtsCmAccessControl implements CmAccessControl, HasAccessModel {

   private final AccessModel accessModel;
   private final AtsBranchAccessManager atsBranchAccessManager;

   public AtsCmAccessControl(AccessModel accessModel, AtsBranchAccessManager atsBranchObjectManager) {
      this.accessModel = accessModel;
      this.atsBranchAccessManager = atsBranchObjectManager;
   }

   @Override
   public boolean isApplicable(IBasicArtifact<?> user, Object object) {
      boolean result = false;
      if (object != null && object instanceof Branch) {
         result = atsBranchAccessManager.isApplicable((Branch) object);
      }
      return result;
   }

   @Override
   public AccessModel getAccessModel() {
      return accessModel;
   }

   @Override
   public Collection<? extends IAccessContextId> getContextId(IBasicArtifact<?> user, Object object) {
      if (object != null && object instanceof Branch) {
         if (object instanceof Branch) {
            return atsBranchAccessManager.getContextId((Branch) object);
         }
      }
      return Collections.emptyList();
   }
}
