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

package org.eclipse.osee.ats.config.wizard;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigWizardPage1 extends WizardPage {
   private XWidgetPage page;

   public AtsConfigWizardPage1() {
      super("Create New ATS Configuration", "Create ATS Configuration", null);
      setMessage("Enter configuration information.");
   }

   private final XModifiedListener xModListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         getContainer().updateButtons();
      }
   };

   public String getTeamDefName() {
      return (String) getXWidget(TEAMDEF_NAME).getData();
   }

   public List<String> getActionableItems() {
      List<String> aias = new ArrayList<>();
      for (String aia : ((String) getXWidget(ACTIONABLE_ITEMS).getData()).split(",")) {
         aia = aia.replaceAll("^ *", "");
         aia = aia.replaceAll(" *$", "");
         if (!aia.equals("")) {
            aias.add(aia);
         }
      }
      return aias;
   }

   public List<String> getVersions() {
      List<String> versions = new ArrayList<>();
      for (String version : ((String) getXWidget(VERSIONS).getData()).split(",")) {
         version = version.replaceAll("^ *", "");
         version = version.replaceAll(" *$", "");
         if (!version.equals("")) {
            versions.add(version);
         }
      }
      return versions;
   }

   public String getWorkDefinitionName() {
      return (String) getXWidget(WORKFLOW_ID).getData();
   }

   private final static String TEAMDEF_NAME = "Team Definition Name";
   private final static String ACTIONABLE_ITEMS = "Actionable Item(s) (comma delim)";
   private final static String VERSIONS = "Versions (comma delim)";
   private final static String WORKFLOW_ID = "WorkDefinition Name";

   @Override
   public void createControl(Composite parent) {

      try {
         String xWidgetXml = "<WorkPage>" +
         //
            "<XWidget displayName=\"" + TEAMDEF_NAME + "\" required=\"true\" xwidgetType=\"XText\"/>" +
            //
            "<XWidget displayName=\"" + ACTIONABLE_ITEMS + "\" required=\"true\" xwidgetType=\"XText\"/>" +
            //
            "<XWidget displayName=\"" + VERSIONS + "\" required=\"false\" xwidgetType=\"XText\"/>" +
            //
            "<XWidget displayName=\"" + WORKFLOW_ID + "\" required=\"false\" xwidgetType=\"XText\"/>" +
            //
            "</WorkPage>";
         Composite comp = new Composite(parent, SWT.NONE);
         comp.setLayout(new GridLayout(1, false));
         comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

         page = new XWidgetPage(xWidgetXml, ATSXWidgetOptionResolver.getInstance());
         page.createBody(null, comp, null, xModListener, true);

         Button populateExampleButton = new Button(comp, SWT.PUSH);
         populateExampleButton.setText("Populate with example entries");
         populateExampleButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               super.widgetSelected(e);
               try {
                  ((XText) getXWidget(TEAMDEF_NAME)).set("SAW Labs");
                  ((XText) getXWidget(ACTIONABLE_ITEMS)).set("Lab Station, Lab Computer, Lab Fire System");
                  ((XText) getXWidget(VERSIONS)).set("SAW 1.0, SAW 2.0, SAW 3.0");
                  ((XText) getXWidget(WORKFLOW_ID)).setText(
                     "WorkDef_Team_SawLabs" + AtsUtil.getAtsDeveloperIncrementingNum());
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
         setControl(comp);
         ((XText) getXWidget(WORKFLOW_ID)).setText("WorkDef_Team_SawLabs" + AtsUtil.getAtsDeveloperIncrementingNum());

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public XWidget getXWidget(String attrName) {
      if (page == null) {
         throw new OseeArgumentException("WorkPage == null");
      }
      return page.getLayoutData(attrName).getXWidget();
   }

   @Override
   public boolean isPageComplete() {
      if (!page.isPageComplete().isTrue()) {
         return false;
      }

      return true;
   }

}
