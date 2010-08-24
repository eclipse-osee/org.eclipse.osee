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
package org.eclipse.osee.ats.access;

import java.util.logging.Level;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.framework.core.data.AccessContextId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * This class will return access context ids for ATS Objects stored on Common branch
 */
public class AtsObjectAccessManager implements AtsAccessContextIdResolver {

   private final IOseeBranch atsBranch;

   public AtsObjectAccessManager(IOseeBranch atsBranch) {
      this.atsBranch = atsBranch;
   }

   public boolean isApplicable(IOseeBranch objectBranch, ArtifactType objectType) {
      return atsBranch.equals(objectBranch) && objectType.inheritsFrom(AtsArtifactTypes.StateMachineArtifact,
         AtsArtifactTypes.Action, AtsArtifactTypes.Version, AtsArtifactTypes.TeamDefinition);
   }

   /**
    * Return context id for editing ATS Objects on common branch
    */
   public AccessContextId getContextId(ArtifactType artifactType, boolean isAdmin) {
      AccessContextId toReturn = null;
      try {
         if (artifactType.inheritsFrom(CoreArtifactTypes.WorkItemDefinition)) {
            toReturn = AtsObjectContextId.WORK_ITEM_CONFIG_CONTEXT;
         } else if (artifactType.inheritsFrom(AtsArtifactTypes.TeamDefinition) || artifactType.inheritsFrom(AtsArtifactTypes.ActionableItem)) {
            if (isAdmin) {
               toReturn = AtsObjectContextId.TEAM_AND_ACCESS_CONFIG_CONTEXT;
            } else {
               toReturn = AtsObjectContextId.TEAM_CONFIG_CONTEXT;
            }
         } else if (artifactType.inheritsFrom(AtsArtifactTypes.StateMachineArtifact)) {
            toReturn = AtsObjectContextId.WORKFLOW_ADMIN_CONTEXT;
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Exception obtaining ATS Object Access Context Id; Deny returned",
            ex);
         toReturn = AtsObjectContextId.DENY_CONTEXT;
      }
      return toReturn;
   }
}
