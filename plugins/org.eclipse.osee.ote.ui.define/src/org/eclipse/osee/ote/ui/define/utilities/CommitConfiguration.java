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
package org.eclipse.osee.ote.ui.define.utilities;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
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
         User user = UserManager.getUser();
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
