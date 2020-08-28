/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.ide.util;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.ide.branch.AtsBranchServiceIde;
import org.eclipse.osee.ats.ide.query.AtsQueryServiceIde;
import org.eclipse.osee.ats.ide.workflow.IAtsWorkItemServiceIde;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.sprint.SprintArtifact;
import org.eclipse.osee.ats.ide.workflow.task.IAtsTaskServiceIde;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Donald G. Dunne
 */
public interface AtsApiIde extends AtsApi, IAtsNotifier {

   IArtifactMembersCache<GoalArtifact> getGoalMembersCache();

   IArtifactMembersCache<SprintArtifact> getSprintItemsCache();

   OseeClient getOseeClient();

   AtsQueryServiceIde getQueryServiceIde();

   IAtsTaskServiceIde getTaskServiceIde();

   IAtsWorkItemServiceIde getWorkItemServiceIde();

   AtsBranchServiceIde getBranchServiceIde();

}