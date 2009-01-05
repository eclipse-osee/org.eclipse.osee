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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigWizardPage1 extends WizardPage {
   private final AtsConfigWizard wizard;
   private WorkPage page;

   /**
    * @param actionWizard
    */
   public AtsConfigWizardPage1(AtsConfigWizard actionWizard) {
      super("Create New ATS Configuration", "Create ATS Configuration", null);
      setMessage("Enter configuration information.");
      this.wizard = actionWizard;
   }

   private final XModifiedListener xModListener = new XModifiedListener() {
      public void widgetModified(XWidget widget) {
         getContainer().updateButtons();
      }
   };

   public String getNamespace() {
      return (String) getXWidget(CONFIG_NAMESPACE).getData();
   }

   public String getTeamDefName() {
      return (String) getXWidget(TEAMDEF_NAME).getData();
   }

   public List<String> getActionableItems() {
      List<String> aias = new ArrayList<String>();
      for (String aia : ((String) getXWidget(ACTIONABLE_ITEMS).getData()).split(",")) {
         aia.replaceAll("^ *", "");
         if (!aia.equals("")) {
            aias.add(aia);
         }
      }
      return aias;
   }

   public List<String> getVersions() {
      List<String> versions = new ArrayList<String>();
      for (String version : ((String) getXWidget(VERSIONS).getData()).split(",")) {
         version.replaceAll("^ *", "");
         if (!version.equals("")) {
            versions.add(version);
         }
      }
      return versions;
   }

   public String getWorkflowId() {
      return (String) getXWidget(WORKFLOW_ID).getData();
   }

   private static String CONFIG_NAMESPACE = "Configuration Namespace";
   private static String TEAMDEF_NAME = "Team Definition Name";
   private static String ACTIONABLE_ITEMS = "Actionable Item(s) (comma delim)";
   private static String VERSIONS = "Versions (comma delim)";
   private static String WORKFLOW_ID = "Workflow Id (blank to create default)";

   public void createControl(Composite parent) {

      try {
         String xWidgetXml = "<WorkPage>" +
         //
         "<XWidget displayName=\"" + CONFIG_NAMESPACE + "\" required=\"true\" xwidgetType=\"XText\"/>" +
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

         page = new WorkPage(xWidgetXml, ATSXWidgetOptionResolver.getInstance());
         page.createBody(null, comp, null, xModListener, true);

         setControl(comp);
         ((XText) getXWidget(CONFIG_NAMESPACE)).setFocus();

         ((XText) getXWidget(CONFIG_NAMESPACE)).getLabelWidget().addListener(SWT.MouseUp, new Listener() {
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  String namespace = "qwerty" + AtsLib.getAtsDeveloperIncrementingNum();
                  ((XText) getXWidget(CONFIG_NAMESPACE)).set(namespace);
                  ((XText) getXWidget(TEAMDEF_NAME)).set(namespace + " Team");
                  ((XText) getXWidget(ACTIONABLE_ITEMS)).set(namespace + " a, " + namespace + " b, " + namespace + " c");
                  ((XText) getXWidget(VERSIONS)).set(namespace + " 1.0, " + namespace + " 2.0, " + namespace + " 3.0");
               }
            }
         });

      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
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
