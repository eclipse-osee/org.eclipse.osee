/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.ats.ide.notify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.notify.AtsNotificationEvent;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IUserGroup;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkTriStateBooleanDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XRadioButtonsBooleanTriState.BooleanState;

/**
 * 3-state widget with User Group notification upon set
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractXHyperlinkTriStateNotifyDam extends XHyperlinkTriStateBooleanDam {

   private final IUserGroupArtifactToken notifyUserGroup;

   public AbstractXHyperlinkTriStateNotifyDam(AttributeTypeToken attrType, IUserGroupArtifactToken notifyUserGroup) {
      this.attributeType = attrType;
      this.notifyUserGroup = notifyUserGroup;
   }

   @Override
   protected void handleSelectionPersist(BooleanState selected) {
      super.handleSelectionPersist(selected);
      if (selected.isYes()) {
         sendNotifications(
            org.eclipse.osee.framework.jdk.core.util.Collections.castAll(Arrays.asList((IAtsWorkItem) artifact)));
      }
   }

   public void sendNotifications(Collection<IAtsWorkItem> workItems) {
      sendNotifications(attributeType, notifyUserGroup, workItems, AtsApiService.get());
   }

   public static void sendNotifications(AttributeTypeToken attrType, IUserGroupArtifactToken notifyUserGroup,
      Collection<IAtsWorkItem> workItems, AtsApi atsApi) {
      AtsNotificationCollector notifications = new AtsNotificationCollector();
      notifications.setSubject(String.format("OSEE modified [%s]", attrType.getUnqualifiedName()));
      IUserGroup userGroup = atsApi.userService().getUserGroupOrSentinel(notifyUserGroup);
      if (userGroup.getArtifact().isInvalid()) {
         return;
      }
      for (IAtsWorkItem workItem : workItems) {

         boolean value = atsApi.getAttributeResolver().getSoleAttributeValue(workItem, attrType, false);
         if (!value) {
            continue;
         }
         Collection<UserToken> members = userGroup.getMembers();
         if (members != null && members.size() > 0) {
            ArrayList<String> toUserEmailList = new ArrayList<String>();
            for (UserToken userTok : members) {
               if (userTok.isActive()) {
                  String userEmail = userTok.getEmail();
                  if (EmailUtil.isEmailValid(userEmail)) {
                     toUserEmailList.add(userEmail);
                  }
               }
            }

            if (toUserEmailList.isEmpty()) {
               continue;
            }

            AtsNotificationEvent notify = new AtsNotificationEvent();
            notify.setType("OSEE modified " + attrType.getUnqualifiedName());
            notify.setDescription(String.format("OSEE modified [%s] to value [%s]<br/><br/>" //
               + "This email sent to [%s] User Group.", //
               workItem.toStringWithAtsId(), //
               value, notifyUserGroup.getName()));
            notify.setId(workItem.getAtsId());
            notify.setUrl(atsApi.getWorkItemService().getHtmlUrl(workItem, atsApi));
            notify.setEmailAddresses(toUserEmailList);
            notify.setFromUserId(AtsCoreUsers.SYSTEM_USER.getUserId());
            notifications.addNotificationEvent(notify);
         }
      }
      if (notifications.isValid()) {
         atsApi.getNotificationService().sendNotifications(notifications);
      }
   }

}
