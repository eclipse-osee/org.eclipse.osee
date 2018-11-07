/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity;

import org.eclipse.osee.activity.api.ActivityEntry;
import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.framework.core.data.ActivityTypeId;
import org.eclipse.osee.framework.core.data.ActivityTypeToken;

/**
 * @author Ryan D. Brooks
 */
public interface ActivityStorage {

   ActivityEntry getEntry(ActivityEntryId entryId);

   int addEntries(Iterable<Object[]> newEntries);

   int updateEntries(Iterable<Object[]> updatedEntries);

   ActivityTypeToken getActivityType(ActivityTypeId typeId);

   void cleanEntries(int daysToKeep);

   /**
    * Stores the activity type if not already in the data store. If the type token has an invalid id, a new id is
    * generated and included in the returned token
    */
   ActivityTypeToken createIfAbsent(ActivityTypeToken type);

   void createIfAbsent(Iterable<ActivityTypeToken> types);

}