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
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.api.agile.jira.JiraDiffData;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;

/**
 * @author Stephen J. Molaro
 */
public class SyncJiraAndOseeBlam extends AbstractBlam {

   private static final String NAME = "Sync JIRA by Program Increment";

   @Override
   public String getName() {
      return NAME;
   }

   @Override
   public IOperation createOperation(VariableMap variableMap, OperationLogger logger) throws Exception {

      return new AbstractOperation(getName(), Activator.PLUGIN_ID, logger) {

         final String programIncrement = variableMap.getString("Program Increment");
         final String teamId = variableMap.getString("Team Id");

         @Override
         protected void doWork(IProgressMonitor monitor) throws Exception {
            JiraDiffData data = new JiraDiffData();
            data.setProgramIncrement(programIncrement);
            data.setTeamId(teamId);
            data = AtsApiService.get().getServerEndpoints().getActionEndpoint().reportEpicDiffs(data);
            XResultDataUI.report(data.getResults(), getName());
         }
      };
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AgileNavigateItemProvider.AGILE);
   }

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      XWidgetBuilder wb = new XWidgetBuilder();
      wb.andWidget("Program Increment", "XText").endWidget();
      wb.andWidget("Team Id", "XText").endWidget();
      return wb.getItems();
   }

   @Override
   public String getDescriptionUsage() {
      return "Enter the Program Increment (PI) associated your epics (e.g. PI20). "//
         + "Team Id is your team id in Jira (represented by a numerical value).";

   }
}