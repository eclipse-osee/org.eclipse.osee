/*******************************************************************************
 * Copyright (c) 2024 Boeing.
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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.ReviewUtil;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlabelMemberSelectionDam;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkMeetingAttendeesSelectionDam extends XHyperlabelMemberSelectionDam {

   public XHyperlinkMeetingAttendeesSelectionDam() {
      super(ReviewUtil.MEETING_ATTENDEES_LABEL);
   }

   @Override
   public Set<UserToken> getStoredUsers() {
      Set<UserToken> users = new HashSet<>();
      try {
         for (Object artIdObj : artifact.getAttributeValues(AtsAttributeTypes.MeetingAttendeeId)) {
            try {
               users.add(OseeApiService.userSvc().getUser((ArtifactId) artIdObj));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
         // For backwards compatibility; remove after PI 33
         boolean meetingAttendeeAsUserId =
            AtsApiService.get().isConfigValue(AtsUtil.MEETING_ATTENDEE_AS_USER_ID, false);
         if (meetingAttendeeAsUserId) {
            for (Object artUserIdObj : artifact.getAttributeValues(AtsAttributeTypes.MeetingAttendeeUserId)) {
               try {
                  if (Strings.isValid((String) artUserIdObj)) {
                     users.add(OseeApiService.userSvc().getUserByUserId((String) artUserIdObj));
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return users;
   }

   @Override
   public void saveToArtifact() {
      try {
         artifact.setAttributeFromValues(AtsAttributeTypes.MeetingAttendeeId, getSelectedUsers());

         // For backwards compatibility; remove after PI 33
         boolean meetingAttendeeAsUserId =
            AtsApiService.get().isConfigValue(AtsUtil.MEETING_ATTENDEE_AS_USER_ID, false);
         if (meetingAttendeeAsUserId) {
            Set<String> userIds = new HashSet<>();
            for (ArtifactId id : getSelectedUsers()) {
               AtsUser user = AtsApiService.get().getUserService().getUserById(id);
               userIds.add(user.getUserId());
            }
            artifact.setAttributeFromValues(AtsAttributeTypes.MeetingAttendeeUserId, userIds);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

}
