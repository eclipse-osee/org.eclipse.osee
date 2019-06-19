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