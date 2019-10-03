/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.util.email;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeArtifactDialog;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class EmailUserGroups extends XNavigateItemAction {

   /**
    * @param teamDefHoldingVersions Team Definition Artifact that is related to versions or null for popup selection
    */
   public EmailUserGroups(XNavigateItem parent) {
      super(parent, "Email User Groups", FrameworkImage.EMAIL);
   }

   public static Set<Artifact> getEmailGroupsAndUserGroups(User user) {
      Set<Artifact> artifacts = new HashSet<>();
      for (Artifact art : ArtifactQuery.getArtifactListFromTypeWithInheritence(CoreArtifactTypes.UserGroup, COMMON,
         DeletionFlag.EXCLUDE_DELETED)) {
         // Only add group if have read permissions
         if (!art.getName().equals("Root Artifact") && AccessControlManager.hasPermission(art, PermissionEnum.READ)) {
            artifacts.add(art);
         }
      }
      return artifacts;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         Set<Artifact> groupOptions = getEmailGroupsAndUserGroups(UserManager.getUser());
         FilteredCheckboxTreeArtifactDialog dialog =
            new FilteredCheckboxTreeArtifactDialog("Select Groups to Email", groupOptions);
         if (dialog.open() == 0) {

            Set<String> emails = new HashSet<>();
            for (Artifact artifact : dialog.getChecked()) {
               if (artifact.isOfType(CoreArtifactTypes.UniversalGroup)) {
                  for (Artifact userArt : artifact.getRelatedArtifacts(CoreRelationTypes.UniversalGrouping_Members)) {
                     if (userArt instanceof User) {
                        if (!EmailUtil.isEmailValid((User) userArt)) {
                           OseeLog.logf(Activator.class, Level.SEVERE, "Invalid email [%s] for user [%s]; skipping",
                              ((User) userArt).getEmail(), userArt);
                        } else if (((User) userArt).isActive()) {
                           emails.add(((User) userArt).getEmail());
                        }
                     }
                  }
               } else if (artifact.isOfType(CoreArtifactTypes.UserGroup)) {
                  for (User user : artifact.getRelatedArtifacts(CoreRelationTypes.Users_User, User.class)) {
                     if (!EmailUtil.isEmailValid(user)) {
                        OseeLog.logf(Activator.class, Level.SEVERE, "Invalid email [%s] for user [%s]; skipping",
                           user.getEmail(), user);
                     } else if (user.isActive()) {
                        emails.add(user.getEmail());
                     }
                  }
               }
            }
            if (emails.isEmpty()) {
               AWorkbench.popup("Error", "No emails configured.");
               return;
            }
            String emailStr = org.eclipse.osee.framework.jdk.core.util.Collections.toString(";", emails);
            if (emailStr.length() > 2048) {
               AWorkbench.popup("Email list too big for auto-open. Emails opened in editor for copy/paste.");
               ResultsEditor.open("Email Addresses", "Email Addresses", emailStr);
            } else {
               Program.launch("mailto:" + emailStr);
            }
            AWorkbench.popup("Complete", "Configured emails openened in local email client.");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }
}
