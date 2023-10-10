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

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.util.OseeEmail;
import org.eclipse.osee.framework.jdk.core.util.EmailUtil;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.notify.OseeEmailIde;

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
               if (selected.isOfType(CoreArtifactTypes.UserGroup)) {
                  UserService userService = ServiceUtil.getOseeClient().userService();
                  members = userService.getUserGroup(selected).getMembers();
               }
               if (members == null || members.isEmpty()) {
                  return;
               }
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
                  return;
               }

               OseeEmail mail = OseeEmailIde.create();
               mail.setSubject(getEmailSubject(selected));
               mail.setHTMLBody(getEmailBody(selected));
               mail.setFrom(getEmailFrom(selected));

               String addresses = "";
               for (UserToken user : members) {
                  if (EmailUtil.isEmailValid(user.getEmail())) {
                     addresses = addresses + "," + user.getEmail();
                  }
               }
               if (Strings.isInvalid(addresses)) {
                  return;
               }
               mail.addRecipients(addresses);
               mail.send();

            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }

      }, "Notify " + label + "Users");
      notifyUsers.start();
   }

   protected String getEmailFrom(ArtifactToken selected) {
      return UserManager.getUser().getEmail();
   }

   protected abstract String getEmailBody(ArtifactToken selected);

   protected abstract String getEmailSubject(ArtifactToken selected);

}
