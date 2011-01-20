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
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionProviders;
import org.eclipse.osee.ats.workdef.WorkDefinitionSheet;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.PluginUtil;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class DoesNotWorkItemAts extends XNavigateItemAction {

   public DoesNotWorkItemAts(XNavigateItem parent) {
      super(parent, "Does Not Work - ATS - Load WorkDef_Team_Default.ats old/new way", PluginUiImage.ADMIN);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), getName(), getName())) {
         return;
      }
      AtsWorkDefinitionProviders.loadTeamWorkDefFromFileOldWay();
      AtsWorkDefinitionProviders.loadTeamWorkDefFromFileNewWay();

      PluginUtil util = new PluginUtil("org.eclipse.osee.ats");
      String filename = "support/WorkDef_Team_Default.ats";
      try {
         File file = util.getPluginFile(filename);
         if (!file.exists()) {
            System.err.println("File " + filename + " doesn't exist");
         }

         WorkDefinitionSheet sheet = new WorkDefinitionSheet("WorkDef_Team_Default", "osee.ats.teamWorkflow", file);
         AtsWorkDefinitionProviders.loadWorkFlowDefinitionFromFile(sheet);
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      //      SkynetTransaction transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), "Admin Cleanup");
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
