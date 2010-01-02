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
package org.eclipse.osee.ats.editor;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowDebugSection extends SectionPart {

   private Composite workComp;
   private final XFormToolkit toolkit;
   private final StateMachineArtifact sma;

   /**
    * @param parent
    * @param toolkit
    * @param style
    * @param page
    * @param sma
    * @throws Exception
    */
   public SMAWorkFlowDebugSection(Composite parent, XFormToolkit toolkit, int style, StateMachineArtifact sma) throws OseeCoreException {
      super(parent, toolkit, style | Section.TWISTIE | Section.TITLE_BAR);
      this.sma = sma;
      this.toolkit = toolkit;
   }

   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);

      Section section = getSection();
      section.setText("Debug - Admin Only");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      workComp = toolkit.createClientContainer(section, 1);
      workComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      try {
         // Display team definition
         if (sma.isTeamWorkflow()) {
            TeamDefinitionArtifact teamDef = ((TeamWorkFlowArtifact) sma).getTeamDefinition();
            addDebug("Team Definition: " + teamDef);
            for (WorkRuleDefinition workItemDefinition : teamDef.getWorkRules()) {
               addDebug("        " + workItemDefinition.toString());
            }
         }

         // Display workflows
         addDebug("WorkflowId: " + sma.getWorkFlowDefinition().getId());
         if (sma.getWorkFlowDefinition().getParentId() != null && !sma.getWorkFlowDefinition().getParentId().equals(
               "")) addDebug("Inherit Workflow from Parent Id: " + sma.getWorkFlowDefinition().getParentId());
         for (WorkRuleDefinition workItemDefinition : sma.getWorkFlowDefinition().getWorkRules()) {
            addDebug("        " + workItemDefinition.toString());
         }

         // Display pages
         for (WorkPageDefinition atsPage : sma.getWorkFlowDefinition().getPagesOrdered()) {
            addDebug(atsPage.toString());
            for (WorkItemDefinition wid : atsPage.getWorkItems(true)) {
               addDebug("        " + wid.toString());
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

   }

   public void addDebug(String str) {
      toolkit.createText(workComp, str, SWT.MULTI | SWT.WRAP);
      workComp.layout();
   }

}
