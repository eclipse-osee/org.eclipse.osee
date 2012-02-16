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
package org.eclipse.osee.ats.util;

import java.io.File;
import java.io.FileOutputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.osee.ats.core.client.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.core.workdef.ConvertWorkDefinitionToAtsDsl;
import org.eclipse.osee.ats.core.workdef.ModelUtil;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.utility.IncrementingNum;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.ws.AWorkspace;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemAts extends XNavigateItemAction {

   public DoesNotWorkItemAts(XNavigateItem parent) {
      super(parent, "Does Not Work - ATS - ConvertSaveAndOpenWorkDefToAtsDsl", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      //      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
      //         return;
      //      }

      try {
         WorkDefinition workDef =
            WorkDefinitionFactory.getWorkDefinition(AtsWorkDefinitionSheetProviders.WORK_DEF_TEAM_DEFAULT).getWorkDefinition();

         XResultData resultData = new XResultData();
         ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(workDef, resultData);
         AtsDsl atsDsl = converter.convert(workDef.getName());

         String filename = workDef.getName() + IncrementingNum.get() + ".ats";
         File file = OseeData.getFile(filename);
         try {
            FileOutputStream outputStream = new FileOutputStream(file);
            ModelUtil.saveModel(atsDsl, "ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", outputStream, true);
            String contents = Lib.fileToString(file);

            //            contents = cleanupContents(atsDsl, workDef, contents);

            Lib.writeStringToFile(contents, file);
            IFile iFile = OseeData.getIFile(filename);
            AWorkspace.openEditor(iFile);
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      //      AtsWorkDefinitionProvider.get().loadTeamWorkDefFromFileOldWay();
      //      AtsWorkDefinitionProvider.get().loadTeamWorkDefFromFileNewWay();

      //      PluginUtil util = new PluginUtil("org.eclipse.osee.ats");
      //      String filename = "support/WorkDef_Team_Default.ats";
      //      try {
      //         File file = util.getPluginFile(filename);
      //         if (!file.exists()) {
      //            System.err.println("File " + filename + " doesn't exist");
      //         }
      //
      //         WorkDefinitionSheet sheet = new WorkDefinitionSheet("WorkDef_Team_Default", "osee.ats.teamWorkflow", file);
      //         AtsWorkDefinitionProvider.get().loadWorkFlowDefinitionFromFile(sheet);
      //      } catch (Exception ex) {
      //         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      //      }

      //      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtil.getAtsBranch(), "Admin Cleanup");
      //      Artifact verArt =
      //            ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Version, "0.9.0", AtsUtil.getAtsBranch());
      //      for (Attribute<?> attr : verArt.getAttributes()) {
      //         if (attr.getAttributeType().getName().equals(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName())) {
      //            System.out.println("next " + attr.getValue());
      //            attr.delete();
      //            break;
      //         }
      //      }
      //      verArt.persist(transaction);
      //      verArt = ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Version, "0.8.2", AtsUtil.getAtsBranch());
      //      for (Attribute<?> attr : verArt.getAttributes()) {
      //         if (attr.getAttributeType().getName().equals(ATSAttributes.RELEASED_ATTRIBUTE.getStoreName())) {
      //            System.out.println("released " + attr.getValue());
      //            attr.delete();
      //            break;
      //         }
      //      }
      //      verArt.persist(transaction);
      //      transaction.execute();

      AWorkbench.popup("Completed", "Complete");
   }
}
