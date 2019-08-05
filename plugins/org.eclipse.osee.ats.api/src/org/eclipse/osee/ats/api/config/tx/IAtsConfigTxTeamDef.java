/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config.tx;

import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.query.NextRelease;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
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

   IAtsConfigTxTeamDef andAccessContextId(String contextId);

   IAtsConfigTxTeamDef andVersion(String name, ReleasedOption released, IOseeBranch branch, NextRelease nextRelease, IOseeBranch... parallelVersions);

   IAtsConfigTxTeamDef createChildTeamDef(IAtsTeamDefinition parent, IAtsTeamDefinitionArtifactToken teamDef);

   IAtsConfigTxTeamDef createChildTeamDef(String name);

   IAtsTeamDefinition getTeamDef();

   IAtsConfigTxTeamDef andVersion(IAtsVersionArtifactToken version, ReleasedOption released, IOseeBranch branch, NextRelease nextRelease, IOseeBranch... parallelVersions);

   IAtsConfigTxTeamDef andVersion(IAtsVersionArtifactToken... verToks);

   /**
    * @param peerWorkDefs One or more peer work definitions. If 0, default will be used. If 1, it will be used. If > 1,
    * user will be given a choice when creating reviews.
    */
   IAtsConfigTxTeamDef andRelatedPeerWorkflowDefinition(NamedId... peerWorkDefs);

   IAtsConfigTxTeamDef andTaskSet(AtsTaskDefToken... taskDefs);
}
