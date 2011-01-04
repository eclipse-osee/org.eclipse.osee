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
package org.eclipse.osee.ats.navigate;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionProviders;
import org.eclipse.osee.ats.workdef.IAtsWorkDefinitionProvider;
import org.eclipse.osee.ats.workdef.WorkDefinition;
import org.eclipse.osee.ats.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.workflow.flow.TeamWorkflowDefinition;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;

public class ConvertWorkFlowDefinitions extends XNavigateItemAction {

   public ConvertWorkFlowDefinitions(XNavigateItem parent) {
      super(parent, "Convert Work Flow Definition(s)", AtsImage.REPORT);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      XResultData resultData = new XResultData();

      Set<Artifact> workFlowDefs = new HashSet<Artifact>();
      WorkFlowDefinition selectedWorkFlowDef = null;
      for (WorkItemDefinition item : WorkItemDefinitionFactory.getWorkItemDefinitions()) {
         if (item instanceof WorkFlowDefinition) {
            workFlowDefs.add(((WorkFlowDefinition) item).getArtifact());
            if (item.getId().equals(TeamWorkflowDefinition.ID)) {
               selectedWorkFlowDef = (WorkFlowDefinition) item;
            }
         }
      }

      // Don't dialog till get conversion working
      //      ArtifactCheckTreeDialog dialog = new ArtifactCheckTreeDialog(workFlowDefs);
      //      if (dialog.open() == 0) {
      //         for (Object obj : dialog.getResult()) {
      convert(selectedWorkFlowDef, resultData);
      //         }
      resultData.report(getName());
      //      }
   }

   private void convert(WorkFlowDefinition workFlowDef, XResultData resultData) throws OseeCoreException {
      WorkDefinition workDef = WorkDefinitionFactory.translateToWorkDefinition(workFlowDef);
      IAtsWorkDefinitionProvider provider = AtsWorkDefinitionProviders.getAtsTeamWorkflowExtensions().iterator().next();
      provider.convertAndOpenAtsDsl(workDef, resultData);
   }
}
