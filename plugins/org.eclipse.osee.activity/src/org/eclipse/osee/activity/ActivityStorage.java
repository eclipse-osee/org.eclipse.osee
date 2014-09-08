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

import org.eclipse.osee.activity.api.ActivityLog.ActivityDataHandler;
import org.eclipse.osee.activity.api.ActivityLog.ActivityTypeDataHandler;
import org.eclipse.osee.activity.api.ActivityType;

/**
 * @author Ryan D. Brooks
 */
public interface ActivityStorage {

   void selectEntry(Long entryId, ActivityDataHandler handler);

   int addEntries(Iterable<Object[]> newEntries);

   int updateEntries(Iterable<Object[]> updatedEntries);

   void addActivityTypes(ActivityType... types);

   void addActivityTypes(Iterable<ActivityType> types);

   void selectTypes(ActivityTypeDataHandler handler);

   void selectType(Long typeId, ActivityTypeDataHandler handler);

   boolean typeExists(Long typeId);

}