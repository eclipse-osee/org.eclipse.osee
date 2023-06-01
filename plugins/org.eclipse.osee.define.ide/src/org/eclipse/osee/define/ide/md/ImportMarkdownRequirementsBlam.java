/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.define.ide.md;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.api.md.DefineMarkdownEndpoint;
import org.eclipse.osee.define.api.md.DefineMarkdownImportData;
import org.eclipse.osee.framework.core.client.OseeClient;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class ImportMarkdownRequirementsBlam extends AbstractBlam {

   BranchToken branch = null;

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets>" + //
         "<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" />" + //
         "</xWidgets>";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE);
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      branch = (BranchToken) variableMap.getBranch("Branch");
      if (branch == null) {
         branch = BranchToken.SENTINEL;
      }

      if (branch == null || branch.isInvalid()) {
         log("Must select valid branch.");
         return;
      }

      OseeClient oseeClient = ServiceUtil.getOseeClient();
      DefineMarkdownEndpoint defineMdEp = oseeClient.getDefineMarkdownEndpoint();
      DefineMarkdownImportData data =
         new DefineMarkdownImportData(BranchId.valueOf(branch.getId()), CoreArtifactTokens.SoftwareRequirementsFolder);
      data = defineMdEp.importMarkdown(data);
      XResultDataUI.report(data.getRd(), getName());

   }

}
