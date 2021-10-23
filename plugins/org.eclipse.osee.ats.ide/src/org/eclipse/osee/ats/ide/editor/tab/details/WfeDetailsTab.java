/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.details;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.access.IAtsAccessService;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.WfeAbstractTab;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AccessContextToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.FormsUtil;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * @author Donald G. Dunne
 */
public class WfeDetailsTab extends WfeAbstractTab {
   private ScrolledForm scrolledForm;
   public final static String ID = "ats.details.tab";
   private final WorkflowEditor editor;
   private Browser browser;

   public WfeDetailsTab(WorkflowEditor editor, IAtsWorkItem workItem) {
      super(editor, ID, workItem, "Details");
      this.editor = editor;
   }

   @Override
   protected void createFormContent(IManagedForm managedForm) {
      super.createFormContent(managedForm);

      scrolledForm = managedForm.getForm();
      try {

         bodyComp = scrolledForm.getBody();
         GridLayout gridLayout = new GridLayout(1, true);
         bodyComp.setLayout(gridLayout);
         GridData gd = new GridData(SWT.LEFT, SWT.LEFT, true, true);
         bodyComp.setLayoutData(gd);

         browser = new Browser(bodyComp, SWT.NONE);
         browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         updateTitleBar(managedForm);
         createToolbar(managedForm);
         FormsUtil.addHeadingGradient(editor.getToolkit(), managedForm.getForm(), true);

         refresh();
      } catch (Exception ex) {
         handleException(ex);
      }
   }

   public void refresh() {
      refreshBrowser(browser, editor, getManagedForm());
   }

   public static void refreshBrowser(Browser browser, WorkflowEditor editor, IManagedForm managedForm) {
      if (Widgets.isAccessible(browser)) {
         IAtsWorkItem workItem = editor.getWorkItem();

         try {
            Map<String, String> smaDetails = Artifacts.getDetailsKeyValues((Artifact) workItem.getStoreObject());
            addSMADetails(workItem, smaDetails);

            FontData systemFont = browser.getDisplay().getSystemFont().getFontData()[0];
            String formattedDetails =
               Artifacts.getDetailsFormText(smaDetails, systemFont.getName(), systemFont.getHeight());
            browser.setText(formattedDetails);
         } catch (Exception ex) {
            browser.setText(Lib.exceptionToString(ex));
         }
         managedForm.reflow(true);
      }
   }

   private static void addSMADetails(IAtsWorkItem workItem, Map<String, String> details) {
      details.put("Workflow Definition", workItem.getWorkDefinition().getName());
      IAtsAction parentAction = workItem.getParentAction();
      if (parentAction == null) {
         details.put("Action Id", "No Parent Action");
      } else {
         details.put("Action Id", parentAction.getAtsId());
      }
      if (!workItem.isOfType(AtsArtifactTypes.TeamWorkflow) && workItem.getParentTeamWorkflow() != null) {
         details.put("Parent Team Workflow Id", workItem.getParentTeamWorkflow().getAtsId());
      }
      if (workItem.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         details.put("Working Branch Access Context Id", getAccessContextId((TeamWorkFlowArtifact) workItem));
      }
   }

   private static String getAccessContextId(TeamWorkFlowArtifact workflow) {
      String message;
      IAtsAccessService accessService = AtsApiService.get().getAtsAccessService();
      BranchId workingBranch = null;
      try {
         workingBranch = workflow.getWorkingBranch();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      Collection<? extends AccessContextToken> ids = null;
      if (workingBranch == null) {
         try {
            // get what would be if branch created
            ids = accessService.getFromWorkflow(workflow);
            message = ids.toString();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            message = String.format("Error getting context id [%s]", ex.getMessage());
         }
      } else {
         try {
            ids = accessService.getContextIds(workingBranch);
            message = ids.toString();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            message = String.format("Error getting context id [%s]", ex.getMessage());
         }
      }
      return message;
   }

}
