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

import org.eclipse.osee.ats.editor.stateItem.AtsDebugWorkPage;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowDebugSection extends SMAWorkFlowSection {

   private Composite workComp;
   private XFormToolkit toolkit;

   /**
    * @param parent
    * @param toolkit
    * @param style
    * @param page
    * @param smaMgr
    */
   public SMAWorkFlowDebugSection(Composite parent, XFormToolkit toolkit, int style, SMAManager smaMgr) {
      super(parent, toolkit, style, new AtsDebugWorkPage(), smaMgr);
   }

   @Override
   protected Composite createWorkArea(Composite comp, AtsWorkPage page, XFormToolkit toolkit) {
      this.toolkit = toolkit;
      workComp = super.createWorkArea(comp, page, toolkit);
      toolkit.createLabel(workComp, "ATS Debug Section");

      addDebug("WorkflowId: " + smaMgr.getWorkFlow().getId());
      if (smaMgr.getWorkFlow().getInheritData() != null && !smaMgr.getWorkFlow().getInheritData().equals("")) addDebug("InheritData: " + smaMgr.getWorkFlow().getInheritData());
      for (org.eclipse.osee.ats.workflow.AtsWorkPage atsPage : smaMgr.getWorkFlow().getPagesOrdered()) {
         addDebug("      PageId: " + atsPage.getId());
         if (atsPage.isAllowCreateBranch()) addDebug("            " + AtsWorkPage.WORKPAGE_ATS_ALLOW_CREATE_BRANCH + ": " + atsPage.isAllowCreateBranch());
         if (atsPage.isAllowCreateBranch()) addDebug("            " + AtsWorkPage.WORKPAGE_ATS_ALLOW_COMMIT_BRANCH + ": " + atsPage.isAllowCommitBranch());
         if (atsPage.isForceAssigneesToTeamLeads()) addDebug("            " + AtsWorkPage.WORkPAGE_ATS_FORCE_ASSIGNEES_TO_TEAM_LEADS + ": " + atsPage.isForceAssigneesToTeamLeads());
         if (atsPage.isRequireStateHoursSpentPrompt()) addDebug("            " + AtsWorkPage.WORKPAGE_ATS_REQUIRE_STATE_HOURS_SPENT_PROMPT + ": " + atsPage.isRequireStateHoursSpentPrompt());
         if (atsPage.isStartPage()) addDebug("            " + AtsWorkPage.WORKPAGE_STARTPAGE + ": " + atsPage.isStartPage());
         if (atsPage.isValidatePage()) addDebug("            " + AtsWorkPage.WORKPAGE_VALIDATE_PAGE + ": " + atsPage.isValidatePage());
         for (IAtsStateItem stateItem : smaMgr.getStateItems().getStateItems(atsPage.getId())) {
            addDebug("            StateItem: " + stateItem.getDescription());
         }
      }

      // Button button = toolkit.createButton(workComp, "Return to \"" + item.getState() + "\"",
      // SWT.PUSH);
      // button.addListener(SWT.MouseUp, new Listener() {
      // public void handleEvent(Event event) {
      // handleUnComplete(fItem.getState());
      // }
      // });
      return workComp;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ats.editor.SMAWorkFlowSection#createPage(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Section createPage(Composite comp) {
      Section section = super.createPage(comp);
      return section;
   }

   public void addDebug(String str) {
      toolkit.createText(workComp, str, SWT.MULTI | SWT.WRAP);
      workComp.layout();
   }

}
