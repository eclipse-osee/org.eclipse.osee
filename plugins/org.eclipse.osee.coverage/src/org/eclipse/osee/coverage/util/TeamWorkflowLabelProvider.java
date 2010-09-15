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
package org.eclipse.osee.coverage.util;

import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.store.CoverageRelationTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;

/**
 * @author Donald G. Dunne
 */
public class TeamWorkflowLabelProvider extends ArtifactLabelProvider {

   public TeamWorkflowLabelProvider() {
      super();
   }

   @Override
   public String getText(Object element) {
      Artifact teamWf = (Artifact) element;
      try {
         Artifact version = getTargetedForVersion(teamWf);
         if (version != null) {
            return "[" + teamWf.getArtifactTypeName() + "][" + version.getName() + "] - " + teamWf.getName();
         } else {
            return "[" + teamWf.getArtifactTypeName() + "] - " + teamWf.getName();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   private Artifact getTargetedForVersion(Artifact teamWf) throws OseeCoreException {
      try {
         return teamWf.getRelatedArtifact(CoverageRelationTypes.TeamWorkflowTargetedForVersion_Version);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing;
      }
      return null;
   }
}
