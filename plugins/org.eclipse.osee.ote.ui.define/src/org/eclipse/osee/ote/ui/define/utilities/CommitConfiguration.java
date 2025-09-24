/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ote.ui.define.utilities;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class CommitConfiguration {
   private static final CommitConfiguration instance = new CommitConfiguration();

   private CommitConfiguration() {
   }

   private boolean isUserAllowedToOverride() {
      boolean isAllowed = false;

      try {
         User user = OseeApiService.getUserArt();
         Collection<Artifact> teams = user.getRelatedArtifacts(AtsRelationTypes.TeamMember_Team);
         for (Artifact team : teams) {
            if (team.getName().equals("OSEE")) {
               isAllowed = true;
            }
         }
      } catch (Exception ex) {
         // Do Nothing!
      }
      return isAllowed;
   }

   public static boolean isCommitOverrideAllowed() {
      return instance.isUserAllowedToOverride();
   }
}
