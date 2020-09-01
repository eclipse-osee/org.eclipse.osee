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

package org.eclipse.osee.ats.ide.workflow.task;

import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTaskServiceIde extends IAtsTaskService {

   TaskArtifact createNewTaskWithDialog(IAtsTeamWorkflow teamWf);

}