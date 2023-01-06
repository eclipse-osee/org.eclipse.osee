/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.agile.navigate;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.agile.jira.JiraByEpicData;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class SyncJiraAndOseeByEpicBlam extends AbstractBlam {

   private static final String NAME = "Sync JIRA by Epic";

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {

      return new AbstractOperation(getName(), Activator.PLUGIN_ID, logger) {

         final String tabDelimStr = variableMap.getString("Jira by Epic Query Results");

         @Override
         protected void doWork(IProgressMonitor monitor) throws Exception {
            JiraByEpicData data = new JiraByEpicData();
            data.setTabDelimReport(tabDelimStr);
            data = AtsApiService.get().getServerEndpoints().getActionEndpoint().reportEpicDiffsByEpic(data);
            XResultDataUI.report(data.getResults(), getName());
         }
      };
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AgileNavigateItemProvider.AGILE);
   }

   @Override
   public String getXWidgetsXml() {
      StringBuilder sb = new StringBuilder();
      sb.append("<xWidgets>");
      sb.append("<XWidget xwidgetType=\"XText\" fill=\"Vertically\" displayName=\"Jira by Epic Query Results\" />");
      sb.append("</xWidgets>");
      return sb.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "Copy/Paste JIRA report by epic and team from exported Excel spreadsheet.\n" //
         + "Returns differences between JIRA and OSEE for resulting entries.\n" //
         + "Report should have Summary, ID, Epic Link, Description, Status, Story Points columns";
   }

}