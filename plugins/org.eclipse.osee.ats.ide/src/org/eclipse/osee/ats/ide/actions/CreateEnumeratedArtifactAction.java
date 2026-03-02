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

package org.eclipse.osee.ats.ide.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CreateEnumeratedArtifactDialog;

/**
 * @author Vaibhav Patel
 */
public class CreateEnumeratedArtifactAction extends XNavigateItemAction {

   private static final String TITLE = "Create Enumerated Artifact";

   public CreateEnumeratedArtifactAction() {
      super(TITLE, FrameworkImage.GEAR, XNavigateItem.DEFINE_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

   private ArtifactToken getOrCreateEnumeratedArtifactFolder() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Get or Create Enumerated Artifact Folder");
      ArtifactToken enumArtFolder = AtsApiService.get().getQueryService().getOrCreateArtifact(
         CoreArtifactTokens.OseeConfiguration, CoreArtifactTokens.EnumeratedArtifactsFolder, changes);
      changes.executeIfNeeded();
      return enumArtFolder;
   }

   private ArtifactToken createEnumeratedArtifact(ArtifactToken parentFolder, String name, List<String> values) {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(TITLE);
      ArtifactToken enumArt = changes.createArtifact(CoreArtifactTypes.OseeTypeEnum, name);
      for (String value : values) {
         if (Strings.isValid(value)) {
            changes.addAttribute(enumArt, CoreAttributeTypes.IdValue, value);
         } else {
            AWorkbench.popup("ERROR", value + " is not valid.");
            return null;
         }
      }
      changes.addChild(parentFolder, enumArt);
      changes.executeIfNeeded();
      return enumArt;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      try {
         StringBuilder msg = new StringBuilder();
         msg.append("Please enter name and value(s). Note: one value per line. \n");
         msg.append(
            "The purpose of the Enumerated Artifact is to dynamically add, edit, or remove value(s) used to populate a certain Attribute Type. \n");
         CreateEnumeratedArtifactDialog dialog = new CreateEnumeratedArtifactDialog(TITLE, msg.toString());
         if (dialog.open() == Window.OK) {
            String name = dialog.getXtextString(CreateEnumeratedArtifactDialog.NAME_ID);
            String value = dialog.getXtextString(CreateEnumeratedArtifactDialog.VALUE_ID);
            if (Strings.isValid(name) && Strings.isValid(value)) {
               List<String> values = new ArrayList<>();
               for (String val : value.split("\r\n")) {
                  val = val.replaceAll("^ *", "");
                  val = val.replaceAll(" *$", "");
                  if (Strings.isValid(val)) {
                     if (!values.contains(val)) {
                        values.add(val);
                     }
                  } else {
                     AWorkbench.popup("ERROR", value + " is not valid.");
                  }
               }
               ArtifactToken enumArtFolder = getOrCreateEnumeratedArtifactFolder();
               ArtifactToken enumArt = createEnumeratedArtifact(enumArtFolder, name, values);
               ArtifactEditor.editArtifact((Artifact) enumArt);
            } else {
               AWorkbench.popup("ERROR", name + " and/or " + value + " are not valid.");
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN, XNavItemCat.OSEE_ADMIN);
   }
}
