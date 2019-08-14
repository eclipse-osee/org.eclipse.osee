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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactExtractorContributionManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractorDelegate;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactExtractorSelectPanel;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactSelectPanel;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactTypeSelectPanel;
import org.eclipse.osee.framework.ui.skynet.panels.AttributeTypeSelectPanel;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan C. Schmitt
 * @author Roberto E. Escobar
 */
public class ArtifactImportPage extends WizardDataTransferPage {

   private static final String PAGE_NAME = "osee.define.wizardPage.artifactImportPage";

   private DirectoryOrFileSelector directoryFileSelector;
   private final File defaultSourceFile;
   private Button updateExistingArtifacts;
   private Button updateByGuid;
   private Button deleteUnmatchedArtifacts;

   private final ArtifactSelectPanel artifactSelectPanel;
   private final ArtifactExtractorSelectPanel parserSelectPanel;
   private final ArtifactTypeSelectPanel artifactTypeSelectPanel;
   private final AttributeTypeSelectPanel attributeTypeSelectPanel;
   private final RoughArtifactCollector collector;
   private final ArtifactExtractorContributionManager importContributionManager;
   private final SelectionLatch selectionLatch;
   private final Collection<ArtifactTypeToken> selectedArtifactTypes;

   private static StringBuilder operationReportMessages;

   protected ArtifactImportPage(File defaultSourceFile, Artifact defaultDestinationArtifact) {
      super(PAGE_NAME);
      selectedArtifactTypes = new ArrayList<>();
      selectionLatch = new SelectionLatch();
      collector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
      artifactSelectPanel = new ArtifactSelectPanel();
      artifactSelectPanel.setDialogTitle("Select Destination Artifact");
      artifactSelectPanel.setDialogMessage(
         "Select a destination artifact. Imported items will be added as children of the selected artifact.");

      artifactSelectPanel.setDefaultItem(defaultDestinationArtifact);

      artifactTypeSelectPanel = new ArtifactTypeSelectPanel();
      artifactTypeSelectPanel.setDialogTitle("Import as Artifact Type");
      artifactTypeSelectPanel.setDialogMessage("Select what artifact type data should be imported as.");

      attributeTypeSelectPanel = new AttributeTypeSelectPanel();
      attributeTypeSelectPanel.setDialogTitle("Select Attribute Types");
      attributeTypeSelectPanel.setDialogMessage(
         "Select which attribute types should be used to match imported and existing artifacts.");

      importContributionManager = new ArtifactExtractorContributionManager();
      parserSelectPanel = new ArtifactExtractorSelectPanel(importContributionManager);

      setTitle("Import artifacts into OSEE");
      setDescription("Import artifacts into Define");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_IMPORT_WIZARD));

      operationReportMessages = new StringBuilder();

      this.defaultSourceFile = defaultSourceFile;
   }

   public RoughArtifactCollector getCollectedArtifacts() {
      return collector;
   }

   public void setDefaultDestinationArtifact(Artifact destinationArtifact) {
      artifactSelectPanel.setDefaultItem(destinationArtifact);
   }

   public Artifact getDefaultDestinationArtifact() {
      return artifactSelectPanel.getDefaultItem();
   }

   public File getDefaultSourceFile() {
      return defaultSourceFile;
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   @Override
   public void handleEvent(Event arg0) {
      updateExtractedElements();
      updatePageCompletion();
   }

   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());

      createDestinationArtifactSelectArea(composite);
      createSourceFileArea(composite);
      createParserSelectionArea(composite);
      createArtifactTypeSelectArea(composite);
      createNonChangingAttributeTypeSelectArea(composite);

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());
      setControl(composite);
      selectionLatch.setCurrentValues(getDestinationArtifact(), getSourceFile(), getArtifactParser());
      selectionLatch.latch();
   }

   private void createDestinationArtifactSelectArea(Composite parent) {
      Label selectParentInstructions = new Label(parent, SWT.NONE);
      selectParentInstructions.setText("Select parent artifact:");

      artifactSelectPanel.createControl(parent);
      artifactSelectPanel.addListener(this);
   }

   private void createSourceFileArea(Composite parent) {
      directoryFileSelector = new DirectoryOrFileSelector(parent, SWT.NONE, "Import Source", this);
      if (defaultSourceFile == null) {
         directoryFileSelector.setDirectorySelected(true);
      } else {
         directoryFileSelector.setDirectorySelected(!defaultSourceFile.isFile());
         directoryFileSelector.setText(defaultSourceFile.getAbsolutePath());
      }
      directoryFileSelector.addListener(SWT.Selection, this);
   }

   private void createParserSelectionArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Select Parser");
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      Label selectParserInstructions = new Label(composite, SWT.NONE);
      selectParserInstructions.setText("Select the method to be used for importing the selected file or directory:");

      parserSelectPanel.addListener(this);
      parserSelectPanel.createControl(composite);
   }

   private void createArtifactTypeSelectArea(Composite parent) {
      Label selectArtifactTypeInstructions = new Label(parent, SWT.NONE);
      selectArtifactTypeInstructions.setText("Select artifact type for imported data:");

      artifactTypeSelectPanel.createControl(parent);
      artifactTypeSelectPanel.addListener(this);
   }

   private void createNonChangingAttributeTypeSelectArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setText("Options");
      group.setLayout(new GridLayout(1, false));
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Composite updateComp = new Composite(group, SWT.NULL);
      updateComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      updateComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      updateExistingArtifacts = new Button(updateComp, SWT.CHECK);
      updateExistingArtifacts.setText("Update existing child artifacts");
      updateExistingArtifacts.setToolTipText(
         "All imported artifacts will be checked against the root\n" + "import artifact and the content will be placed on the artifact\n" + "that has the same identifying attributes and level from the root");
      updateExistingArtifacts.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
      updateExistingArtifacts.setSelection(false);

      updateByGuid = new Button(updateComp, SWT.CHECK);
      updateByGuid.setText("by GUID");
      updateByGuid.setToolTipText("Match imported artifacts based on user-supplied GUIDs");
      updateByGuid.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
      updateByGuid.setSelection(false);
      updateByGuid.setEnabled(false);

      deleteUnmatchedArtifacts = new Button(group, SWT.CHECK);
      deleteUnmatchedArtifacts.setText("Delete unmatched artifacts");
      deleteUnmatchedArtifacts.setToolTipText(
         "Any child artifacts that cannot be matched to an imported artifact will be deleted.");
      deleteUnmatchedArtifacts.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
      deleteUnmatchedArtifacts.setSelection(false);
      deleteUnmatchedArtifacts.setEnabled(false);

      final Composite composite = new Composite(group, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      composite.setEnabled(false);
      attributeTypeSelectPanel.createControl(composite);
      attributeTypeSelectPanel.addListener(this);

      updateExistingArtifacts.addListener(SWT.Selection, this);
      updateExistingArtifacts.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            boolean isNowSelected = updateExistingArtifacts.getSelection();
            widgetEnabledHelper(composite, isNowSelected);
            deleteUnmatchedArtifacts.setEnabled(isNowSelected);
            deleteUnmatchedArtifacts.setSelection(false);
            updateByGuid.setEnabled(isNowSelected);
            updateByGuid.setSelection(false);
         }
      });
      updateByGuid.addListener(SWT.Selection, this);
      widgetEnabledHelper(composite, false);
   }

   private void widgetEnabledHelper(Control control, boolean isEnabled) {
      control.setEnabled(isEnabled);
      if (control instanceof Composite) {
         for (Control child : ((Composite) control).getChildren()) {
            widgetEnabledHelper(child, isEnabled);
         }
      }
   }

   private boolean isUpdateExistingSelected() {
      return updateExistingArtifacts.getSelection();
   }

   private boolean isUpdateByGuidSelected() {
      return updateByGuid.getSelection();
   }

   public boolean isDeleteUnmatchedSelected() {
      return deleteUnmatchedArtifacts.getSelection();
   }

   public ArtifactCreationStrategy getArtifactCreationStrategy() {
      if (isUpdateExistingSelected() && isUpdateByGuidSelected()) {

         if (parserSelectPanel.getArtifactExtractor().getName().contains("DOORS")) {
            return ArtifactCreationStrategy.CREATE_ON_DOORS_BEST_FIT;
         } else {
            return ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID;
         }
      } else if (isUpdateExistingSelected()) {
         return ArtifactCreationStrategy.CREATE_ON_DIFFERENT_ATTRIBUTES;
      } else {
         return ArtifactCreationStrategy.CREATE_NEW_ALWAYS;
      }
   }

   public boolean isDirectory() {
      boolean result = false;
      if (Widgets.isAccessible(directoryFileSelector)) {
         result = directoryFileSelector.isDirectorySelected();
      }
      return result;
   }

   public File getSourceFile() {
      File sourceFile = null;
      if (Widgets.isAccessible(directoryFileSelector)) {
         sourceFile = directoryFileSelector.getSingleSelection();
      }
      return sourceFile;
   }

   public IArtifactExtractor getArtifactParser() {
      return parserSelectPanel.getArtifactExtractor();
   }

   public Artifact getDestinationArtifact() {
      return artifactSelectPanel.getSelected();
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactTypeSelectPanel.getSelected();
   }

   public Collection<AttributeTypeToken> getNonChangingAttributes() {
      return attributeTypeSelectPanel.getSelected();
   }

   @Override
   protected boolean validateSourceGroup() {
      return getSourceFile() != null;
   }

   @Override
   protected boolean validateDestinationGroup() {
      return getDestinationArtifact() != null;
   }

   @Override
   protected boolean validateOptionsGroup() {
      return getArtifactParser() != null && //
         selectionLatch.areSelectionsValid() && !selectionLatch.hasChanged() && //
         getArtifactType() != null && //
         (!updateExistingArtifacts.getSelection() || updateByGuid.getSelection() || getNonChangingAttributes() != null);
   }

   @Override
   protected void restoreWidgetValues() {
      super.restoreWidgetValues();
      IDialogSettings settings = getDialogSettings();
      if (settings != null) {
         if (getDefaultSourceFile() == null) {
            directoryFileSelector.setDirectorySelected(settings.getBoolean("isDirectory"));
            String file = settings.get("source.file");
            if (Strings.isValid(file)) {
               directoryFileSelector.setText(file);
            }
         }

         String parser = settings.get("selected.parser");
         if (Strings.isValid(parser)) {
            for (IArtifactExtractor item : importContributionManager.getExtractors()) {
               if (parser.equals(item.getClass().getSimpleName())) {
                  parserSelectPanel.setArtifactExtractor(item);
               }
            }
         }
         if (getDefaultDestinationArtifact() == null) {
            String guid = settings.get("destination.artifact.guid");
            String branchUuidStr = settings.get("destination.branch.uuid");

            if (GUID.isValid(guid) && Strings.isNumeric(branchUuidStr)) {
               try {
                  Long bramchUuid = Long.valueOf(branchUuidStr);
                  Artifact artifact = ArtifactQuery.getArtifactFromId(guid, BranchId.valueOf(bramchUuid));
                  artifactSelectPanel.setDefaultItem(artifact);
               } catch (OseeCoreException ex) {
                  OseeLog.logf(Activator.class, Level.SEVERE,
                     "Unable to restore destination artifact- guid:[%s] branch uuid:[%d]", guid, branchUuidStr);
               }
            }
         }

         boolean toUpdate = settings.getBoolean("is.update.existing.selected");
         updateExistingArtifacts.setSelection(toUpdate);
         deleteUnmatchedArtifacts.setEnabled(toUpdate);
         if (toUpdate) {
            try {
               ArtifactType artType = ArtifactTypeManager.getType(getArtifactType());
               attributeTypeSelectPanel.setAllowedAttributeTypes(
                  artType.getAttributeTypes(BranchManager.getBranch(getDestinationArtifact().getBranch())));
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         } else {
            attributeTypeSelectPanel.setAllowedAttributeTypes(new ArrayList<>());
         }
      }
   }

   @Override
   protected void saveWidgetValues() {
      super.saveWidgetValues();
      IDialogSettings settings = getDialogSettings();
      if (settings != null) {

         settings.put("isDirectory", isDirectory());

         File file = getSourceFile();
         if (file != null) {
            settings.put("source.file", getSourceFile().getAbsolutePath());
         }

         IArtifactExtractor parser = getArtifactParser();
         if (parser != null) {
            settings.put("selected.parser", parser.getClass().getSimpleName());
         }

         Artifact artifact = getDestinationArtifact();
         if (artifact != null) {
            settings.put("destination.artifact.guid", artifact.getGuid());
            settings.put("destination.branch.uuid", artifact.getBranch().getId());
         }
         settings.put("is.update.existing.selected", isUpdateExistingSelected());
      }
   }

   private synchronized void updateExtractedElements() {
      selectionLatch.setCurrentValues(getDestinationArtifact(), getSourceFile(), getArtifactParser());
      if (selectionLatch.areSelectionsValid()) {
         selectionLatch.latch();

         collector.reset();

         final Artifact destinationArtifact = selectionLatch.currentSelected.destinationArtifact;
         final File sourceFile = selectionLatch.currentSelected.sourceFile;
         final IArtifactExtractor extractor = selectionLatch.currentSelected.extractor;

         operationReportMessages.setLength(0);

         IOperation op = ArtifactImportOperationFactory.createArtifactsCompOperation("Extracting data from source",
            sourceFile, destinationArtifact, new OperationLogger() {
               @Override
               public void log(String... row) {
                  for (String warningMessage : row) {
                     operationReportMessages.append(warningMessage);
                  }
               };
            }, extractor, collector, selectedArtifactTypes, true);

         selectedArtifactTypes.clear();

         if (executeOperation(op)) {
            artifactTypeSelectPanel.setAllowedArtifactTypes(selectedArtifactTypes);
            try {
               if (getArtifactType() != null) {
                  ArtifactType specificArtifactType = ArtifactTypeManager.getType(getArtifactType());
                  attributeTypeSelectPanel.setAllowedAttributeTypes(specificArtifactType.getAttributeTypes(
                     BranchManager.getBranch(getDestinationArtifact().getBranch())));
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
   }

   protected boolean executeOperation(final IOperation operation) {
      final IStatus[] status = new IStatus[1];
      try {
         getContainer().run(true, true, new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
               try {
                  status[0] = Operations.executeWorkAndCheckStatus(operation, monitor);
               } catch (OseeCoreException ex) {
                  if (monitor.isCanceled()) {
                     throw new InterruptedException();
                  } else {
                     throw new InvocationTargetException(ex);
                  }
               }
            }
         });
      } catch (InterruptedException e) {
         return false;
      } catch (InvocationTargetException e) {
         displayErrorDialog(e.getTargetException());
         return false;
      }

      //grab the error here and
      if (operationReportMessages.length() != 0) {
         setMessage(operationReportMessages.toString(), IMessageProvider.WARNING);
      } else {
         setMessage("", IMessageProvider.NONE);
      }

      if (status[0].isOK()) {
         setErrorMessage(null);
      } else {
         setErrorMessage(status[0].getMessage());
      }
      return true;
   }

   private static final class SelectionLatch {
      protected final SelectionData lastSelected;
      protected final SelectionData currentSelected;

      public SelectionLatch() {
         lastSelected = new SelectionData();
         currentSelected = new SelectionData();
      }

      public void setCurrentValues(Artifact destinationArtifact, File sourceFile, IArtifactExtractor extractor) {
         this.currentSelected.setValues(destinationArtifact, sourceFile, extractor);
      }

      public void latch() {
         lastSelected.setValues(currentSelected.destinationArtifact, currentSelected.sourceFile,
            currentSelected.extractor);
      }

      public boolean areSelectionsValid() {
         return currentSelected.isValid();
      }

      public boolean hasChanged() {
         return !lastSelected.destinationArtifact.equals(currentSelected.destinationArtifact) && //
            !lastSelected.sourceFile.equals(currentSelected.sourceFile) && //
            !lastSelected.extractor.equals(currentSelected.extractor) && //
            !lastSelected.delegate.equals(currentSelected.delegate);
      }
   }

   private static final class SelectionData {
      protected Artifact destinationArtifact;
      protected File sourceFile;
      protected IArtifactExtractor extractor;
      protected IArtifactExtractorDelegate delegate;

      public void setValues(Artifact destinationArtifact, File sourceFile, IArtifactExtractor extractor) {
         this.destinationArtifact = destinationArtifact;
         this.sourceFile = sourceFile;
         this.extractor = extractor;
         this.delegate = extractor != null ? extractor.getDelegate() : null;
      }

      public boolean isValid() {
         return Conditions.notNull(destinationArtifact, sourceFile,
            extractor) && (extractor.isDelegateRequired() ? extractor.hasDelegate() : true);
      }
   }
}
