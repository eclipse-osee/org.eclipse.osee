/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.Collection;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;

/**
 * @author Donald G. Dunne
 */
public interface AtsSubcribeService {

   void addSubscribed(IAtsWorkItem workItem, AtsUser user, IAtsChangeSet changes);

   void removeSubscribed(IAtsWorkItem workItem, AtsUser user, IAtsChangeSet changes);

   boolean isSubscribed(IAtsWorkItem workItem, AtsUser user);

   boolean amISubscribed(IAtsWorkItem workItem);

   void toggleSubscribe(IAtsWorkItem awa);

   void toggleSubscribe(Collection<IAtsWorkItem> workItems);

}