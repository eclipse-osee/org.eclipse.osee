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
package org.eclipse.osee.ats.core.team;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.workflow.ITeamWorkflowProvider;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Donald G. Dunne
 */
public final class TeamWorkflowProviders {

   private static List<ITeamWorkflowProvider> teamWorkflowProvider;

   private TeamWorkflowProviders() {
      // private constructor
   }

   public static Set<IArtifactType> getAllTeamWorkflowArtifactTypes() throws OseeCoreException {
      Set<IArtifactType> artifactTypes = new HashSet<IArtifactType>();
      artifactTypes.add(AtsArtifactTypes.TeamWorkflow);
      for (ITeamWorkflowProvider ext : getAtsTeamWorkflowExtensions()) {
         artifactTypes.addAll(ext.getTeamWorkflowArtifactTypes());
      }
      return artifactTypes;
   }

   /*
    * due to lazy initialization, this function is non-reentrant therefore, the synchronized keyword is necessary
    */
   public synchronized static List<ITeamWorkflowProvider> getAtsTeamWorkflowExtensions() {
      if (teamWorkflowProvider == null) {

         ExtensionDefinedObjects<ITeamWorkflowProvider> objects =
            new ExtensionDefinedObjects<ITeamWorkflowProvider>("org.eclipse.osee.ats.core.AtsTeamWorkflowProvider",
               "AtsTeamWorkflowProvider", "classname");
         teamWorkflowProvider = objects.getObjects();

      }
      return teamWorkflowProvider;
   }

}
