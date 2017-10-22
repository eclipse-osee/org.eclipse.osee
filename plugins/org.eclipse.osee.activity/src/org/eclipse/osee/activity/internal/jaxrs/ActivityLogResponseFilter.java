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
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.logger.Log;

/**
 * @author Ryan D. Brooks
 */
@Provider
public class ActivityLogResponseFilter implements ContainerResponseFilter {

   private Log logger;
   private ActivityLog activityLog;

   public void setActivityLogger(ActivityLog activityLog) {
      this.activityLog = activityLog;
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
               activityLog.createActivityThread(JAXRS_METHOD_CALL_FILTER_ERROR, SystemUser.Anonymous,
                  Long.valueOf(OseeClient.getPort()), DEFAULT_CLIENT_ID, ERROR_MSG__MISSING_ACTIVITY_HEADER);
            }
         } catch (Throwable th) {
            logger.error(th, "Error during ActivityContainerResponseFilter");
         }
      }
   }
}
