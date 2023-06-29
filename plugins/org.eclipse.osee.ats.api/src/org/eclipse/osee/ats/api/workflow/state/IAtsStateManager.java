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

package org.eclipse.osee.ats.api.workflow.state;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;

/**
 * @author Donald G. Dunne
 */
public interface IAtsStateManager {

   void transitionHelper(IStateToken fromStateName, IStateToken toStateName);

   Collection<AtsUser> getAssignees(String stateName);

   List<String> getVisitedStateNames();

   Collection<AtsUser> getAssignees(IStateToken state);

   boolean isStateVisited(IStateToken state);

   boolean isStateVisited(String stateName);

   void writeToStore(IAtsChangeSet changes);

   void createOrUpdateState(String stateName, Collection<AtsUser> assignees);

   void clearCaches();

   void addAssignee(AtsUser user);

   void createOrUpdateState(IStateToken state);

   void setCurrentState(String stateName);

}