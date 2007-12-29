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

package org.eclipse.osee.ats.actions.wizard;

import java.sql.SQLException;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XList;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class NewActionPage2 extends WizardPage {
   private AtsWorkPage page;
   private final NewActionWizard wizard;

   /**
    * @param wizard -
    */
   public NewActionPage2(NewActionWizard wizard) {
      super("Create new ATS Action", "Create ATS Action", null);
      this.wizard = wizard;
      setMessage("Enter description, priority, change type and select Finish.");
   }

   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      if (wizard.getInitialDescription() != null) ((XText) getXWidget("Description")).set(wizard.getInitialDescription());
      if (wizard.isTTAction()) handlePopulateWithDebugInfo();
      wizard.createPage3IfNecessary();
      ((XText) getXWidget("Description")).getStyledText().setFocus();
   }

   private XModifiedListener xModListener = new XModifiedListener() {
      public void widgetModified(XWidget widget) {
         getContainer().updateButtons();
      }
   };

   public void createControl(Composite parent) {

      StringBuffer sb = new StringBuffer();
      sb.append("<WorkPage><XWidget displayName=\"Description\" height=\"80\" required=\"true\" xwidgetType=\"XText\" fill=\"Vertically\" toolTip=\"" + ATSAttributes.DESCRIPTION_ATTRIBUTE.getDescription() + "\"/>");
      sb.append("<XWidget displayName=\"Change Type\" storageName=\"ats.Change Type\" xwidgetType=\"XCombo(" + ATSXWidgetOptionResolver.OPTIONS_FROM_ATTRIBUTE_VALIDITY + ")\" required=\"true\" horizontalLabel=\"true\" toolTip=\"" + ATSAttributes.CHANGE_TYPE_ATTRIBUTE.getDescription() + "\"/>");
      sb.append("<XWidget displayName=\"Priority\" storageName=\"ats.Priority\" xwidgetType=\"XCombo(" + ATSXWidgetOptionResolver.OPTIONS_FROM_ATTRIBUTE_VALIDITY + ")\" required=\"true\" horizontalLabel=\"true\"/>");
      sb.append("<XWidget displayName=\"Deadline\" xwidgetType=\"XDate\" horizontalLabel=\"true\" toolTip=\"" + ATSAttributes.DEADLINE_ATTRIBUTE.getDescription() + "\"/>");
      sb.append("<XWidget displayName=\"Validation Required\" xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" toolTip=\"" + ATSAttributes.VALIDATION_REQUIRED_ATTRIBUTE.getDescription() + "\"/>");
      sb.append("<XWidget displayName=\"User Community\" storageName=\"ats.User Community\" xwidgetType=\"XList(" + ATSXWidgetOptionResolver.OPTIONS_FROM_ATTRIBUTE_VALIDITY + ")\" required=\"true\" toolTip=\"" + ATSAttributes.USER_COMMUNITY_ATTRIBUTE.getDescription() + "\"/></WorkPage>");

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(new GridLayout(2, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      page = new AtsWorkPage("Action", "", sb.toString(), ATSXWidgetOptionResolver.getInstance());
      page.createBody(null, comp, null, xModListener, true);

      ((XText) getXWidget("Description")).getLabelWidget().addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(Event event) {
            if (event.button == 3) {
               handlePopulateWithDebugInfo();
            }
         }
      });

      setControl(comp);
      setHelpContexts();
   }

   private void setHelpContexts() {
      AtsPlugin.getInstance().setHelp(this.getControl(), "new_action_wizard_page_2");
   }

   public void handlePopulateWithDebugInfo() {
      try {
         ((XText) getXWidget("Description")).set("See title");
         // Must use skynet attribute name cause these widget uses the OPTIONS_FROM_ATTRIBUTE_VALIDITY
         ((XList) getXWidget("ats.User Community")).setSelected("Other");
         ((XCombo) getXWidget("ats.Priority")).set("3");
         ((XCombo) getXWidget("ats.Change Type")).set("Improvement");
      } catch (IllegalStateException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   public void update() {
      getContainer().updateButtons();
   }

   @Override
   public boolean isPageComplete() {
      if (!page.isPageComplete().isTrue()) return false;
      return true;
   }

   public XWidget getXWidget(String attrName) {
      if (page == null) throw new IllegalArgumentException("WorkPage == null");
      return page.getLayoutData(attrName).getXWidget();
   }

}
