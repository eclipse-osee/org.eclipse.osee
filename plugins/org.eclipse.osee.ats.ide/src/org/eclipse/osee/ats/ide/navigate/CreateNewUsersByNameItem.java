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

package org.eclipse.osee.ats.ide.navigate;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * Admin only. Create new users by name, each will be given a id as user id. Development use only.
 *
 * @author Donald G. Dunne
 */
public class CreateNewUsersByNameItem extends XNavigateItemAction {

   public CreateNewUsersByNameItem() {
      super("Create New Users by Name (Testing Only)", FrameworkImage.USER, XNavigateItem.USER_MANAGEMENT_ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      EntryDialog ed = new EntryDialog(Displays.getActiveShell(), "Create New User(s)", null,
         "Enter User name(s) one per line", MessageDialog.QUESTION, new String[] {"OK", "Cancel"}, 0);
      ed.setFillVertically(true);
      if (ed.open() == 0) {
         Set<String> newUserNames = new HashSet<>();
         for (String str : ed.getEntry().split(System.getProperty("line.separator"))) {
            newUserNames.add(str);
         }
         XResultData resultData = new XResultData(false);
         for (String newUserName : newUserNames) {
            if (!Strings.isValid(newUserName)) {
               resultData.error("user name can't be blank");
            }
            try {
               if (AtsApiService.get().getUserService().getUserByName(newUserName) != null) {
                  resultData.error(String.format("User [%s] already exists", newUserName));
               }
            } catch (UserNotInDatabase ex) {
               // do nothing
            }
         }
         if (!resultData.isEmpty()) {
            resultData.log("\nErrors found while creating users.\nPlease resolve and try again.");
            XResultDataUI.report(resultData, "Create New User(s) Error");
            return;
         }
         try {
            SkynetTransaction transaction =
               TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), "Create New User(s)");
            Set<Artifact> newUsers = createNewUserItemTx(transaction, newUserNames);
            transaction.execute();

            if (newUsers.size() == 1) {
               RendererManager.open(newUsers.iterator().next(), PresentationType.DEFAULT_OPEN);
            } else {
               MassArtifactEditor.editArtifacts("New Users", newUsers, TableLoadOption.None);
            }

         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private Set<Artifact> createNewUserItemTx(SkynetTransaction transaction, Set<String> userNames) {
      Set<Artifact> newVersions = new HashSet<>();
      for (String userName : userNames) {
         Artifact userArt =
            ArtifactTypeManager.addArtifact(CoreArtifactTypes.User, AtsApiService.get().getAtsBranch(), userName);
         userArt.setSoleAttributeValue(CoreAttributeTypes.UserId, Lib.generateArtifactIdAsInt().toString());
         userArt.persist(transaction);
         newVersions.add(userArt);
      }
      return newVersions;
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(CoreUserGroups.OseeAdmin);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.USER_ADD);
   }

}
