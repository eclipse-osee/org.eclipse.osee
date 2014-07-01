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

import static org.eclipse.osee.activity.ActivityConstants.ERROR_MSG__MISSING_ACTIVITY_HEADER;
import static org.eclipse.osee.activity.ActivityConstants.HTTP_HEADER__ACTIVITY_ENTRY_ID;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.activity.ActivityConstants;
import org.eclipse.osee.activity.api.Activity;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Ryan D. Brooks
 */
@Provider
public class ActivityLogFilter implements ContainerRequestFilter, ContainerResponseFilter {

   private Log logger;
   private ActivityLog activityLog;

   public void setActivityLogger(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   /**
    * Called before a resource method is executed
    */
   @Override
   public void filter(ContainerRequestContext context) {
      try {
         String message = String.format("%s %s", context.getMethod(), context.getUriInfo().getRequestUri());

         Long serverId = getServerId(context);
         Long clientId = getClientId(context);
         Long accountId = getAccountId(context);

         Long entryId =
            activityLog.createActivityThread(Activity.JAXRS_METHOD_CALL, accountId, serverId, clientId, message);

         context.getHeaders().addFirst(ActivityConstants.HTTP_HEADER__ACTIVITY_ENTRY_ID, String.valueOf(entryId));
      } catch (Throwable th) {
         logger.error(th, "Error during ActivityContainerRequestFilter");
      }
   }

   /**
    * Called after a resource method is executed
    */
   @Override
   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
      try {
         String activityHeader = requestContext.getHeaders().getFirst(HTTP_HEADER__ACTIVITY_ENTRY_ID);
         if (!Strings.isValid(activityHeader)) {
            activityLog.createUpdateableEntry(Activity.JAXRS_METHOD_CALL_FILTER_ERROR,
               ERROR_MSG__MISSING_ACTIVITY_HEADER);
         } else {
            Long entryId = Long.parseLong(activityHeader);

            StatusType statusType = responseContext.getStatusInfo();
            if (statusType.getFamily() == Status.Family.SUCCESSFUL) {
               activityLog.completeEntry(entryId);
            } else {
               activityLog.endEntryAbnormally(entryId, responseContext.getStatus());
            }
         }
      } catch (Throwable th) {
         logger.error(th, "Error during ActivityContainerResponseFilter");
      }
   }

   private Long getServerId(ContainerRequestContext context) {
      Long toReturn = ActivityConstants.DEFAULT_SERVER_ID;
      return toReturn;
   }

   private Long getClientId(ContainerRequestContext context) {
      Long toReturn = ActivityConstants.DEFAULT_CLIENT_ID;
      return toReturn;
   }

   private Long getAccountId(ContainerRequestContext context) {
      Long toReturn = ActivityConstants.DEFAULT_ACCOUNT_ID;
      return toReturn;
   }

}
