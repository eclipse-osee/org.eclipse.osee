/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.notify;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotifyEndpointApi;
import org.eclipse.osee.ats.core.notify.AbstractAtsNotificationService;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationServiceImpl extends AbstractAtsNotificationService {

   public AtsNotificationServiceImpl(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public synchronized void sendNotifications(final AtsNotificationCollector notifications) {
      if (AtsUtilClient.isEmailEnabled()) {
         Jobs.startJob(new Job("Send Notifications") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               try {
                  System.err.println(String.format("client: [%s] - [%s]",
                     AtsNotificationServiceImpl.class.getSimpleName(), notifications));

                  AtsNotifyEndpointApi notifyEndpoint = AtsApiService.get().getServerEndpoints().getNotifyEndpoint();
                  notifyEndpoint.sendNotifications(notifications);
               } catch (Exception ex) {
                  OseeLog.log(AtsNotificationServiceImpl.class, Level.SEVERE, ex);
               }
               return Status.OK_STATUS;
            }
         }, false);
      }
   }

   @Override
   public synchronized void sendNotifications(String fromUserEmail, Collection<String> toUserEmails, String subject, String body) {
      throw new UnsupportedOperationException("Not supported on client");
   }

}
