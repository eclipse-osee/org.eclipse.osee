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

import java.util.Arrays;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeUtility;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
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
public class EditAttributeWizardPage extends WizardPage {

   public static final String TITLE = "Editor Page";
   private AttributeConflict conflict;
   private String changeType = "";
   private Button sourceButton;
   private Button destButton;
   private Button clearButton;
   private Label imageLabel;
   private static final String SOURCE_BUTTON_TEXT = "Load Source Data";
   private static final String SOURCE_TEXT = ConflictResolutionWizard.SOURCE_TITLE;
   private static final String SOURCE_TOOLTIP = "Load the Editor with the Source Branch Attribute Value";
   private static final String DEST_BUTTON_TEXT = "Load Destination Data";
   private static final String DEST_TEXT = ConflictResolutionWizard.DEST_TITLE;
   private static final String DEST_TOOLTIP = "Load the Editor with the Destination Branch Attribute Value";
   private static final int NUM_COLUMNS = 1;
   private IEmbeddedAttributeEditor editor;

   private final Listener listener = new Listener() {
      @Override
      public void handleEvent(Event event) {
         // ...
         try {
            if (conflict.getStatus().isOverwriteAllowed()) {
               if (event.widget == sourceButton) {
                  if (conflict.getSourceObject() != null) {
                     editor.update(conflict.getSourceObject());
                  }
               }
               if (event.widget == destButton) {
                  if (conflict.getDestObject() != null) {
                     editor.update(conflict.getDestObject());
                  }
               }
               if (event.widget == clearButton) {
                  editor.update("");
               }
            } else {
               MessageDialog.openInformation(getShell(), "Attention", MergeUtility.COMMITED_PROMPT);
            }

         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
         getWizard().getContainer().updateButtons();
      }
   };

   public EditAttributeWizardPage(AttributeConflict conflict) {
      super(TITLE);
      try {
         this.conflict = conflict;
         changeType = conflict.getChangeItem();
         if (!conflict.isWordAttribute()) {
            editor = EmbededAttributeEditorFactory.getEmbeddedEditor(conflict.getAttributeType(),
               conflict.getSourceDisplayData(), Arrays.asList(conflict), true);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void createControl(Composite parent) {
      setTitle("Edit the attribute ");
      Composite composite = new Composite(parent, SWT.NONE);

      if (editor == null) {
         setControl(composite);
         return;
      }

      GridLayout gl = new GridLayout();
      gl.numColumns = NUM_COLUMNS;
      composite.setLayout(gl);
      GridData gd = new GridData(SWT.BEGINNING);
      composite.setLayoutData(gd);
      gd.horizontalSpan = NUM_COLUMNS;

      imageLabel = new Label(composite, SWT.NONE);
      imageLabel.setText("hello there");
      imageLabel.setImage(null);

      try {
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.ART_TEXT);
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.INDENT + conflict.getArtifactName());
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.TYPE_TEXT);
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.INDENT + changeType);

         new Label(composite, SWT.NONE).setText("");

         new Label(composite, SWT.NONE).setText(SOURCE_TEXT);
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.INDENT + conflict.getSourceDisplayData());
         // sourceButton =
         // createButton(conflict.getSourceDisplayData(),SOURCE_TOOLTIP,composite,gd);
         new Label(composite, SWT.NONE).setText(DEST_TEXT);
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.INDENT + conflict.getDestDisplayData());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      Composite buttonComp = new Composite(composite, SWT.NONE);
      GridLayout glay = new GridLayout();
      glay.numColumns = 3;
      buttonComp.setLayout(glay);
      GridData gdata = new GridData(SWT.FILL);
      buttonComp.setLayoutData(gdata);
      gdata.horizontalSpan = 1;

      sourceButton = createButton(SOURCE_BUTTON_TEXT, SOURCE_TOOLTIP, buttonComp);
      destButton = createButton(DEST_BUTTON_TEXT, DEST_TOOLTIP, buttonComp);
      // clearButton = createButton(CLEAR_BUTTON_TEXT, CLEAR_TOOLTIP,
      // buttonComp, gdata);

      editor.create(composite, gd);

      setControl(composite);
   }

   private Button createButton(String text, String tooltip, Composite composite) {
      Button button = new Button(composite, SWT.PUSH);
      button.addListener(SWT.Selection, listener);
      button.setText(text);
      button.setToolTipText(tooltip);
      return button;
   }

   public boolean canFinish() {
      return editor.canFinish();
   }

   public boolean closingPage() {
      return editor.commit();
   }

   public void setResolution(Image image) {
      imageLabel.setImage(image);
   }

}
