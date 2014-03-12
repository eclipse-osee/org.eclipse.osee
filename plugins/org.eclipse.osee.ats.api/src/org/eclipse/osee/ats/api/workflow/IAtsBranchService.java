/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public interface IAtsBranchService {

   boolean isBranchInCommit(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   boolean isWorkingBranchInWork(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IOseeBranch getBranch(IAtsTeamWorkflow teamWf) throws OseeCoreException;

   IOseeBranch getBranch(IAtsConfigObject configObject);

   IOseeBranch getBranch(ICommitConfigItem configObject);

   String getBranchShortName(ICommitConfigItem commitConfigArt);

   boolean isBranchValid(ICommitConfigItem configArt);

   IOseeBranch getBranchInherited(IAtsVersion version);
}
