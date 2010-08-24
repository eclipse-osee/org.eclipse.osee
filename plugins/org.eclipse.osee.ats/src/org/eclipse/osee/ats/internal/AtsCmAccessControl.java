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
import org.eclipse.osee.ats.access.AtsObjectAccessManager;
import org.eclipse.osee.ats.access.IAtsAccessControlService;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class AtsCmAccessControl implements CmAccessControl, HasAccessModel {

   private final Branch atsBranch;
   private final AccessModel accessModel;
   private final AtsObjectAccessManager atsObjectAccessManager;
   private final AtsBranchObjectManager atsBranchObjectManager;

   public AtsCmAccessControl(Branch atsBranch, AccessModel accessModel, Collection<IAtsAccessControlService> atsAccessServices) {
      this.atsBranch = atsBranch;
      this.accessModel = accessModel;
      atsBranchObjectManager = new AtsBranchObjectManager(atsBranch, atsAccessServices);
      atsObjectAccessManager = new AtsObjectAccessManager(atsBranch);
   }

   @Override
   public boolean isApplicable(IBasicArtifact<?> user, Object object) {
      boolean result = false;
      Artifact artifact = asCastedObject(object);
      if (artifact != null) {
         try {
            result =
               atsObjectAccessManager.isApplicable(artifact.getBranch(), artifact.getArtifactType()) || atsBranchObjectManager.isApplicable(artifact.getBranch());
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

   private Artifact asCastedObject(Object object) {
      Artifact artifact = null;
      if (object instanceof Artifact) {
         artifact = (Artifact) object;
      }
      return artifact;
   }

   @Override
   public Collection<? extends AccessContextId> getContextId(IBasicArtifact<?> user, Object object) {
      AccessContextId contextId = null;
      Artifact artifact = asCastedObject(object);
      if (artifact != null) {
         if (atsBranch.equals(artifact.getBranch())) {
            ArtifactType artifactType = artifact.getArtifactType();
            contextId = atsObjectAccessManager.getContextId(artifactType, AtsUtil.isAtsAdmin());
         } else {
            contextId = atsBranchObjectManager.getContextId(artifact);
         }
      }
      return contextId != null ? Collections.singletonList(contextId) : Collections.<AccessContextId> emptyList();
   }
}
