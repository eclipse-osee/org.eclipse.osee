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

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Donald G Dunne
 */
public interface IAtsStateFactory {

   IAtsStateManager getStateManager(IAtsWorkItem workItem);

   void writeToStore(AtsUser atsUser, IAtsWorkItem workItem, IAtsChangeSet changes);

   void load(IAtsWorkItem workItem, IAtsStateManager stateMgr);

   void clearStateManager(Id id);

   void setStateMgr(IAtsWorkItem workItem, IAtsStateManager stateMgr);

   void setEnableTransactionCheck(boolean enable);

}
