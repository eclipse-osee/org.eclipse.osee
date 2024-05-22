/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * See getDescription() below
 *
 * @author Donald G Dunne
 */
public class ConvertMeetingAttendeesToIdAttr implements IAtsDatabaseConversion {

   private final String TITLE = "Convert UserId Meeting Attendees to ArtifactId";

   @Override
   public void run(XResultData rd, boolean reportOnly, AtsApi atsApi) {

      Collection<IAtsWorkItem> reviews =
         atsApi.getQueryService().getWorkItemsAtrTypeExists(AtsAttributeTypes.MeetingAttendeeUserId);

      IAtsChangeSet changes = null;
      if (!reportOnly) {
         changes = atsApi.createChangeSet("MeetingAttendeeUserId - Convert", AtsCoreUsers.SYSTEM_USER);
      }
      for (IAtsWorkItem review : reviews) {
         List<Object> meetingAttendees = new ArrayList<>();
         for (String userId : atsApi.getAttributeResolver().getAttributesToStringList(review.getStoreObject(),
            AtsAttributeTypes.MeetingAttendeeUserId)) {
            AtsUser user = atsApi.getUserService().getUserByUserId(userId);
            if (user != null) {
               if (!meetingAttendees.contains(user.getArtifactId())) {
                  meetingAttendees.add(user.getArtifactId());
               }
            }
         }
         if (changes != null) {
            changes.setAttributeValues(review, AtsAttributeTypes.MeetingAttendeeId, meetingAttendees);
         }
         rd.logf("Converting Meeting Attendees for %s\n", review.toStringWithId());
         break;
      }
      if (!reportOnly && changes != null && !changes.isEmpty()) {
         TransactionToken tx = changes.executeIfNeeded();
         System.err.println("Transaction: " + tx.getIdString());
         rd.logf("Transaction %s\n", tx.getIdString());
      }
   }

   @Override
   public String getName() {
      return TITLE;
   }

   @Override
   public String getDescription() {
      return "Converts all Peer Review artifact meeting assignee attrs from UserId to user ArtifactId";
   }
}