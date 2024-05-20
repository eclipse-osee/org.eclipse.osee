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

package org.eclipse.osee.framework.ui.skynet.export;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.equinox.bidi.StructuredTextTypeHandlerFactory;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.util.BidiUtils;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan D. Brooks
 * @author Loren K. Ashley
 */

public class ArtifactExportPage extends WizardDataTransferPage {

   private Button browseButton;

   private Button cancelOnErrorCheckBox;

   private Button disableErrorPopUpsCheckBox;

   private String errorDialogTitle;

   private Combo pathCombo;

   private final IStructuredSelection selection;

   private boolean errorPopUpsWereDisabled;

   private boolean errorPopUpsWereAllowed;

   public ArtifactExportPage(String name, IStructuredSelection selection) {

      super(name);

      this.setTitle(ArtifactExportConstants.WIZARD_PAGE_1_TITLE);
      this.setDescription(ArtifactExportConstants.WIZARD_PAGE_1_DESCRIPTION);

      this.selection = selection;
      this.errorDialogTitle = ArtifactExportConstants.WIZARD_PAGE_1_ERROR_DIALOG_TITLE_DEFAULT;
      this.errorPopUpsWereDisabled = false;
      this.errorPopUpsWereAllowed = true;
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   @Override
   public void createControl(Composite parent) {

      this.initializeDialogUnits(parent);

      final var composite = new Composite(parent, SWT.NONE);

      final var layout = new GridLayout();
      layout.numColumns = 2;

      final var gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL, GridData.GRAB_HORIZONTAL);
      gridData.widthHint = SIZING_TEXT_FIELD_WIDTH;

      final var font = parent.getFont();

      composite.setLayout(layout);
      composite.setLayoutData(gridData);
      composite.setFont(font);

      final var pathComboLabelGridData = new GridData(GridData.GRAB_HORIZONTAL);
      pathComboLabelGridData.horizontalSpan = 2;

      final var pathComboLabel = new Label(composite, SWT.NONE);
      pathComboLabel.setLayoutData(pathComboLabelGridData);
      pathComboLabel.setText(ArtifactExportConstants.WIZARD_PAGE_1_PATH_SELECTION_LABEL);
      pathComboLabel.setFont(font);

      final var defaultExportPath = ArtifactExportConstants.WIZARD_PAGE_1_PATH_SELECTION_DEFAULT_PATH;

      this.pathCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER);
      this.pathCombo.addListener(SWT.Modify, this);
      this.pathCombo.addListener(SWT.Selection, this);

      final var pathComboGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
      pathComboGridData.widthHint = SIZING_TEXT_FIELD_WIDTH;

      pathCombo.setLayoutData(pathComboGridData);
      pathCombo.setFont(font);
      BidiUtils.applyBidiProcessing(pathCombo, StructuredTextTypeHandlerFactory.FILE);
      pathCombo.setText(defaultExportPath);

      this.browseButton = new Button(composite, SWT.PUSH);
      this.browseButton.setText(ArtifactExportConstants.WIZARD_PAGE_1_BROWSE_BUTTON_TEXT);
      this.browseButton.addListener(SWT.Selection, this);
      this.browseButton.setFont(font);

      this.setButtonLayoutData(this.browseButton);

      this.createOptionsGroup(composite);

      this.restoreWidgetValues();

      this.setPageComplete(this.determinePageCompletion());

      this.setControl(composite);
   }

   @Override
   protected void createOptionsGroupButtons(Group optionsGroup) {

      final var font = optionsGroup.getFont();

      this.cancelOnErrorCheckBox = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
      this.cancelOnErrorCheckBox.setText("Cancel on error.");
      this.cancelOnErrorCheckBox.setFont(font);

      this.disableErrorPopUpsCheckBox = new Button(optionsGroup, SWT.CHECK | SWT.LEFT);
      this.disableErrorPopUpsCheckBox.setText("Disable error popups.");
      this.disableErrorPopUpsCheckBox.setFont(font);

   }

   protected boolean executeExportOperation(ArtifactExportOperation artifactExportOperation) {

      var result = true;

      try {

         if (this.disableErrorPopUpsCheckBox.getSelection() == true) {
            this.errorPopUpsWereDisabled = true;
            this.errorPopUpsWereAllowed = RenderingUtil.arePopupsAllowed();
            RenderingUtil.setPopupsAllowed(false);
         }

         this.getContainer().run(true, true, artifactExportOperation);

      } catch (InterruptedException e) {

         result = false;

      } catch (Exception e) {

         this.displayErrorDialog(e.getCause());

         result = false;

      } finally {

         final var status = artifactExportOperation.getStatus();

         if (!status.isOK()) {

            //@formatter:off
            ErrorDialog.openError
               (
                  this.getContainer().getShell(),
                  ArtifactExportConstants.WIZARD_EXPORT_ERROR_TITLE,
                  ArtifactExportConstants.WIZARD_EXPORT_ERROR_SHORT_MESSAGE,
                  status
               );
            //@formatter:on

            result = false;
         }

      }

      return result;
   }

   public boolean cancel() {

      this.restoreErrorPopUps();

      return true;
   }

   public boolean finish() {

      this.saveDirtyEditors();
      this.saveWidgetValues();

      final var exportPath = this.getExportPath();
      final var cancelOnError = this.getCancelOnError();
      final var exportArtifacts = this.getExportArtifacts();
      final var artifactExportOperation = new ArtifactExportOperation(exportPath, cancelOnError, exportArtifacts);
      final var result = this.executeExportOperation(artifactExportOperation);

      this.restoreErrorPopUps();

      return result;
   }

   public boolean getCancelOnError() {
      return this.cancelOnErrorCheckBox.getSelection();
   }

   @Override
   protected String getErrorDialogTitle() {
      return this.errorDialogTitle;
   }

   public List<Artifact> getExportArtifacts() {

      if (selection == null) {
         return List.of();
      }

      final var selectedArtifacts = new LinkedList<Artifact>();
      final var invalidSelections = new LinkedList<String>();

      Iterator<?> selectionIterator = selection.iterator();
      while (selectionIterator.hasNext()) {

         final var selectedObject = selectionIterator.next();
         Object selection = null;

         if (selectedObject instanceof Match) {
            selection = ((Match) selectedObject).getElement();
         } else if (selectedObject instanceof IAdaptable) {
            selection = ((IAdaptable) selectedObject).getAdapter(Artifact.class);
         }

         if (selection instanceof Artifact) {
            final var artifactSelection = (Artifact) selection;
            selectedArtifacts.add(artifactSelection);
         } else {
            invalidSelections.add(selectedObject.toString());
         }
      }

      if (!invalidSelections.isEmpty()) {
         //@formatter:off
         this.errorDialogTitle = ArtifactExportConstants.WIZARD_PAGE_1_ERROR_DIALOG_TITLE_INVALID_SELECTION;
         this.displayErrorDialog
            (
               new Message()
                      .title( ArtifactExportConstants.WIZARD_PAGE_1_INVALID_SELECTION_ERROR_TITLE )
                      .indentInc()
                      .segmentIndexed( ArtifactExportConstants.WIZARD_PAGE_1_INVALID_SELECTION_ERROR_SEGMENT_TITLE, invalidSelections )
                      .toString()
            );
         //@formatter:on

         return List.of();
      }

      return selectedArtifacts;
   }

   public Path getExportPath() {

      final var exportPathString = this.pathCombo.getText().trim();
      final var exportPathFile = Path.of(exportPathString);
      return exportPathFile;
   }

   protected void handleDestinationBrowseButtonPressed() {

      final var dialog = new DirectoryDialog(getContainer().getShell(), SWT.SAVE | SWT.SHEET);

      dialog.setText(ArtifactExportConstants.WIZARD_PAGE_1_BROWSE_TITLE);

      final var browsePath = this.pathCombo.getText().trim();

      dialog.setFilterPath(browsePath);

      final var selectedDirectoryName = dialog.open();

      if (selectedDirectoryName != null) {
         this.setErrorMessage(null);
         this.pathCombo.setText(selectedDirectoryName);
      }
   }

   @Override
   public void handleEvent(Event event) {

      final var source = event.widget;

      if (source == this.browseButton) {
         this.handleDestinationBrowseButtonPressed();
      }

      this.updatePageCompletion();
   }

   private void restoreErrorPopUps() {

      if (this.errorPopUpsWereDisabled && this.errorPopUpsWereAllowed) {
         RenderingUtil.setPopupsAllowed(true);
      }
   }

   @Override
   protected void restoreWidgetValues() {

      final var settings = this.getDialogSettings();

      if (settings == null) {

         return;
      }

      final var directoryNames = settings.getArray(ArtifactExportConstants.WIZARD_PAGE_1_STORE_DESTINATION_NAMES_ID);

      if (directoryNames != null) {

         this.pathCombo.setText(directoryNames[0]);

         for (String directoryName : directoryNames) {
            this.pathCombo.add(directoryName);
         }

      }

      final var cancelOnErrorCheckBoxSelectionText =
         settings.get(ArtifactExportConstants.WIZARD_PAGE_1_STORE_CACNEL_ON_ERROR_ID);

      if (cancelOnErrorCheckBoxSelectionText != null) {

         final var cancelOnErrorCheckBoxSelection = Boolean.parseBoolean(cancelOnErrorCheckBoxSelectionText);
         this.cancelOnErrorCheckBox.setSelection(cancelOnErrorCheckBoxSelection);

      } else {

         this.cancelOnErrorCheckBox.setSelection(ArtifactExportConstants.WIZARD_PAGE_1_CANCEL_ON_ERROR_DEFAULT);
      }

      final var disableErrorPopUpsCheckBoxSelectionText =
         settings.get(ArtifactExportConstants.WIZARD_PAGE_1_STORE_DISABLE_ERROR_POP_UPS_ID);

      if (disableErrorPopUpsCheckBoxSelectionText != null) {

         final var disableErrorPopUpsCheckBoxSelection = Boolean.parseBoolean(disableErrorPopUpsCheckBoxSelectionText);
         this.disableErrorPopUpsCheckBox.setSelection(disableErrorPopUpsCheckBoxSelection);

      } else {

         this.disableErrorPopUpsCheckBox.setSelection(
            ArtifactExportConstants.WIZARD_PAGE_1_DISABLE_ERROR_POP_UPS_DEFAULT);
      }
   }

   protected boolean saveDirtyEditors() {
      return PlatformUI.getWorkbench().saveAllEditors(true);
   }

   @Override
   protected void saveWidgetValues() {

      final var settings = this.getDialogSettings();

      if (settings == null) {

         return;
      }

      var directoryNames = settings.getArray(ArtifactExportConstants.WIZARD_PAGE_1_STORE_DESTINATION_NAMES_ID);

      if (directoryNames == null) {
         directoryNames = new String[0];
      }

      directoryNames = this.addToHistory(directoryNames, this.pathCombo.getText().trim());
      settings.put(ArtifactExportConstants.WIZARD_PAGE_1_STORE_DESTINATION_NAMES_ID, directoryNames);

      final var cancelOnErrorCheckBoxSelection = this.cancelOnErrorCheckBox.getSelection();
      settings.put(ArtifactExportConstants.WIZARD_PAGE_1_STORE_CACNEL_ON_ERROR_ID, cancelOnErrorCheckBoxSelection);

      final var disableErrorPopUpsCheckBoxSelection = this.disableErrorPopUpsCheckBox.getSelection();
      settings.put(ArtifactExportConstants.WIZARD_PAGE_1_STORE_DISABLE_ERROR_POP_UPS_ID,
         disableErrorPopUpsCheckBoxSelection);

   }
}