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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactChecks;
import org.eclipse.osee.framework.skynet.core.artifact.IArtifactCheck;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * Calls extensions to determine if artifacts are valid to delete.
 * 
 * @author Donald G. Dunne
 */
public class ValidateArtifactsToDelete extends AbstractBlam {

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      processChange(variableMap.getArtifacts("artifact"));
   }

   @Override
   public String getName() {
      return "Validate Artifacts To Delete";
   }

   /**
    * Changes the descriptor of the artifacts to the provided artifact descriptor
    */
   private void processChange(List<Artifact> artifacts) {
      if (artifacts.isEmpty()) {
         throw new IllegalArgumentException("The artifact list can not be empty");
      }

      XResultData rd = new XResultData();
      rd.log("Validation Artifacts: " + Artifacts.toString("; ", artifacts));
      // Confirm artifacts are fit to delete
      try {
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            try {
               IStatus result = check.isDeleteable(artifacts);
               if (!result.isOK()) {
                  rd.error(result.getMessage());
               }
            } catch (Exception ex) {
               log(ex);
               rd.log("Exception occurred...see error log" + ex.getLocalizedMessage());
            }
         }
         rd.log("\n");
         rd.log("Validation Complete - Any errors will be displayed.");
         XResultDataUI.report(rd, "Validate Artifacts to Delete");
      } catch (Exception ex) {
         log(ex);
      }
   }

   @Override
   public String getDescriptionUsage() {
      return "Drag in Artifacts and press Play button on top right.  Artifacts will be validated for deletion by framework. NOTE: Artifacts will NOT be deleted, only validated for deletion.";
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifact\" /></xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}