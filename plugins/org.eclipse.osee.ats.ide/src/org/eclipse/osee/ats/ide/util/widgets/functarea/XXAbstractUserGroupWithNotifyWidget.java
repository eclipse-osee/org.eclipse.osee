/*********************************************************************
 * Copyright (c) 2026 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.functarea;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.core.util.SendEmailRequest;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXChildSelWidget;

/**
 * @author Donald G. Dunne
 */
public abstract class XXAbstractUserGroupWithNotifyWidget extends XXChildSelWidget {

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAbstractUserGroupWithNotifyWidget(WidgetId widgetId, String label, ArtifactToken folder, AttributeTypeToken attrType) {
      super(widgetId, label, folder);
      setAttributeType(attrType);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   protected boolean isSelectable(Artifact art) {
      return art.isOfType(CoreArtifactTypes.UserGroup);
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   @Override
   protected void handleTransactionCompleted() {
      // Values are names (String-based widget). notifyUserGroup(String) resolves each back to its user group
      // artifact before notifying.
      for (String sel : getSelected()) {
         notifyUserGroup(sel);
      }
   }

   /**
    * Resolve a selected user group name to its artifact (from the parent's selectable children) and notify its
    * members. Subclasses/callers holding only the stored String value can use this without doing their own artifact
    * lookup. No-op if the name does not resolve to a current selectable user group.
    */
   public void notifyUserGroup(String selectedName) {
      Artifact userGroup = getSelectableArtifact(selectedName);
      if (userGroup != null) {
         notifyUserGroup(userGroup);
      }
   }

   public void notifyUserGroup(ArtifactToken selected) {
      new Thread(() -> {
         try {
            if (selected == null || !selected.isOfType(CoreArtifactTypes.UserGroup)) {
               return;
            }
            UserService userService = ServiceUtil.getOseeClient().userService();
            Collection<UserToken> members = userService.getUserGroup(selected).getMembers();
            if (members == null || members.isEmpty()) {
               return;
            }

            Set<String> addresses = new HashSet<>();
            Set<String> addressesAbridged = new HashSet<>();
            for (UserToken user : members) {
               if (user.isActive() && EmailUtil.isEmailValid(user.getEmail())) {
                  addresses.add(user.getEmail());
               }
               String abridgedEmail = OseeApiService.userSvc().getAbridgedEmail(user);
               if (EmailUtil.isEmailValid(abridgedEmail)) {
                  addressesAbridged.add(abridgedEmail);
               }
            }
            if (addresses.isEmpty() && addressesAbridged.isEmpty()) {
               return;
            }

            if (!xxWid.isTeamWf()) {
               return;
            }
            IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
            if (teamWf == null) {
               return;
            }
            SendEmailRequest request = new SendEmailRequest();
            request.setToAddresses(addresses);
            request.setFromAddress(OseeApiService.user().getEmail());
            request.setReplyToAddress(OseeApiService.user().getEmail());
            request.setSubject(FunctionalAreaUtil.getEmailSubject(teamWf, selected));
            request.setBody(FunctionalAreaUtil.getEmailBody(teamWf, selected));
            request.setBodyType(BodyType.Html);
            request.setEmailAddressesAbridged(addressesAbridged);
            request.setBodyAbridged(FunctionalAreaUtil.getEmailBodyAbridged(teamWf, selected));

            OseeApiService.serverEnpoints().getOrcsUserEndpoint().sendEmail(request);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }, "Notify " + getLabel() + " Users").start();
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
