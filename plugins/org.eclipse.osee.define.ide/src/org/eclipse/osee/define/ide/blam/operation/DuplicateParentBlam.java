/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.api.DefineBranchEndpointApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.orcs.rest.client.OseeClient;

/**
 * @author Donald G. Dunne
 */
public class DuplicateParentBlam extends AbstractBlam {

   BranchId branch = null;

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets>" + //
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" />" + //
         "</xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define");
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      branch = variableMap.getBranch("Branch");

      if (branch == null || branch.isInvalid()) {
         log("Must select valid branch.");
         return;
      }

      OseeClient oseeClient = ServiceUtil.getOseeClient();
      DefineBranchEndpointApi defineBrchEp = oseeClient.getDefineBranchEndpoint();
      XResultData results = defineBrchEp.getChildrenWithMultipleParents(branch, ArtifactTypeId.SENTINEL);
      if (results.isErrors()) {
         List<ArtifactId> artifactIds = new ArrayList<>(results.getIds().size());
         for (String id : results.getIds()) {
            artifactIds.add(ArtifactId.valueOf(id));
         }
         logf(results.getResults().iterator().next());
         List<Artifact> artifacts = ArtifactQuery.getArtifactListFrom(artifactIds, branch);
         MassArtifactEditor.editArtifacts(getName(), artifacts);
      } else {
         log("No artifacts found with duplicate parents.");
      }

   }

   @Override
   public String getTarget() {
      return TARGET_ALL;
   }

}
