/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.xchild;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.util.OseeEmail.BodyType;
import org.eclipse.osee.framework.core.util.SendEmailRequest;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

/**
 * See AbstractXHyperlinkWfdSelectedChildDam<br/>
 * <br/>
 * In addition, this widget will notify the selected UserGroup members of the new selection. <br/>
 * <br/>
 *
 * @author Donald G. Dunne
 */
public abstract class AbstractXHyperlinkWfdSelectedUserGroupWithNotifyDam extends AbstractXHyperlinkWfdSelectedChildDam {

   public AbstractXHyperlinkWfdSelectedUserGroupWithNotifyDam(String label, ArtifactToken parentArt) {
      super(label, parentArt);
   }

   @Override
   protected void handleTransactionCompleted(ArtifactToken selected) {
      notifyUserGroups(selected);
   }

   public void notifyUserGroups(final ArtifactToken selected) {
      Thread notifyUsers = new Thread(new Runnable() {

         @Override
         public void run() {
            try {
               Collection<UserToken> members = null;
               if (selected != null && selected.isOfType(CoreArtifactTypes.UserGroup)) {
                  UserService userService = ServiceUtil.getOseeClient().userService();
                  members = userService.getUserGroup(selected).getMembers();
               }
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

               SendEmailRequest request = new SendEmailRequest();
               request.setToAddresses(addresses);
               request.setFromAddress(getEmailFrom(selected));
               request.setReplyToAddress(getEmailFrom(selected));
               request.setSubject(getEmailSubject(selected));
               request.setBody(getEmailBody(selected));
               request.setBodyType(BodyType.Html);
               request.setEmailAddressesAbridged(addressesAbridged);
               request.setSubjectAbridged(getEmailSubjectAbridged(selected));
               request.setBodyAbridged(getEmailBodyAbridged(selected));

               OseeApiService.serverEnpoints().getOrcsUserEndpoint().sendEmail(request);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

      }, "Notify " + label + "Users");
      notifyUsers.start();
   }

   protected String getEmailFrom(ArtifactToken selected) {
      return OseeApiService.user().getEmail();
   }

   protected abstract String getEmailBody(ArtifactToken selected);

   protected abstract String getEmailBodyAbridged(ArtifactToken selected);

   protected abstract String getEmailSubject(ArtifactToken selected);

   protected abstract String getEmailSubjectAbridged(ArtifactToken selected);

}
