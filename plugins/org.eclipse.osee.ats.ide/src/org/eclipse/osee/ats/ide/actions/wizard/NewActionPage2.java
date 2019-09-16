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

package org.eclipse.osee.ats.ide.actions.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XCombo;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetPage;
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
   private XWidgetPage page;
   private final NewActionWizard wizard;
   private boolean debugPopulated = false;
   protected final static String DESCRIPTION = "Description";
   protected static final String CHANGE_TYPE = "Change Type";
   protected static final String PRIORITY = "Priority";
   protected static final String DEADLINE = "Deadline";
   protected static final String VALIDATION_REQUIRED = "Validation Required";

   public NewActionPage2(NewActionWizard wizard) {
      super("Create new ATS Action", "Create ATS Action", null);
      this.wizard = wizard;
      setMessage("Enter description, priority, change type and select Finish.");
   }

   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      try {
         if (wizard.getInitialDescription() != null && ((XText) getXWidget(DESCRIPTION)).get().equals("")) {
            ((XText) getXWidget(DESCRIPTION)).set(wizard.getInitialDescription());
         }
         if (wizard.isTTAction()) {
            handlePopulateWithDebugInfo();
         }
         wizard.createPage3IfNecessary();
         ((XText) getXWidget(DESCRIPTION)).getStyledText().setFocus();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   private final XModifiedListener xModListener = new XModifiedListener() {
      @Override
      public void widgetModified(XWidget widget) {
         getContainer().updateButtons();
      }
   };

   protected String getWidgetXml() {
      StringBuffer stringBuffer = new StringBuffer(800);
      stringBuffer.append("<WorkPage>");
      stringBuffer.append(
         "<XWidget displayName=\"" + DESCRIPTION + "\" height=\"80\" required=\"true\" xwidgetType=\"XText\" fill=\"Vertically\" toolTip=\"" + AtsAttributeTypes.Description.getDescription() + "\"/>");
      appendCustomWidgetXml(stringBuffer);
      stringBuffer.append(
         "<XWidget displayName=\"" + CHANGE_TYPE + "\"  storageName=\"ats.Change Type\" xwidgetType=\"XCombo(" + ATSXWidgetOptionResolver.OPTIONS_FROM_ATTRIBUTE_VALIDITY + ")\" required=\"true\" horizontalLabel=\"true\" toolTip=\"" + AtsAttributeTypes.ChangeType.getDescription() + "\"/>");
      stringBuffer.append(
         "<XWidget displayName=\"" + PRIORITY + "\" storageName=\"ats.Priority\" xwidgetType=\"XCombo(" + ATSXWidgetOptionResolver.OPTIONS_FROM_ATTRIBUTE_VALIDITY + ")\" required=\"true\" horizontalLabel=\"true\"/>");
      stringBuffer.append(
         "<XWidget displayName=\"" + DEADLINE + "\" xwidgetType=\"XDate\" horizontalLabel=\"true\" toolTip=\"" + AtsAttributeTypes.NeedBy.getDescription() + "\"/>");
      stringBuffer.append(
         "<XWidget displayName=\"" + VALIDATION_REQUIRED + "\" xwidgetType=\"XCheckBox\" fill=\"Vertically\" horizontalLabel=\"true\" labelAfter=\"true\" toolTip=\"" + AtsAttributeTypes.ValidationRequired.getDescription() + "\"/>");
      stringBuffer.append("</WorkPage>");
      return stringBuffer.toString();
   }

   protected void appendCustomWidgetXml(StringBuffer stringBuffer) {
      // provided for subclass implementation
   }

   @Override
   public void createControl(Composite parent) {

      try {
         String widgetXml = getWidgetXml();

         Composite comp = new Composite(parent, SWT.NONE);
         comp.setLayout(new GridLayout(1, false));
         comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

         page = new XWidgetPage(widgetXml, ATSXWidgetOptionResolver.getInstance());
         page.createBody(null, comp, null, xModListener, true);

         ((XText) getXWidget(DESCRIPTION)).getLabelWidget().addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  handlePopulateWithDebugInfo();
               }
            }
         });
         ((XCheckBox) getXWidget(VALIDATION_REQUIRED)).getLabelWidget().addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  handlePopulateWithDebugInfo();
               }
            }
         });

         setControl(comp);
         setHelpContexts();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   protected boolean addValidation() {
      return true;
   }

   protected boolean hasNeedByDate() {
      return true;
   }

   private void setHelpContexts() {
      HelpUtil.setHelp(this.getControl(), AtsHelpContext.NEW_ACTION_PAGE_2);
   }

   private void handlePopulateWithDebugInfo() {
      if (debugPopulated) {
         return;
      }
      try {
         ((XText) getXWidget(DESCRIPTION)).set("See title");
         // Must use skynet attribute name cause these widget uses the OPTIONS_FROM_ATTRIBUTE_VALIDITY
         ((XCombo) getXWidget(PRIORITY)).set("4");
         ((XCombo) getXWidget(CHANGE_TYPE)).set("Improvement");
         debugPopulated = true;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public boolean isPageComplete() {
      if (page == null || !page.isPageComplete().isTrue()) {
         return false;
      }
      return true;
   }

   protected XWidget getXWidget(String displayName) {
      Conditions.checkNotNull(page, "WorkPage");
      return page.getLayoutData(displayName).getXWidget();
   }

}
