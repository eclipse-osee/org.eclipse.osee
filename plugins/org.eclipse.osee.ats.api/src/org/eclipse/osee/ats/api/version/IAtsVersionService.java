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
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IAtsVersionService {

   boolean hasTargetedVersion(IAtsWorkItem workItem);

   IAtsVersion getTargetedVersion(IAtsWorkItem workItem);

   IAtsVersion getFoundInVersion(IAtsWorkItem workItem);

   Collection<IAtsTeamWorkflow> getTargetedForTeamWorkflows(IAtsVersion version);

   IAtsTeamDefinition getTeamDefinition(IAtsVersion version);

   void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef, IAtsChangeSet changes);

   boolean isReleased(IAtsTeamWorkflow teamWf);

   boolean isVersionLocked(IAtsTeamWorkflow teamWf);

   void removeTargetedVersion(IAtsTeamWorkflow teamWf, IAtsChangeSet changes);

   BranchId getBranch(IAtsVersion version);

   IAtsVersion getTargetedVersionByTeamWf(IAtsTeamWorkflow team);

   IAtsVersion setTargetedVersion(IAtsTeamWorkflow teamWf, IAtsVersion version, IAtsChangeSet changes);

   Version createVersion(IAtsProgram program, String versionName, IAtsChangeSet changes);

   IAtsVersion getVersion(IAtsProgram program, String versionName);

   Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef);

   Version getVersionById(ArtifactId versionId);

   Version createVersion(String title, long id, IAtsChangeSet changes);

   Version createVersion(String name, IAtsChangeSet changes);

   boolean isTeamUsesVersions(IAtsTeamDefinition teamDef);

   IAtsVersion getNextReleaseVersion(IAtsTeamDefinition teamDef);

   IAtsTeamDefinition getTeamDefinitionHoldingVersions(IAtsTeamDefinition teamDef);

   IAtsVersion getVersion(IAtsTeamDefinition teamDef, String name);

   Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef, VersionReleaseType releaseType, VersionLockedType lockedType);

   Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(IAtsTeamDefinition teamDef, VersionReleaseType releaseType, VersionLockedType lockedType);

   Collection<IAtsVersion> getVersionsLocked(IAtsTeamDefinition teamDef, VersionLockedType lockType);

   Collection<IAtsVersion> getVersionsReleased(IAtsTeamDefinition teamDef, VersionReleaseType releaseType);

   Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(IAtsTeamDefinition teamDef);

   BranchId getBaselineBranchIdInherited(IAtsVersion version);

   Result isAllowCommitBranchInherited(IAtsVersion version);

   Date getEstimatedReleaseDate(IAtsVersion version);

   Date getReleaseDate(IAtsVersion version);

   Result isAllowCreateBranchInherited(IAtsVersion version);

   Version createVersion(ArtifactToken verArt);

   String getTargetedVersionStr(IAtsWorkItem workItem, IAtsVersionService versionService);

   List<IAtsVersion> getParallelVersions(IAtsVersion version);

   void getParallelVersions(IAtsVersion version, Set<ICommitConfigItem> configArts);

   Version getVersionById(IAtsVersion versionId);

   boolean hasVersions(IAtsTeamDefinition teamDef);

}
