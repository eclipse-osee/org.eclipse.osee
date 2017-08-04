/*******************************************************************************
 *
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity.internal.jaxrs;

import javax.ws.rs.core.Response;
import org.eclipse.osee.activity.api.ActivityEntry;
import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ActivityLogEndpoint;
import org.eclipse.osee.framework.core.data.ActivityTypeId;
import org.eclipse.osee.framework.core.data.ActivityTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Ryan D. Brooks
 */
public final class ActivityLogResource implements ActivityLogEndpoint {

   private final ActivityLog activityLog;

   public ActivityLogResource(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   @Override
   public ActivityEntry getEntry(ActivityEntryId entryId) {
      Conditions.checkNotNull(entryId, "activity entry id");
      return activityLog.getEntry(entryId);
   }

   @Override
   public ActivityEntryId createEntry(ActivityTypeId type, Long parentId, Integer status, String message) {
      Long entryId = activityLog.createEntry(activityLog.getActivityType(type), parentId, status, message);
      return new ActivityEntryId(entryId);
   }

   @Override
   public Response updateEntry(Long entryId, Integer statusId) {
      activityLog.updateEntry(entryId, statusId);
      return Response.ok().build();
   }

   @Override
   public ActivityTypeToken createIfAbsent(ActivityTypeToken activityType) {
      return activityLog.createIfAbsent(activityType);
   }
}