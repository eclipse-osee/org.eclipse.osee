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
package org.eclipse.osee.ats.util;

import java.util.Collection;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * Convenience methods to bulk load ATS objects based on currently held objects
 * 
 * @author Donald G. Dunne
 */
public class AtsBulkLoad {

   public static Set<Artifact> loadFromActions(Collection<? extends Artifact> actions) throws OseeCoreException {
      return RelationManager.getRelatedArtifacts(actions, 4, AtsRelationTypes.SmaToTask_Task,
            AtsRelationTypes.ActionToWorkflow_WorkFlow, AtsRelationTypes.TeamWorkflowToReview_Review);
   }

   public static Set<Artifact> loadFromTeamWorkflows(Collection<? extends Artifact> teams) throws OseeCoreException {
      return RelationManager.getRelatedArtifacts(teams, 3, AtsRelationTypes.SmaToTask_Task,
            AtsRelationTypes.TeamWorkflowToReview_Team, AtsRelationTypes.ActionToWorkflow_Action);
   }
}
