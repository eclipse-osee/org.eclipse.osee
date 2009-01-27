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

package org.eclipse.osee.ats.workflow.editor.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class NewWorkflowConfigPage1 extends WizardPage {
   private final AtsWorkflowConfigCreationWizard wizard;
   private WorkPage page;

   /**
    * @param actionWizard
    */
   public NewWorkflowConfigPage1(AtsWorkflowConfigCreationWizard actionWizard) {
      super("Create new ATS Workflow Configuration", "Create ATS Workflow Configuration", null);
      setMessage("Enter workflow namespace.");
      this.wizard = actionWizard;
   }

   private final XModifiedListener xModListener = new XModifiedListener() {
      public void widgetModified(XWidget widget) {
         getContainer().updateButtons();
      }
   };

   public String getNamespace() {
      return (String) getXWidget("Namespace").getData();
   }

   public void createControl(Composite parent) {

      try {
         String xWidgetXml =
               "<WorkPage><XWidget displayName=\"Namespace\" required=\"true\" xwidgetType=\"XText\"/></WorkPage>";
         Composite comp = new Composite(parent, SWT.NONE);
         comp.setLayout(new GridLayout(1, false));
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         page = new WorkPage(xWidgetXml, ATSXWidgetOptionResolver.getInstance());
         page.createBody(null, comp, null, xModListener, true);

         setControl(comp);
         ((XText) getXWidget("Namespace")).setFocus();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public XWidget getXWidget(String attrName) {
      if (page == null) throw new IllegalArgumentException("WorkPage == null");
      return page.getLayoutData(attrName).getXWidget();
   }

   @Override
   public boolean isPageComplete() {
      if (!page.isPageComplete().isTrue()) return false;

      return true;
   }

}
