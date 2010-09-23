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
import java.util.logging.Level;
import org.eclipse.osee.ats.access.AtsBranchObjectManager;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class AtsCmAccessControl implements CmAccessControl, HasAccessModel {

   private final AccessModel accessModel;
   private final AtsBranchObjectManager atsBranchObjectManager;

   public AtsCmAccessControl(AccessModel accessModel, AtsBranchObjectManager atsBranchObjectManager) {
      this.accessModel = accessModel;
      this.atsBranchObjectManager = atsBranchObjectManager;
   }

   @Override
   public boolean isApplicable(IBasicArtifact<?> user, Object object) {
      boolean result = false;
      if (object != null) {
         try {
            if (object instanceof Artifact) {
               result = atsBranchObjectManager.isApplicable(((Artifact) object).getBranch());
            } else if (object instanceof Branch) {
               result = atsBranchObjectManager.isApplicable((Branch) object);
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error determining access applicibility", ex);
         }
      }
      return result;
   }

   @Override
   public AccessModel getAccessModel() {
      return accessModel;
   }

   @Override
   public Collection<? extends AccessContextId> getContextId(IBasicArtifact<?> user, Object object) {
      AccessContextId contextId = null;
      if (object != null) {
         if (object instanceof Artifact) {
            contextId = atsBranchObjectManager.getContextId((Artifact) object);
         } else if (object instanceof Branch) {
            contextId = atsBranchObjectManager.getContextId((Branch) object);
         }
      }
      return contextId != null ? Collections.singletonList(contextId) : Collections.<AccessContextId> emptyList();
   }
}
