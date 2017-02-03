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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeLabelProvider;
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
public class EditWFCAttributeWizardPage extends WizardPage {

   public static final String TITLE = "WFC Editor Page";
   private AttributeConflict conflict;
   private String changeType = "";
   private Button editButton;
   private Button mergeButton;
   private Button clearButton;
   private Button sourceButton;
   private Button destButton;
   private Button sourceDiffButton;
   private Button destDiffButton;
   private Button sourceDestDiffButton;
   private Button sourceMergeDiffButton;
   private Button destMergeDiffButton;
   private Label imageLabel;
   private static final String EDIT_TEXT = "Edit Merge Artifact";
   private static final String EDIT_TOOLTIP = "Make additional changes using the Document/Merge Editor";
   private static final String MERGE_TEXT = "Generate Three Way Merge";
   private static final String MERGE_TOOLTIP = "Use the new inline merging";
   private static final String CLEAR_TEXT = "Clear the Merge Artifact";
   private static final String CLEAR_TOOLTIP = "Reinitializes the merge for this Document";
   private static final String SOURCE_TEXT = "Populate with Source Data";
   private static final String SOURCE_TOOLTIP = "Initialize the Document with Source Values";
   private static final String DEST_TEXT = "Populate with Destination Data";
   private static final String DEST_TOOLTIP = "Initialize the Document with Destination Values";
   private static final String SDIFF_TEXT = "Show Source Diff";
   private static final String SDIFF_TOOLTIP =
      "Show the differences between the current Source" + " artifact and the artifact at the time the Source Branch was created";
   private static final String DDIFF_TEXT = "Show Destination Diff";
   private static final String DDIFF_TOOLTIP =
      "Show the differences between the current Destination" + " artifact and the artifact at the time the Source Branch was created";
   private static final String SDDIFF_TEXT = "Show Source/Destination Diff";
   private static final String SDDIFF_TOOLTIP =
      "Show the differences between the current Source" + " artifact and the current Merge artifact";
   private static final String SMDIFF_TEXT = "Show Source/Merge Diff";
   private static final String SMDIFF_TOOLTIP =
      "Show the differences between the current Destination" + " artifact and the current Merge artifact";
   private static final String DMDIFF_TEXT = "Show Destination/Merge Diff";
   private static final String DMDIFF_TOOLTIP =
      "Show the differences between the current Destination" + " artifact and the current Source artifact";
   private static final int NUM_COLUMNS = 1;

   private final Listener listener = new Listener() {
      @Override
      public void handleEvent(Event event) {
         // ...

         try {
            if (event.widget == editButton) {
               RendererManager.openInJob(conflict.getArtifact(), PresentationType.SPECIALIZED_EDIT);
               conflict.markStatusToReflectEdit();
            } else if (event.widget == clearButton) {
               MergeUtility.clearValue(conflict, getShell(), true);
            } else if (event.widget == sourceButton) {
               MergeUtility.setToSource(conflict, getShell(), true);
            } else if (event.widget == destButton) {
               MergeUtility.setToDest(conflict, getShell(), true);
            } else if (event.widget == sourceDiffButton) {
               MergeUtility.showCompareFile(MergeUtility.getStartArtifact(conflict), conflict.getSourceArtifact(),
                  "Source_Diff_For");
            } else if (event.widget == destDiffButton) {
               MergeUtility.showCompareFile(MergeUtility.getStartArtifact(conflict), conflict.getDestArtifact(),
                  "Destination_Diff_For");
            } else if (event.widget == sourceDestDiffButton) {
               MergeUtility.showCompareFile(conflict.getSourceArtifact(), conflict.getDestArtifact(),
                  "Source_Destination_Diff_For");
            } else if (event.widget == sourceMergeDiffButton) {
               if (conflict.wordMarkupPresent()) {
                  throw new OseeCoreException(AttributeConflict.DIFF_MERGE_MARKUP);
               }
               MergeUtility.showCompareFile(conflict.getSourceArtifact(), conflict.getArtifact(),
                  "Source_Merge_Diff_For");
            } else if (event.widget == destMergeDiffButton) {
               if (conflict.wordMarkupPresent()) {
                  throw new OseeCoreException(AttributeConflict.DIFF_MERGE_MARKUP);
               }
               MergeUtility.showCompareFile(conflict.getDestArtifact(), conflict.getArtifact(),
                  "Destination_Merge_Diff_For");
            } else if (event.widget == mergeButton) {
               MergeUtility.launchMerge(conflict, getShell());
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
         getWizard().getContainer().updateButtons();
      }
   };

   public EditWFCAttributeWizardPage(AttributeConflict conflict) {
      super(TITLE);
      try {
         if (conflict != null) {
            this.conflict = conflict;
            changeType = conflict.getChangeItem();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public void createControl(Composite parent) {
      setTitle("Edit the attribute ");

      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gl = new GridLayout();
      gl.numColumns = NUM_COLUMNS;
      composite.setLayout(gl);
      GridData gd = new GridData(SWT.BEGINNING);
      composite.setLayoutData(gd);
      gd.horizontalSpan = NUM_COLUMNS;

      imageLabel = new Label(composite, SWT.NONE);
      imageLabel.setImage(null);

      try {
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.ART_TEXT);
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.INDENT + conflict.getArtifactName());
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.TYPE_TEXT);
         new Label(composite, SWT.NONE).setText(ConflictResolutionWizard.INDENT + changeType);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      new Label(composite, SWT.NONE);

      editButton = createButton(EDIT_TEXT, EDIT_TOOLTIP, composite);
      mergeButton = createButton(MERGE_TEXT, MERGE_TOOLTIP, composite);
      new Label(composite, SWT.NONE);
      sourceButton = createButton(SOURCE_TEXT, SOURCE_TOOLTIP, composite);
      destButton = createButton(DEST_TEXT, DEST_TOOLTIP, composite);
      clearButton = createButton(CLEAR_TEXT, CLEAR_TOOLTIP, composite);
      new Label(composite, SWT.NONE);

      Composite buttonComp = new Composite(composite, SWT.NONE);
      GridLayout glay = new GridLayout();
      glay.numColumns = 3;
      buttonComp.setLayout(glay);
      GridData gdata = new GridData(SWT.FILL);
      buttonComp.setLayoutData(gdata);
      gdata.horizontalSpan = 1;

      sourceDiffButton = createButton(SDIFF_TEXT, SDIFF_TOOLTIP, buttonComp);
      destDiffButton = createButton(DDIFF_TEXT, DDIFF_TOOLTIP, buttonComp);
      sourceDestDiffButton = createButton(SDDIFF_TEXT, SDDIFF_TOOLTIP, buttonComp);
      sourceMergeDiffButton = createButton(SMDIFF_TEXT, SMDIFF_TOOLTIP, buttonComp);
      destMergeDiffButton = createButton(DMDIFF_TEXT, DMDIFF_TOOLTIP, buttonComp);
      if (MergeUtility.getStartArtifact(conflict) == null) {
         sourceDiffButton.setEnabled(false);
         destDiffButton.setEnabled(false);
      }

      try {
         setResolution(XMergeLabelProvider.getMergeImage(conflict));
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

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
      return true;
   }

   public boolean closingPage() {
      return true;
   }

   public void setResolution(Image image) {
      imageLabel.setImage(image);
   }

}
