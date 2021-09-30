/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.activity.ActivityConstants.DEFAULT_CLIENT_ID;
import static org.eclipse.osee.activity.ActivityConstants.ERROR_MSG__MISSING_ACTIVITY_HEADER;
import static org.eclipse.osee.framework.core.data.CoreActivityTypes.JAXRS_METHOD_CALL_FILTER_ERROR;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.activity.ActivityConstants;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan D. Brooks
 */
@Provider
public class ActivityLogResponseFilter implements ContainerResponseFilter {

   private Log logger;
   private ActivityLog activityLog;
   private UserService userService;

   public void bindOrcsApi(OrcsApi orcsApi) {
      activityLog = orcsApi.getActivityLog();
      userService = orcsApi.userService();
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   /**
    * Called after a resource method is executed
    */
   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
      if (activityLog.isEnabled()) {
         try {
            Long entryId = (Long) requestContext.getProperty(ActivityConstants.HTTP_HEADER__ACTIVITY_ENTRY_ID);
            if (entryId != null) {
               StatusType statusType = responseContext.getStatusInfo();
               if (statusType.getFamily() == Status.Family.SUCCESSFUL) {
                  activityLog.completeEntry(entryId);
               } else {
                  activityLog.endEntryAbnormally(entryId, responseContext.getStatus());
               }
            } else {
               // Response was called without a matching request
               activityLog.createActivityThread(JAXRS_METHOD_CALL_FILTER_ERROR, userService.getUser(),
                  Long.valueOf(OseeClient.getPort()), DEFAULT_CLIENT_ID, ERROR_MSG__MISSING_ACTIVITY_HEADER);
            }
         } catch (Throwable th) {
            logger.error(th, "Error during ActivityContainerResponseFilter");
         }
      }
      activityLog.removeActivityThread();
   }
}