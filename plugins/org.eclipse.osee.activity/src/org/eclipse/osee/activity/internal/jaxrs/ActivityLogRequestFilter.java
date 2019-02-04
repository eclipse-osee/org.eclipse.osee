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

import static org.eclipse.osee.framework.core.data.CoreActivityTypes.JAXRS_METHOD_CALL;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;
import org.eclipse.osee.activity.ActivityConstants;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.data.OseeClient;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Ryan D. Brooks
 */
@PreMatching
@Provider
public class ActivityLogRequestFilter implements ContainerRequestFilter {

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
      if (activityLog.isEnabled()) {
         try {
            String message = String.format("%s %s", context.getMethod(), context.getUriInfo().getPath());
            String clientStr = context.getHeaderString("osee.client.id");
            Long clientId = Strings.isValid(clientStr) ? Long.valueOf(clientStr) : Id.SENTINEL;
            Long serverId = Long.valueOf(OseeClient.getPort());
            UserId accountId = UserId.valueOf(context.getHeaderString("osee.account.id"));

            Long entryId = activityLog.createActivityThread(JAXRS_METHOD_CALL, accountId, serverId, clientId, message);

            context.setProperty(ActivityConstants.HTTP_HEADER__ACTIVITY_ENTRY_ID, entryId);
         } catch (Throwable th) {
            logger.error(th, "Error during ActivityContainerRequestFilter");
         }
      }
   }
}