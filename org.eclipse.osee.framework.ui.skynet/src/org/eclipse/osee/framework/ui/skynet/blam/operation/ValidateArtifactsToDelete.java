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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.sql.SQLException;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChecks;
import org.eclipse.osee.framework.skynet.core.artifact.IArtifactCheck;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;

/**
 * Calls extensions to determine if artifacts are valid to delete.
 * 
 * @author Don G. Dunne
 */
public class ValidateArtifactsToDelete extends AbstractBlam {

   @SuppressWarnings("unchecked")
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      processChange(variableMap.getArtifacts("artifact"));
   }

   /**
    * Changes the descriptor of the artifacts to the provided artifact descriptor
    * 
    * @param artifacts
    * @param descriptor
    * @throws SQLException
    */
   private void processChange(List<Artifact> artifacts) throws SQLException {
      if (artifacts.isEmpty()) {
         throw new IllegalArgumentException("The artifact list can not be empty");
      }

      XResultData rd = new XResultData(SkynetGuiPlugin.getLogger());
      // Confirm artifacts are fit to delete
      try {
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            try {
               Result result = check.isDeleteable(artifacts);
               if (result.isFalse()) rd.logError(result.getText());
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
               rd.log("Exception occurred...see error log" + ex.getLocalizedMessage());
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      rd.log("\n\n\n");
      rd.log("Validation Artifacts: " + Artifacts.commaArts(artifacts));
      rd.report("Validate Artifacts to Delete");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getDescriptionUsage()
    */
   @Override
   public String getDescriptionUsage() {
      return "Drag in Artifacts and press Play button on top right.  Artifacts will be validated for deletion by framework. NOTE: Artifacts will NOT be deleted, only validated for deletion.";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifact\" /></xWidgets>";
   }
}