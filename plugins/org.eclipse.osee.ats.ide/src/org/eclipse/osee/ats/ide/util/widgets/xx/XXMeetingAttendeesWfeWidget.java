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
package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.review.ReviewFormalType;
import org.eclipse.osee.ats.api.review.ReviewUtil;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
// CodeReview.IgnoreXxWid
@Component(service = XWidget.class, immediate = true)
public class XXMeetingAttendeesWfeWidget extends XXAssigneesWidget {

   public static WidgetId ID = WidgetIdAts.XXMeetingAttendeesWfeWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXMeetingAttendeesWfeWidget() {
      super(ID, ReviewUtil.MEETING_ATTENDEES_LABEL);
      setAttributeType(AtsAttributeTypes.MeetingAttendeeId);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   protected TransactionToken handleSelectedPersist() {
      try {
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + getLabel());
         changes.setAttributeValues(ArtifactId.valueOf(getArtifact().getId()), AtsAttributeTypes.MeetingAttendeeId,
            new ArrayList<>(getSelected()));
         return changes.executeIfNeeded();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return TransactionToken.SENTINEL;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      Set<UserToken> users = new HashSet<>();
      try {
         for (Object artIdObj : getArtifact().getAttributeValues(AtsAttributeTypes.MeetingAttendeeId)) {
            try {
               users.add(OseeApiService.userSvc().getUser((ArtifactId) artIdObj));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      setSelected(users, false);
      String formalType = getArtifact().getSoleAttributeValue(AtsAttributeTypes.ReviewFormalType, "");
      if (ReviewFormalType.Formal.name().equals(formalType)) {
         set(XOption.REQUIRED);
      }
   }

}
