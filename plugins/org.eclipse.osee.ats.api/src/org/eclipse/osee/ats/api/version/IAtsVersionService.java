/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.version;

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsVersionService {

   boolean hasTargetedVersion(IAtsWorkItem workItem);

   IAtsVersion getTargetedVersion(IAtsWorkItem workItem);

   Collection<IAtsTeamWorkflow> getTargetedForTeamWorkflows(IAtsVersion version);

   IAtsTeamDefinition getTeamDefinition(IAtsVersion version);

   void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef, IAtsChangeSet changes);

   boolean isReleased(IAtsTeamWorkflow teamWf);

   boolean isVersionLocked(IAtsTeamWorkflow teamWf);

   void removeTargetedVersion(IAtsTeamWorkflow teamWf, IAtsChangeSet changes);

   IAtsVersion getById(ArtifactId id);

   BranchId getBranch(IAtsVersion version);

   IAtsVersion getTargetedVersionByTeamWf(IAtsTeamWorkflow team);

   IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version, IAtsChangeSet changes);

   IAtsVersion createVersion(IAtsProgram program, String versionName, IAtsChangeSet changes);

   IAtsVersion getVersion(IAtsProgram program, String versionName, IAtsChangeSet changes);

   IAtsVersion createVersion(String name, long id, IAtsChangeSet changes);

   IAtsVersion createVersion(String name, IAtsChangeSet changes);

   Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef);

   IAtsVersion getVersion(ArtifactId versionId);

}
