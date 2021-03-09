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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class CreateArtifactWithIdBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "Create Artifact With Id";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ArtifactTypeToken artifactType = variableMap.getArtifactType("Artifact Type");
      if (artifactType == null) {
         AWorkbench.popup("Must enter Artifact Type");
         return;
      }
      BranchToken branch = BranchManager.getBranchToken(variableMap.getBranch("Branch"));
      Long artId = null;
      if (branch == null) {
         AWorkbench.popup("Must enter Branch");
         return;
      }
      String name = variableMap.getString("Name");
      if (!Strings.isValid(name)) {
         AWorkbench.popup("Must enter name");
         return;
      }
      String idStr = variableMap.getString("Id (Must be int)");
      if (!Strings.isValid(idStr)) {
         AWorkbench.popup("Must enter id");
         return;
      }
      try {
         Integer.valueOf(idStr);
         artId = Long.valueOf(idStr);
      } catch (Exception ex) {
         AWorkbench.popup("Must enter id as int");
         return;
      }

      final Long fArtId = artId;
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            Branch brch = BranchManager.getBranch(branch);
            if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Create new Artifact", //
               String.format("Create new Artifact\n\n" + //
            "Type: %s\n" + //
            "Branch: %s\n" + //
            "Id: %s\n" + //
            "Name: [%s]\n\n" + //
            "WARNING, WARNING, WARNING: And you confirm you have checked the id does not already exist?", artifactType,
                  brch, fArtId, name))) {
               return;
            }

            SkynetTransaction transaction = TransactionManager.createTransaction(branch, getName());
            Artifact artifact = ArtifactTypeManager.addArtifact(artifactType, branch, name, fArtId);
            transaction.addArtifact(artifact);
            transaction.execute();

            ArtifactEditor.editArtifact(artifact);
         }
      });
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets>" + //
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" />" + //
         "<XWidget xwidgetType=\"XArtifactTypeComboViewer\" displayName=\"Artifact Type\" />" + //
         "<XWidget xwidgetType=\"XText\" displayName=\"Name\" />" + //
         "<XWidget xwidgetType=\"XText\" displayName=\"Id (Must be int)\" />" + //
         "</xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin/Define");
   }

   @Override
   public String getDescriptionUsage() {
      return "Create artifact of type with given id.\nWARNING: You must manually confirm that id is not already used.";
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return java.util.Collections.singleton(CoreUserGroups.OseeAdmin);
   }

}