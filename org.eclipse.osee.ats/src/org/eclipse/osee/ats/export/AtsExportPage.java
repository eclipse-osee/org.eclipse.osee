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

package org.eclipse.osee.ats.export;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.ats.export.AtsExportManager.ExportOption;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XCheckBox;
import org.eclipse.osee.framework.ui.skynet.widgets.XFileTextWithSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XFileTextWithSelectionDialog.Type;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Donald G. Dunne
 */
public class AtsExportPage extends WizardDataTransferPage {
   MouseMoveListener listener;
   Label errorLabel;
   List<XCheckBox> checkBoxes = new ArrayList<XCheckBox>();
   XFileTextWithSelectionDialog xFileSel;
   String fileLocation;
   Collection<ExportOption> selectedExportOptions = new ArrayList<ExportOption>();
   private final Collection<? extends Artifact> artifacts;

   public AtsExportPage(IStructuredSelection selection) throws OseeCoreException {
      super("Main");
      this.artifacts = AtsExportManager.getSmaArts(selection);
   }

   public AtsExportPage(Collection<? extends Artifact> artifacts) {
      super("Main");
      this.artifacts = artifacts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.dialogs.WizardDataTransferPage#allowNewContainerName()
    */
   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
    */
   @Override
   public void handleEvent(Event event) {

   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
    */
   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);
      setTitle("Export ATS Artifacts");
      setMessage("Select export options and export location.");
      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
      composite.setFont(parent.getFont());

      Label label = new Label(composite, SWT.NONE);
      if (artifacts.size() == 0) {
         label.setText("Error: No ATS Artifacts input.  Close wizard and re-perform selection.");
         label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
      } else {
         label.setText("Selected " + artifacts.size() + " ATS Artifact to export.");
      }
      GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
      gridData.horizontalSpan = 2;
      label.setLayoutData(gridData);

      XModifiedListener modifyListener = new XModifiedListener() {
         /* (non-Javadoc)
          * @see org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener#widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget)
          */
         @Override
         public void widgetModified(XWidget widget) {
            handleModified();
         }
      };

      List<ExportOption> validExportOptions = new ArrayList<ExportOption>();
      validExportOptions.addAll(Arrays.asList(ExportOption.values()));
      validExportOptions.remove(ExportOption.POPUP_DIALOG);
      validExportOptions.remove(ExportOption.NONE);

      for (ExportOption exportOption : validExportOptions) {
         XCheckBox checkBox = new XCheckBox(exportOption.name());
         checkBox.setLabelAfter(true);
         checkBox.createWidgets(composite, 2);
         if (selectedExportOptions.contains(exportOption)) {
            checkBox.set(true);
         }
         checkBox.addXModifiedListener(modifyListener);
         checkBoxes.add(checkBox);
      }

      xFileSel = new XFileTextWithSelectionDialog("Export Location", Type.Directory);
      xFileSel.createWidgets(composite, 2);
      xFileSel.addXModifiedListener(modifyListener);

      setPageComplete(determinePageCompletion());
      setControl(composite);
   }

   public Result isEntryValid() {
      if (artifacts.size() == 0) {
         return new Result("No Artifacts selected.  Cancel wizard and try again.");
      }
      if (!selectedExportOptions.contains(ExportOption.AS_HTML_TO_FILE) && !selectedExportOptions.contains(ExportOption.AS_HTML_TO_RESULT_EDITOR) && !selectedExportOptions.contains(ExportOption.AS_PDF)) {
         return new Result("Must select at least one export AS_ option.");
      }
      if (!selectedExportOptions.contains(ExportOption.MERGE_INTO_SINGLE_FILE) && !selectedExportOptions.contains(ExportOption.SAVE_INTO_SEPARATE_FILES)) {
         return new Result("Must select \"MERGE_INTO_SINGLE_FILE\" or \"SAVE_INTO_SEPARATE_FILES\"");
      }
      if (selectedExportOptions.contains(ExportOption.AS_HTML_TO_FILE) || selectedExportOptions.contains(ExportOption.AS_PDF)) {
         if (xFileSel.get().equals("")) {
            return new Result(
                  "Must select \"Export Location\" for \"" + ExportOption.AS_HTML_TO_FILE + "\" or \"" + ExportOption.AS_PDF + "\" options.");
         }
         if (!(new File(xFileSel.get())).isDirectory()) {
            return new Result("Invalid Directory");
         }
      }
      return Result.TrueResult;
   }

   public void handleModified() {
      for (XCheckBox checkBox : checkBoxes) {
         ExportOption exportOption = ExportOption.valueOf(checkBox.getLabel());
         if (checkBox.isSelected()) {
            selectedExportOptions.add(exportOption);
         } else {
            selectedExportOptions.remove(exportOption);
         }
      }
      fileLocation = xFileSel.get();
   }

   /**
    * @return the selectedExportOptions
    */
   public Collection<ExportOption> getSelectedExportOptions() {
      return selectedExportOptions;
   }

   /**
    * @return the fileLocation
    */
   public String getFileLocation() {
      return fileLocation;
   }

}