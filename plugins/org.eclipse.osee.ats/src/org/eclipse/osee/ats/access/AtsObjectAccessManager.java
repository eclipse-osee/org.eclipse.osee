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
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * This class will return access context ids for ATS Objects stored on Common branch
 */
public class AtsObjectAccessManager {

   /**
    * Return context id for editing ATS Objects on common branch
    */
   public static AtsObjectContextId getAtsObjectContextId(Artifact artifact, boolean includeAccessControl) {
      try {
         if (artifact.isOfType(CoreArtifactTypes.WorkItemDefinition)) {
            return AtsObjectContextId.WORK_ITEM_CONFIG_CONTEXT;
         } else if (artifact.isOfType(AtsArtifactTypes.TeamDefinition) || artifact.isOfType(AtsArtifactTypes.ActionableItem)) {
            if (includeAccessControl) {
               return AtsObjectContextId.TEAM_AND_ACCESS_CONFIG_CONTEXT;
            } else {
               return AtsObjectContextId.TEAM_CONFIG_CONTEXT;
            }
         } else if (artifact.isOfType(AtsArtifactTypes.StateMachineArtifact)) {
            return AtsObjectContextId.WORKFLOW_ADMIN_CONTEXT;
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, "Exception obtaining ATS Object Access Context Id; Deny returned",
            ex);
         return AtsObjectContextId.DENY_CONTEXT;
      }
      return AtsObjectContextId.DEFAULT_ATSOBJECT_CONTEXT;
   }

}
