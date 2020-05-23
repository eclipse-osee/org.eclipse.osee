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
import org.eclipse.osee.ats.api.workflow.WorkState;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkStateFactory {

   String toStoreStr(IAtsStateManager stateMgr, String stateName);

   WorkState fromStoreStr(String storeStr);

   String getStorageString(Collection<AtsUser> users);

   List<AtsUser> getUsers(String sorageString);

}
