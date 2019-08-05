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

import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.query.NextRelease;
import org.eclipse.osee.ats.api.query.ReleasedOption;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigTx {

   IAtsConfigTxTeamDef createTeamDef(IAtsTeamDefinition parent, IAtsTeamDefinitionArtifactToken teamDef);

   IAtsConfigTxActionableItem createActionableItem(IAtsActionableItemArtifactToken actionableItem);

   TransactionId execute();

   IAtsConfigTxVersion createVersion(String name, ReleasedOption released, IOseeBranch branch, NextRelease nextRelease, IAtsTeamDefinition teamDef);

   IAtsConfigTxVersion createVersion(IAtsVersionArtifactToken versionTok, ReleasedOption released, IOseeBranch branch, NextRelease nextRelease, IAtsTeamDefinition teamDef);

   IAtsTeamDefinition getTeamDef(ArtifactId teamDef);

   IAtsTeamDefinition getTeamDef(String name);

   IAtsConfigTxProgram createProgram(IAtsProgramArtifactToken program);

   IAtsConfigTxActionableItem createActionableItem(IAtsActionableItem parent, IAtsActionableItemArtifactToken actionableItem);

   IAtsActionableItem getActionableItem(ArtifactId artifact);

   IAtsChangeSet getChanges();

   CreateTasksDefinitionBuilder createTaskDefinitionBuilder(Long id, String name);

}
