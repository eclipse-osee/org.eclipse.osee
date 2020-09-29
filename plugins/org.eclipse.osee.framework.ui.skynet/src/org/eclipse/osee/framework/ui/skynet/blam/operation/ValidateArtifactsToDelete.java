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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.access.internal.OseeApiService;
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
      rd.log("Validation Artifacts: " + Collections.toString("; ", artifacts));
      // Confirm artifacts are fit to delete
      try {
         OseeApiService.get().getAccessControlService().isDeleteable(Collections.castAll(artifacts), rd);
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

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return java.util.Collections.singleton(CoreUserGroups.Everyone);
   }

}