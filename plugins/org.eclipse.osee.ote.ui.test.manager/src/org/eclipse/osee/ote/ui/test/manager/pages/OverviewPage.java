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
package org.eclipse.osee.ote.ui.test.manager.pages;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.internal.TestManagerPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author David Diepenbrock
 */
public class OverviewPage extends TestManagerPage implements IActionable {

   private static final String pageName = "Overview";
   private static final String release = "";
   Map<LabelEnum, Label> labelMap;

   private enum LabelEnum {
      TM_Release("This Test Manager will only work with TM Server Release " + release),
      Configuration,
      Contact,
      Description;

      private String toolTip;

      LabelEnum() {
         this("");
      }

      LabelEnum(String toolTip) {
         this.toolTip = toolTip;
      }

      public String getToolTipText() {
         return toolTip;
      }

      @Override
      public String toString() {
         return name().replaceAll("_", " ");
      }
   }

   /**
    * Creates and populates the Overview Page
    */
   public OverviewPage(Composite parent, int style, TestManagerEditor parentTestManager) {
      super(parent, style, parentTestManager);
      createPage();
      updateLabelText();
      computeScrollSize();

      // TODO: Change to use OteHelpContext
      HelpUtil.setHelp(this, "test_manager_overview_page", "org.eclipse.osee.ote.help.ui");
   }

   /**
    * @return Returns the pageName.
    */
   @Override
   public String getPageName() {
      return pageName;
   }

   /**
    * Refreshes the label's text based upon the test manager's model.
    */
   public void updateLabelText() {
      for (LabelEnum enumEntry : LabelEnum.values()) {
         String toSet = "";
         Label label = labelMap.get(enumEntry);
         switch (enumEntry) {
            case TM_Release:
               toSet = release;
               break;
            case Configuration:
               toSet = getTestManager().getModel().getConfiguration();
               break;
            case Contact:
               toSet = getTestManager().getModel().getContact();
               break;
            case Description:
               toSet = getTestManager().getModel().getDescription();
               break;
            default:
               break;
         }
         label.setText(toSet);
      }
   }

   @Override
   protected void createPage() {
      super.createPage();
      Composite parent = (Composite) getContent();
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      labelMap = new HashMap<LabelEnum, Label>();
      for (LabelEnum enumEntry : LabelEnum.values()) {
         Label label = new Label(composite, SWT.NONE);
         label.setText(enumEntry.toString() + ": ");

         Label updateableLabel = new Label(composite, SWT.NONE);
         updateableLabel.setToolTipText(enumEntry.getToolTipText());

         labelMap.put(enumEntry, updateableLabel);
      }
      OseeUiActions.addButtonToEditorToolBar(this, TestManagerPlugin.PLUGIN_ID, composite, TestManagerEditor.namespace,
         "Test Manager");

   }

   @Override
   public String getActionDescription() {
      return "";
   }

   @Override
   public boolean areSettingsValidForRun() {
      return true;
   }

   @Override
   public void restoreData() {
      // Do Nothing
   }

   @Override
   public void saveData() {
      // Do Nothing
   }

   @Override
   public String getErrorMessage() {
      return "";
   }

   @Override
   public boolean onConnection(ConnectionEvent event) {
      return false;
   }

   @Override
   public boolean onDisconnect(ConnectionEvent event) {
      return false;

   }

   @Override
   public boolean onConnectionLost() {
      return false;
   }

}
