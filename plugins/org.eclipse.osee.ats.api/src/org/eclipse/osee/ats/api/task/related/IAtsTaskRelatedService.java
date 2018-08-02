/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.task.related;

import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTaskRelatedService {

   public static final String IMPL_DETAILS = " (Impl Details)";
   public static final String DELETED = " (Deleted)";

   IAtsTeamWorkflow getDefivedFromTeamWf(IAtsTask task);

   boolean isCodeWorkflow(IAtsTeamWorkflow teamWf);

   boolean isRequirementsWorkflow(IAtsTeamWorkflow teamWf);

   TaskRelatedData getRelatedRequirementArtifact(IAtsTask task, IAtsTeamWorkflow teamWf, ArtifactId relatedArtifact);

   TaskRelatedData getRelatedRequirementArtifactFromChangeReport(IAtsTeamWorkflow derivedFromTeamWf, IAtsTask task);

   ArtifactToken findHeadArtifact(IAtsTeamWorkflow reqTeam, ArtifactId relatedArtifact, String addDetails);

}
