/*********************************************************************
 * Copyright (c) 2020 Boeing
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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 * @author Loren K. Ashley
 */
public class CreateArtifactWithIdBlam extends AbstractBlam {

   private static String blamDescription =
      "Create artifact of type with given id.\nWARNING: You must manually confirm that id is not already used.";
   private static String blamName = "Create Artifact With Id";
   private static String messageArtifactTypeMustEnter = "Must enter Artifact Type";
   private static String messageBranchMustEnter = "Must enter Branch";
   private static String messageIdMustBe32 = "(Must be an int greater than zero)";
   private static String messageIdMustBe64 = "(Must be a long greater than zero)";
   private static String messageIdMustEnter32 = "Must enter id as an int greater than zero";
   private static String messageIdMustEnter64 = "Must enter id as a long greater than zero";
   private static String messageNameMustEnter = "Must enter name";
   private static String variableMapArtifactType = "Artifact Type";
   private static String variableMapBranch = "Branch";
   private static String variableMapName = "Name";
   private final String messageIdMustBe;
   private final String messageIdMustEnter;
   private final boolean useLongIds;
   private final String variableMapId;

   public CreateArtifactWithIdBlam() {

      super(blamName, blamDescription, null);

      this.useLongIds = ArtifactToken.USE_LONG_IDS;

      if (this.useLongIds) {
         this.messageIdMustBe = messageIdMustBe64;
         this.messageIdMustEnter = messageIdMustEnter64;
      } else {
         this.messageIdMustBe = messageIdMustBe32;
         this.messageIdMustEnter = messageIdMustEnter32;
      }

      this.variableMapId = "Id ".concat(this.messageIdMustBe);
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return List.of(XNavigateItem.DEFINE_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      //@formatter:off
      return
         new XWidgetBuilder()
                .andWidget( variableMapBranch,       "XBranchSelectWidget"      ).endWidget()
                .andWidget( variableMapArtifactType, "XArtifactTypeComboViewer" ).endWidget()
                .andWidget( variableMapName,         "XText"                    ).endWidget()
                .andWidget( this.variableMapId,      "XText"                    ).endWidget()
                .getItems();
      //@formatter:on
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      var artifactType = variableMap.getArtifactType(variableMapArtifactType);
      if (Objects.isNull(artifactType)) {
         AWorkbench.popup(messageArtifactTypeMustEnter);
         return;
      }

      var branchId = variableMap.getBranch(variableMapBranch);
      if (Objects.isNull(branchId)) {
         AWorkbench.popup(messageBranchMustEnter);
      }

      var branchToken = BranchManager.getBranchToken(branchId);
      if (Objects.isNull(branchToken)) {
         AWorkbench.popup(messageBranchMustEnter);
         return;
      }

      var name = variableMap.getString(variableMapName);
      if (!Strings.isValid(name)) {
         AWorkbench.popup(messageNameMustEnter);
         return;
      }

      var idStr = variableMap.getString(this.variableMapId);
      if (!Strings.isValid(idStr)) {
         AWorkbench.popup(this.messageIdMustEnter);
         return;
      }

      Long artId;
      try {
         artId = this.useLongIds ? Long.valueOf(idStr) : Long.valueOf(Integer.valueOf(idStr));
      } catch (Exception ex) {
         AWorkbench.popup(this.messageIdMustEnter);
         return;
      }

      if (artId <= 0) {
         AWorkbench.popup(this.messageIdMustEnter);
         return;
      }

      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            var brch = BranchManager.getBranch(branchToken);
            if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Create new Artifact", //
               String.format("Create new Artifact\n\n" + //
            "Type: %s\n" + //
            "Branch: %s\n" + //
            "Id: %s\n" + //
            "Name: [%s]\n\n" + //
            "WARNING, WARNING, WARNING: And you confirm you have checked the id does not already exist?", artifactType,
                  brch, artId, name))) {
               return;
            }

            SkynetTransaction transaction = TransactionManager.createTransaction(branchToken, getName());
            Artifact artifact = ArtifactTypeManager.addArtifact(artifactType, branchToken, name, artId);
            transaction.addArtifact(artifact);
            transaction.execute();

            ArtifactEditor.editArtifact(artifact);
         }
      });
   }

}