/*******************************************************************************
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

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.eclipse.osee.activity.api.ActivityEntry;
import org.eclipse.osee.activity.api.ActivityEntryId;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.activity.api.ActivityLog.ActivityDataHandler;
import org.eclipse.osee.activity.api.ActivityLog.ActivityTypeDataHandler;
import org.eclipse.osee.activity.api.ActivityLogEndpoint;
import org.eclipse.osee.activity.api.DefaultActivityType;
import org.eclipse.osee.framework.jdk.core.type.MutableBoolean;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public final class ActivityLogResource implements ActivityLogEndpoint {

   private final ActivityLog activityLog;

   public ActivityLogResource(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   @Override
   public ActivityEntry getEntry(Long entryId) {
      Conditions.checkNotNull(entryId, "activity entry id");
      final ActivityEntry entry = new ActivityEntry(entryId);
      final MutableBoolean found = new MutableBoolean(false);
      activityLog.queryEntry(entryId, new ActivityDataHandler() {

         @Override
         public void onData(Long entryId, Long parentId, Long typeId, Long accountId, Long serverId, Long clientId, Long startTime, Long duration, Integer status, String messageArgs) {
            found.setValue(true);
            entry.setAccountId(accountId);
            entry.setClientId(clientId);
            entry.setDuration(duration);
            entry.setMessageArgs(messageArgs);
            entry.setParentId(parentId);
            entry.setServerId(serverId);
            entry.setStartTime(startTime);
            entry.setStatus(status);
            entry.setTypeId(typeId);
         }
      });
      if (!found.getValue()) {
         throw new NotFoundException("Activity Entry for entry id [" + entryId + "] was not found");
      }
      return entry;
   }

   @Override
   public ActivityEntryId createEntry(Long typeId, Long parentId, Integer status, String message) {
      Long entryId = activityLog.createEntry(typeId, parentId, status, message);
      ActivityEntryId entity = new ActivityEntryId(entryId);
      return entity;
   }

   @Override
   public Response updateEntry(Long entryId, Integer statusId) {
      activityLog.updateEntry(entryId, statusId);
      return Response.ok().build();
   }

   @Override
   public DefaultActivityType[] getActivityTypes() {
      final List<DefaultActivityType> types = new ArrayList<>();
      activityLog.queryActivityTypes(new ActivityTypeDataHandler() {

         @Override
         public void onData(Long typeId, Long logLevel, String module, String messageFormat) {
            DefaultActivityType type = new DefaultActivityType();
            type.setTypeId(typeId);
            type.setLogLevel(logLevel);
            type.setModule(module);
            type.setMessageFormat(messageFormat);
            types.add(type);
         }
      });
      return types.toArray(new DefaultActivityType[0]);
   }

   @Override
   public DefaultActivityType getActivityType(Long typeId) {
      Conditions.checkNotNull(typeId, "activity type id");
      final MutableBoolean found = new MutableBoolean(false);
      final DefaultActivityType type = new DefaultActivityType();
      activityLog.queryActivityType(typeId, new ActivityTypeDataHandler() {

         @Override
         public void onData(Long typeId, Long logLevel, String module, String messageFormat) {
            found.setValue(true);
            type.setTypeId(typeId);
            type.setLogLevel(logLevel);
            type.setModule(module);
            type.setMessageFormat(messageFormat);
         }
      });
      if (!found.getValue()) {
         throw new NotFoundException("Activity Type for type id [" + typeId + "] was not found");
      }
      return type;
   }

   @Override
   public DefaultActivityType createActivityType(Long typeId, Long logLevel, String module, String messageFormat) {
      if (!activityLog.activityTypeExists(typeId)) {
         return newActivityHelper(typeId, logLevel, module, messageFormat);
      } else {
         return getActivityType(typeId);
      }
   }

   @Override
   public DefaultActivityType createActivityType(Long logLevel, String module, String messageFormat) {
      Long typeId = Lib.generateUuid();
      return newActivityHelper(typeId, logLevel, module, messageFormat);
   }

   private DefaultActivityType newActivityHelper(Long typeId, Long logLevel, String module, String messageFormat) {
      DefaultActivityType type = new DefaultActivityType();
      type.setTypeId(typeId);
      type.setLogLevel(logLevel);
      type.setModule(module);
      type.setMessageFormat(messageFormat);
      activityLog.createActivityTypes(type);
      return type;
   }

}
