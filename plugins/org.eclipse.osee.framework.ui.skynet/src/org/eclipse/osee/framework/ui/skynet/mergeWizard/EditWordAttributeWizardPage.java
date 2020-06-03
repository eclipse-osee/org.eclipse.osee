/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.mergeWizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.XMergeLabelProvider;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
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
public class EditWordAttributeWizardPage extends WizardPage {

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
   private Label imageLabel, resultLabel;

   private final Listener listener = new Listener() {
      @Override
      public void handleEvent(Event event) {

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

   public EditWordAttributeWizardPage(AttributeConflict conflict) {
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
      parent.setLayout(new GridLayout());
      setTitle("Edit the attribute ");

      Composite mainComp = new Composite(parent, SWT.NONE);
      mainComp.setLayout(new GridLayout(1, false));
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      EditAttributeWizardPage.createChangeTypeLabels(mainComp, conflict, changeType);
      createResultsWidgets(mainComp);

      createSourceDestButtons(mainComp);
      createEditButtons(mainComp);
      createMergeButton(mainComp);
      createDiffsButtons(mainComp);

      try {
         setResolution(conflict);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

      setControl(mainComp);
      getShell().setMinimumSize(100, 500);
   }

   private void createDiffsButtons(Composite mainComp) {
      Composite diffsComp = new Composite(mainComp, SWT.NONE);
      diffsComp.setLayout(new GridLayout(3, false));
      diffsComp.setLayoutData(new GridData());

      sourceDiffButton = createButton(diffsComp, null, "Show Source Diff",
         "Show the differences between the current Source artifact and the artifact at the time the Source Branch was created");
      destDiffButton = createButton(diffsComp, null, "Show Destination Diff",
         "Show the differences between the current Destination" + " artifact and the artifact at the time the Source Branch was created");
      sourceDestDiffButton = createButton(diffsComp, null, "Show Source/Destination Diff",
         "Show the differences between the current Source" + " artifact and the current Destination artifact");
      sourceMergeDiffButton = createButton(diffsComp, null, "Show Source/Merge Diff",
         "Show the differences between the current Source" + " artifact and the current Merge artifact");
      destMergeDiffButton = createButton(diffsComp, null, "Show Destination/Merge Diff",
         "Show the differences between the current Destination" + " artifact and the current Merge artifact");
      if (MergeUtility.getStartArtifact(conflict) == null) {
         sourceDiffButton.setEnabled(false);
         destDiffButton.setEnabled(false);
      }
      diffsComp.layout();
   }

   private void createResultsWidgets(Composite mainComp) {
      Composite resultsComp = new Composite(mainComp, SWT.NONE);
      resultsComp.setLayout(ALayout.getZeroMarginLayout(3, false));
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

      new Label(resultsComp, SWT.NONE).setText("Result: ");

      imageLabel = new Label(resultsComp, SWT.NONE);
      imageLabel.setImage(null);

      resultLabel = new Label(resultsComp, SWT.NONE);
   }

   private void createEditButtons(Composite mainComp) {
      Composite editComp = new Composite(mainComp, SWT.NONE);
      editComp.setLayout(new GridLayout(2, false));
      editComp.setLayoutData(new GridData());

      editButton = createButton(editComp, ImageManager.getImage(FrameworkImage.MERGE_MERGED), "Edit Merged Value",
         "Make additional changes using the Document/Merge Editor");
      clearButton = createButton(editComp, ImageManager.getImage(FrameworkImage.MERGE_START), "Clear Merged Value",
         "Reinitializes the merge for this Document");
   }

   private void createMergeButton(Composite mainComp) {
      Composite mergeComp = new Composite(mainComp, SWT.NONE);
      mergeComp.setLayout(new GridLayout(1, false));
      mergeComp.setLayoutData(new GridData());

      mergeButton = createButton(mergeComp, ImageManager.getImage(FrameworkImage.MERGE_MERGED),
         "Generate Three Way Merge", "Use the new inline merging");
   }

   private Composite createSourceDestButtons(Composite mainComp) {
      Composite sourceDestComp = new Composite(mainComp, SWT.NONE);
      sourceDestComp.setLayout(new GridLayout(2, false));
      sourceDestComp.setLayoutData(new GridData());

      sourceButton = createButton(sourceDestComp, ImageManager.getImage(FrameworkImage.MERGE_SOURCE),
         "Use Source Value", "Initialize the Document with Source Values");
      destButton = createButton(sourceDestComp, ImageManager.getImage(FrameworkImage.MERGE_DEST),
         "Use Destination Value", "Initialize the Document with Destination Values");
      return sourceDestComp;
   }

   private Button createButton(Composite composite, Image image, String text, String tooltip) {
      Button button = new Button(composite, SWT.PUSH);
      button.addListener(SWT.Selection, listener);
      button.setText(text);
      if (image != null) {
         button.setImage(image);
      }
      button.setToolTipText(tooltip);
      return button;
   }

   public boolean canFinish() {
      return true;
   }

   public boolean closingPage() {
      return true;
   }

   public void setResolution(AttributeConflict conflict) {
      imageLabel.setImage(XMergeLabelProvider.getMergeImage(conflict));
      String mergedText = "Unknown";
      if (conflict.getStatus().isUntouched()) {
         mergedText = "Un-Set";
      } else if (conflict.mergeEqualsDestination()) {
         mergedText = "Destination Value";
      } else if (conflict.mergeEqualsSource()) {
         mergedText = "Source Value";
      } else {
         mergedText = "Merged";
      }
      resultLabel.setText(mergedText);
      resultLabel.getParent().layout();
   }

}
