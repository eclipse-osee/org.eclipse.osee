/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.define.ide.blam.operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.api.DefineBranchEndpointApi;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

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
   public Collection<String> getCategoriesStr() {
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
      XResultData results = defineBrchEp.getChildrenWithMultipleParents(branch, ArtifactTypeToken.SENTINEL);
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
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Collections.singleton(CoreUserGroups.Everyone);
   }

}
