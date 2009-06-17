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
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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

      public String toString() {
         return name().replaceAll("_", " ");
      }
   }

   /**
    * Creates and populates the Overview Page
    * 
    * @param parent
    * @param style
    * @param parentTestManager
    */
   public OverviewPage(Composite parent, int style, TestManagerEditor parentTestManager) {
      super(parent, style, parentTestManager);
      createPage();
      updateLabelText();
      computeScrollSize();
      TestManagerPlugin.getInstance().setHelp(this, "tm_overview_page");
   }

   /**
    * @return Returns the pageName.
    */
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

         if (enumEntry.equals(LabelEnum.TM_Release)) {
            updateableLabel.addMouseListener(new MouseAdapter() {

               public void mouseDoubleClick(MouseEvent e) {
                  // Double right click here to turn on the debug for the plug-in
                  if (e.button == 3) {
                     debug.setGlobalDebug();
                  }
               }
            });
         }
      }
      OseeAts.addButtonToEditorToolBar(this, TestManagerPlugin.getInstance(), composite, TestManagerEditor.namespace,
      "Test Manager");

   }

   public String getActionDescription() {
      return "";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#areSettingsValidForRun()
    */
   @Override
   public boolean areSettingsValidForRun() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#restoreData()
    */
   @Override
   public void restoreData() {
      // Do Nothing
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#saveData()
    */
   @Override
   public void saveData() {
      // Do Nothing
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#getErrorMessage()
    */
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

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.ui.test.manager.pages.TestManagerPage#onConnectionLost(org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment)
	 */
	@Override
	public boolean onConnectionLost(IHostTestEnvironment testHost) {
		return false;
	}

}
