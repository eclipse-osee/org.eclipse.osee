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
package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import java.util.EnumMap;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetGuiDebug;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Theron Virgin
 */
public class ConflictResolutionWizardPage extends WizardPage {

   private AttributeConflict conflict;
   private String changeType = "";
   private static final int NUM_COLUMNS = 1;

   public enum BUTTON_NAMES {
      sourceButton, destButton, mergeEditButton
   };

   private final Listener listener = new Listener() {
      public void handleEvent(Event event) {
         // ...
         getWizard().getContainer().updateButtons();
      }
   };
   public static final String TITLE = "How would you like to resolve this conflict?";
   public static final String INDENT = "     ";
   public static final String SOURCE_TITLE = "Source Value:";
   public static final String DEST_TITLE = "Destination value:";
   public static final String ART_TEXT = "Artifact: ";
   public static final String TYPE_TEXT = "Attribute type: ";
   private static final String[] EDIT_TEXT =
         {"Use the Source Branch Attribute Value", "Use the Destination Branch Attribute Value",
               "Merge the Attribute Content between the two Branchs"};
   private static final String[] EDIT_TOOLTIP =
         {"Use the Source Branch Version with no changes", "Use the Destination Branch Version with no changes",
               "Merge the two versions using the appropriate editor"};

   private EnumMap<BUTTON_NAMES, Button> buttons = new EnumMap<BUTTON_NAMES, Button>(BUTTON_NAMES.class);

   /**
    * @param pageName
    */

   public ConflictResolutionWizardPage(AttributeConflict conflict) {
      super("Resolve the conflict");
      try {
         if (conflict != null) {
            this.conflict = conflict;
            changeType = conflict.getDynamicAttributeDescriptor().getName();
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createControl(Composite parent) {
      setTitle(TITLE);

      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gl = new GridLayout();
      gl.numColumns = NUM_COLUMNS;
      composite.setLayout(gl);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      composite.setLayoutData(gd);
      gd.horizontalSpan = NUM_COLUMNS;

      createHeader(composite);

      createEditButtons(composite, gd);

      setControl(composite);

   }

   private void createHeader(Composite composite) {
      try {
         new Label(composite, SWT.NONE).setText(ART_TEXT);
         new Label(composite, SWT.NONE).setText(INDENT + conflict.getArtifactName());

         new Label(composite, SWT.NONE).setText(TYPE_TEXT);
         new Label(composite, SWT.NONE).setText(INDENT + changeType);

      } catch (Exception ex) {
         OSEELog.logException(ConflictResolutionWizard.class, ex, true);
      }

   }

   private void createEditButtons(Composite composite, GridData gd) {

      if (!conflict.isWordAttribute()) {
         new Label(composite, SWT.NONE).setText(SOURCE_TITLE);
         new Label(composite, SWT.NONE).setText(INDENT + conflict.getSourceDisplayData());
         new Label(composite, SWT.NONE).setText(DEST_TITLE);
         new Label(composite, SWT.NONE).setText(INDENT + conflict.getDestDisplayData());
      }

      new Label(composite, SWT.NONE);

      createButtons(composite, gd);

      int x = 0;
      for (BUTTON_NAMES button : BUTTON_NAMES.values()) {
         Button tempButton = buttons.get(button);
         tempButton.setToolTipText(EDIT_TOOLTIP[x]);
         tempButton.setText(EDIT_TEXT[x++]);
      }
   }

   private void createButtons(Composite composite, GridData gd) {
      buttons.put(BUTTON_NAMES.sourceButton, new Button(composite, SWT.RADIO));
      buttons.put(BUTTON_NAMES.destButton, new Button(composite, SWT.RADIO));
      buttons.put(BUTTON_NAMES.mergeEditButton, new Button(composite, SWT.RADIO));

      for (BUTTON_NAMES button : BUTTON_NAMES.values()) {
         Button tempButton = buttons.get(button);
         tempButton.setLayoutData(gd);
         tempButton.setSelection(false);
         tempButton.addListener(SWT.Selection, listener);
      }
      buttons.get(BUTTON_NAMES.mergeEditButton).setSelection(true);
   }

   @Override
   public boolean canFlipToNextPage() {
      if (buttons.get(BUTTON_NAMES.mergeEditButton).getSelection()) {
         return true;
      }
      return false;
   }

   public boolean canFinish() {
      if (buttons.get(BUTTON_NAMES.sourceButton).getSelection() || buttons.get(BUTTON_NAMES.destButton).getSelection()) {
         return true;
      }
      return false;
   }

   public boolean closingPage() {
      try {
         if (buttons.get(BUTTON_NAMES.sourceButton).getSelection()) {
            MergeUtility.setToSource(conflict, getShell(), true);

         } else if (buttons.get(BUTTON_NAMES.destButton).getSelection()) {
            MergeUtility.setToDest(conflict, getShell(), true);
         }
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiDebug.class, ex, true);
      }
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.wizard.WizardPage#getNextPage()
    */
   @Override
   public IWizardPage getNextPage() {
      if (conflict.isWordAttribute())
         return getWizard().getPage(EditWFCAttributeWizardPage.TITLE);
      else
         return getWizard().getPage(EditAttributeWizardPage.TITLE);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.dialogs.DialogPage#setVisible(boolean)
    */
   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      if (visible == true) {
         if (conflict.isWordAttribute()) {
            Job job = new Job("") {
               public IStatus run(IProgressMonitor mon) {
                  try {
                     MergeUtility.showSourceCompareFile(conflict);
                     MergeUtility.showDestCompareFile(conflict);
                  } catch (Exception ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, ex, true);
                  }
                  return Status.OK_STATUS;
               }
            };
            Jobs.startJob(job);
         }
      }
   }
}
