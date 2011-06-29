/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionManagerCore {

   public static Collection<TeamWorkFlowArtifact> getTeams(Object object) throws OseeCoreException {
      if (object instanceof Artifact && ((Artifact) object).isOfType(AtsArtifactTypes.Action)) {
         return ((Artifact) object).getRelatedArtifacts(AtsRelationTypes.ActionToWorkflow_WorkFlow,
            TeamWorkFlowArtifact.class);
      }
      return java.util.Collections.emptyList();
   }

   public static TeamWorkFlowArtifact getFirstTeam(Object object) throws OseeCoreException {
      Collection<TeamWorkFlowArtifact> arts = getTeams(object);
      if (arts.size() > 0) {
         return arts.iterator().next();
      }
      return null;
   }

}
