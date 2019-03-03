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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class CheckDefaulHierarchy extends AbstractBlam {
   @Override
   public String getName() {
      return "Check Default Hierarchy";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      BranchId branch = variableMap.getBranch("Branch");
      ArtifactTypeToken artifactType = variableMap.getArtifactType("Artifact Type");
      List<Artifact> artifacts = ArtifactQuery.getArtifactListFromType(artifactType, branch, EXCLUDE_DELETED);
      for (Artifact artifact : artifacts) {
         try {
            if (!artifact.hasParent()) {
               logf("\n" + artifact.getGuid() + " has no parent");
            }
         } catch (MultipleArtifactsExist ex) {
            logf("\n" + ex.getLocalizedMessage());
         }
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" />" +
      //
         "<XWidget xwidgetType=\"XArtifactTypeComboViewer\" displayName=\"Artifact Type\" /></xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin.Health");
   }

   @Override
   public String getTarget() {
      return TARGET_ALL;
   }

}
