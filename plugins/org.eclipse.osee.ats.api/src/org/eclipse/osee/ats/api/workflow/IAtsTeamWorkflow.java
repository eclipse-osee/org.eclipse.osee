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

package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItemProvider;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;

/**
 * @author Donald G. Dunne
 */
public interface IAtsTeamWorkflow extends IAtsWorkItem, IAtsActionableItemProvider {
   IAtsTeamDefinition getTeamDefinition();
}