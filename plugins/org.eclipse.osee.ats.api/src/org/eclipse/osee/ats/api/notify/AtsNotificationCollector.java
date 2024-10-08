/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.api.notify;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores notification events generated by the framework or applications. Currently, send happens upon call to
 * sendNotifications(). Eventually, a timer will kick the send event at certain intervals. This mechanism allows for
 * notifications to be collected for a certain period of time and rolled into a single notification. This will
 * eventually also support other types of notifications such as popups and allow the user to configure which events are
 * sent and how.
 *
 * @author Donald G. Dunne
 */
public class AtsNotificationCollector {

   private String subject, body;
   private final List<AtsNotificationEvent> notificationEvents = new ArrayList<>();
   private final List<AtsWorkItemNotificationEvent> workItemNotificationEvents = new ArrayList<>();
   private boolean includeCancelHyperlink = false;

   public void addNotificationEvent(AtsNotificationEvent notificationEvent) {
      notificationEvents.add(notificationEvent);
   }

   public List<AtsNotificationEvent> getNotificationEvents() {
      return notificationEvents;
   }

   public void addWorkItemNotificationEvent(AtsWorkItemNotificationEvent workItemNotificationEvent) {
      workItemNotificationEvents.add(workItemNotificationEvent);
   }

   public List<AtsWorkItemNotificationEvent> getWorkItemNotificationEvents() {
      return workItemNotificationEvents;
   }

   public String getSubject() {
      return subject;
   }

   public void setSubject(String subject) {
      this.subject = subject;
   }

   public String getBody() {
      return body;
   }

   public void setBody(String body) {
      this.body = body;
   }

   public boolean isIncludeCancelHyperlink() {
      return includeCancelHyperlink;
   }

   public void setIncludeCancelHyperlink(boolean includeCancelHyperlink) {
      this.includeCancelHyperlink = includeCancelHyperlink;
   }

   @Override
   public String toString() {
      return "AtsNotificationCollector [subject=" + subject + ", body=" + body + ", notificationEvents=" + notificationEvents + ", workItemNotificationEvents=" + workItemNotificationEvents + ", includeCancelHyperlink=" + includeCancelHyperlink + "]";
   }

   public boolean isValid() {
      return !notificationEvents.isEmpty() || !workItemNotificationEvents.isEmpty();
   }

}
