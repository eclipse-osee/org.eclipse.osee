/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.api.config.tx;

import org.eclipse.osee.ats.api.config.Csci;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.query.NextRelease;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigTxTeamDef {

   IAtsConfigTxTeamDef andWorkDef(NamedId id);

   IAtsConfigTxTeamDef andActive(boolean active);

   IAtsConfigTxTeamDef and(AttributeTypeToken attrType, Object value);

   IAtsConfigTxTeamDef andLeads(UserToken... leads);

   IAtsConfigTxTeamDef andMembers(UserToken... members);

   IAtsConfigTxTeamDef andTeamWorkflowArtifactType(ArtifactTypeToken artifactType);

   /**
    * @param taskWorkDef One or more task work definitions. If 0, default will be used. If 1, it will be used. If > 1,
    * user will be given a choice when creating tasks.
    */
   IAtsConfigTxTeamDef andRelatedTaskWorkflowDefinition(NamedId... taskWorkDefs);

   IAtsConfigTxTeamDef andVersion(String name, ReleasedOption released, BranchToken branch, NextRelease nextRelease, IAtsVersionArtifactToken... parallelVersions);

   IAtsConfigTxTeamDef andParallelVersion(IAtsVersion ver1, IAtsVersion ver2);

   IAtsConfigTxTeamDef createChildTeamDef(IAtsTeamDefinition parent, IAtsTeamDefinitionArtifactToken teamDef);

   IAtsConfigTxTeamDef createChildTeamDef(String name);

   IAtsTeamDefinition getTeamDef();

   IAtsConfigTxVersion andVersionTx(IAtsVersionArtifactToken version, ReleasedOption released, BranchToken branch, NextRelease nextRelease, IAtsVersionArtifactToken... parallelVersions);

   IAtsConfigTxTeamDef andVersion(IAtsVersionArtifactToken version, ReleasedOption released, BranchToken branch, NextRelease nextRelease, IAtsVersionArtifactToken... parallelVersions);

   IAtsConfigTxTeamDef andVersion(IAtsVersionArtifactToken... verToks);

   /**
    * @param peerWorkDefs One or more peer work definitions. If 0, default will be used. If 1, it will be used. If > 1,
    * user will be given a choice when creating reviews.
    */
   IAtsConfigTxTeamDef andRelatedPeerWorkflowDefinition(NamedId... peerWorkDefs);

   IAtsConfigTxTeamDef andTaskSet(AtsTaskDefToken... taskDefs);

   IAtsConfigTxTeamDef andWorkType(WorkType workType);

   IAtsConfigTxTeamDef andProgram(IAtsProgramArtifactToken program);

   IAtsConfigTxTeamDef andCsci(Csci... cscis);

   IAtsConfigTxTeamDef andAccessContexts(AccessContextToken... contextIds);

}
