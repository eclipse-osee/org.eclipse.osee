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
import java.util.logging.Level;
import org.eclipse.osee.ats.access.AtsBranchObjectManager;
import org.eclipse.osee.ats.access.AtsObjectAccessManager;
import org.eclipse.osee.ats.access.IAtsAccessControlService;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessModel;
import org.eclipse.osee.framework.core.model.access.HasAccessModel;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 * @author Donald G. Dunne
 */
public class AtsCmAccessControl implements CmAccessControl, HasAccessModel {

   private final AccessModel accessModel;
   private final AtsObjectAccessManager atsObjectAccessManager;
   private final AtsBranchObjectManager atsBranchObjectManager;

   public AtsCmAccessControl(AccessModel accessModel, Collection<IAtsAccessControlService> atsAccessServices) {
      this.accessModel = accessModel;
      atsBranchObjectManager = new AtsBranchObjectManager(atsAccessServices);
      atsObjectAccessManager = new AtsObjectAccessManager();
   }

   @Override
   public boolean isApplicable(IBasicArtifact<?> user, Object object) {
      boolean result = false;
      if (object instanceof Artifact) {
         try {
            result = isAtsObject((Artifact) object) || isAtsCmManagedObject((Artifact) object);
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error determining access applicibility", ex);
         }
      }
      return result;
   }

   public boolean isAtsCmManagedObject(Artifact artifact) throws OseeCoreException {
      return (!artifact.getBranch().equals(AtsUtil.getAtsBranch()) &&
      //
      ArtifactQuery.getArtifactFromId(artifact.getBranch().getAssociatedArtifactId(), AtsUtil.getAtsBranch()).isOfType(
         AtsArtifactTypes.TeamWorkflow));
   }

   public boolean isAtsObject(Artifact artifact) throws OseeCoreException {
      return artifact.getBranch().equals(AtsUtil.getAtsBranch()) && artifact.isOfType(
         AtsArtifactTypes.StateMachineArtifact, AtsArtifactTypes.Action, AtsArtifactTypes.Version,
         AtsArtifactTypes.TeamDefinition);
   }

   @Override
   public AccessModel getAccessModel() {
      return accessModel;
   }

   @Override
   public Collection<? extends AccessContextId> getContextId(IBasicArtifact<?> user, Object object) throws OseeCoreException {
      if (object instanceof Artifact) {
         if (((Artifact) object).getBranch().equals(AtsUtil.getAtsBranch())) {
            return atsObjectAccessManager.getContextId(user, object, AtsUtil.isAtsAdmin());
         } else {
            return atsBranchObjectManager.getContextId(user, object);
         }
      }
      return null;
   }
}
