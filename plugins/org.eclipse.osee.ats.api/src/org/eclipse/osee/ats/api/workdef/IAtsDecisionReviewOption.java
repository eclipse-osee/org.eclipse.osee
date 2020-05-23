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

package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsDecisionReviewOption {

   String getName();

   void setName(String name);

   Collection<String> getUserIds();

   void setUserIds(List<String> userIds);

   boolean isFollowupRequired();

   void setFollowupRequired(boolean followupRequired);

   Collection<String> getUserNames();

   void setUserNames(List<String> userNames);

   @Override
   String toString();

}