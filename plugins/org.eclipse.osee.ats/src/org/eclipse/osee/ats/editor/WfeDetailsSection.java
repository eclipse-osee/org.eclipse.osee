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

import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.access.AtsBranchAccessManager;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class WfeDetailsSection extends SectionPart {

   private Browser browser;
   private final WorkflowEditor editor;
   private boolean sectionCreated = false;

   public WfeDetailsSection(WorkflowEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.editor = editor;
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("Details");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      // Only load when users selects section
      section.addListener(SWT.Activate, new Listener() {

         @Override
         public void handleEvent(Event e) {
            createSection();
         }
      });
   }

   private synchronized void createSection() {
      if (!sectionCreated) {
         final FormToolkit toolkit = getManagedForm().getToolkit();
         Composite composite = toolkit.createComposite(getSection(), toolkit.getBorderStyle() | SWT.WRAP);
         composite.setLayout(ALayout.getZeroMarginLayout());
         composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         composite.addDisposeListener(new DisposeListener() {

            @Override
            public void widgetDisposed(DisposeEvent e) {
               if (Widgets.isAccessible(browser)) {
                  browser.dispose();
               }
            }
         });

         browser = new Browser(composite, SWT.NONE);
         GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 200;
         gd.heightHint = 300;
         browser.setLayoutData(gd);

         getSection().setClient(composite);
         toolkit.paintBordersFor(composite);
         sectionCreated = true;
      }

      if (Widgets.isAccessible(browser)) {
         AbstractWorkflowArtifact workflow = editor.getAwa();

         try {
            Map<String, String> smaDetails = Artifacts.getDetailsKeyValues(workflow);
            addSMADetails(workflow, smaDetails);

            FontData systemFont = browser.getDisplay().getSystemFont().getFontData()[0];
            String formattedDetails =
               Artifacts.getDetailsFormText(smaDetails, systemFont.getName(), systemFont.getHeight());
            browser.setText(formattedDetails);
         } catch (Exception ex) {
            browser.setText(Lib.exceptionToString(ex));
         }
         getManagedForm().reflow(true);
      }
   }

   private void addSMADetails(AbstractWorkflowArtifact workflow, Map<String, String> details)  {
      details.put("Workflow Definition", workflow.getWorkDefinition().getName());
      ActionArtifact parentAction = workflow.getParentActionArtifact();
      if (parentAction == null) {
         details.put("Action Id", "No Parent Action");
      } else {
         details.put("Action Id", parentAction.getAtsId());
      }
      if (!workflow.isOfType(AtsArtifactTypes.TeamWorkflow) && workflow.getParentTeamWorkflow() != null) {
         details.put("Parent Team Workflow Id", workflow.getParentTeamWorkflow().getAtsId());
      }
      if (workflow.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         details.put("Working Branch Access Context Id", getAccessContextId((TeamWorkFlowArtifact) workflow));
      }
   }

   private String getAccessContextId(TeamWorkFlowArtifact workflow) {
      String message;
      CmAccessControl accessControl = workflow.getAccessControl();
      if (accessControl == null) {
         message = "AtsCmAccessControlService not found.";
      } else {
         BranchId workingBranch = null;
         try {
            workingBranch = workflow.getWorkingBranch();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         Collection<? extends IAccessContextId> ids = null;
         if (workingBranch == null) {
            try {
               // get what would be if branch created
               ids = AtsBranchAccessManager.internalGetFromWorkflow(workflow);
               message = ids.toString();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               message = String.format("Error getting context id [%s]", ex.getMessage());
            }
         } else {
            try {
               ids = accessControl.getContextId(AtsClientService.get().getUserServiceClient().getOseeUser(
                  AtsClientService.get().getUserService().getCurrentUser()), workingBranch);
               message = ids.toString();
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
               message = String.format("Error getting context id [%s]", ex.getMessage());
            }
         }
      }
      return message;
   }

}
