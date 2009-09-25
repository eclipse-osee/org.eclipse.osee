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
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactExtractorContributionManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.FilterArtifactTypesByAttributeTypes;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractorDelegate;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactExtractorSelectPanel;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactSelectPanel;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactTypeSelectPanel;
import org.eclipse.osee.framework.ui.skynet.panels.AttributeTypeSelectPanel;
import org.eclipse.osee.framework.ui.swt.ALayout;
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
   private File defaultSourceFile;
   private Button updateExistingArtifacts;

   private final ArtifactSelectPanel artifactSelectPanel;
   private final ArtifactExtractorSelectPanel parserSelectPanel;
   private final ArtifactTypeSelectPanel artifactTypeSelectPanel;
   private final AttributeTypeSelectPanel attributeTypeSelectPanel;
   private final RoughArtifactCollector collector;
   private final ArtifactExtractorContributionManager importContributionManager;
   private final SelectionLatch selectionLatch;
   private final Collection<ArtifactType> selectedArtifactTypes;

   protected ArtifactImportPage() {
      super(PAGE_NAME);
      selectedArtifactTypes = new ArrayList<ArtifactType>();
      selectionLatch = new SelectionLatch();
      collector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
      artifactSelectPanel = new ArtifactSelectPanel();
      artifactSelectPanel.setDialogTitle("Select Destination Artifact");
      artifactSelectPanel.setDialogMessage("Select a destination artifact. Imported items will be added as children of the selected artifact.");

      artifactTypeSelectPanel = new ArtifactTypeSelectPanel();
      artifactTypeSelectPanel.setDialogTitle("Import as Artifact Type");
      artifactTypeSelectPanel.setDialogMessage("Select what artifact type data should be imported as.");

      attributeTypeSelectPanel = new AttributeTypeSelectPanel();
      attributeTypeSelectPanel.setDialogTitle("Import as Attribute Type");
      attributeTypeSelectPanel.setDialogMessage("Select what artifact type data should be imported as.");

      importContributionManager = new ArtifactExtractorContributionManager();
      parserSelectPanel = new ArtifactExtractorSelectPanel(importContributionManager);

      setTitle("Import artifacts into OSEE");
      setDescription("Import artifacts into Define");
      setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.ARTIFACT_IMPORT_WIZARD));

   }

   public RoughArtifactCollector getCollectedArtifacts() {
      return collector;
   }

   public void setDefaultSourceFile(File resource) {
      this.defaultSourceFile = resource;
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
      updateWidgetEnablements();
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
      createNoneChangingAttributeTypeSelectArea(composite);

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

   private void createNoneChangingAttributeTypeSelectArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setText("Options");
      group.setLayout(new GridLayout(1, false));
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      updateExistingArtifacts = new Button(group, SWT.CHECK);
      updateExistingArtifacts.setText("Update existing child artifacts");
      updateExistingArtifacts.setToolTipText("All imported artifacts will be checked against the root\n" + "import artifact and the content will be placed on the artifact\n" + "that has the same identifying attributes and level from the root");
      updateExistingArtifacts.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
      updateExistingArtifacts.setSelection(false);

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
            boolean wasSelected = updateExistingArtifacts.getSelection();
            widgetEnabledHelper(composite, wasSelected);
         }
      });
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

   public boolean isUpdateExistingSelected() {
      return updateExistingArtifacts.getSelection();
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
         sourceFile = directoryFileSelector.getFile();
      }
      return sourceFile;
   }

   private IArtifactExtractor getArtifactParser() {
      return parserSelectPanel.getArtifactExtractor();
   }

   public Artifact getDestinationArtifact() {
      return artifactSelectPanel.getSelected();
   }

   public ArtifactType getArtifactType() {
      return artifactTypeSelectPanel.getSelected();
   }

   public Collection<AttributeType> getNoneChangingAttributes() {
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
      getArtifactType() != null;
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
            String branch = settings.get("destination.branch.guid");

            if (Strings.isValid(guid) && Strings.isValid(branch)) {
               try {
                  Artifact artifact = ArtifactQuery.getArtifactFromId(guid, BranchManager.getBranchByGuid(branch));
                  artifactSelectPanel.setDefaultItem(artifact);
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format(
                        "Unable to restore destination artifact- guid:[%s] branch guid:[%s]", guid, branch));
               }
            }
         }
         updateExistingArtifacts.setSelection(settings.getBoolean("is.update.existing.selected"));

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
            settings.put("destination.branch.guid", artifact.getBranch().getGuid());
         }
         settings.put("is.update.existing.selected", isUpdateExistingSelected());
      }
   }

   private synchronized void updateExtractedElements() {
      selectionLatch.setCurrentValues(getDestinationArtifact(), getSourceFile(), getArtifactParser());
      if (selectionLatch.areSelectionsValid()) {
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Artifact need to be extracted from from source");
         selectionLatch.latch();

         collector.reset();

         final Artifact destinationArtifact = selectionLatch.currentSelected.destinationArtifact;
         final File sourceFile = selectionLatch.currentSelected.sourceFile;
         final IArtifactExtractor extractor = selectionLatch.currentSelected.extractor;

         Collection<IOperation> ops = new ArrayList<IOperation>();
         ops.add(new SourceToRoughArtifactOperation(extractor, sourceFile, collector));
         ops.add(new FilterArtifactTypesByAttributeTypes(destinationArtifact.getBranch(), collector,
               selectedArtifactTypes));
         selectedArtifactTypes.clear();
         if (executeOperation(new CompositeOperation("Extracting data from source", SkynetGuiPlugin.PLUGIN_ID, ops))) {
            OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Extracted items from: " + sourceFile.getAbsoluteFile());
            artifactTypeSelectPanel.setAllowedArtifactTypes(selectedArtifactTypes);
         }
      }

   }

   protected boolean executeOperation(final IOperation operation) {
      try {
         getContainer().run(true, true, new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
               Operations.executeWork(operation, monitor, -1);
            }
         });
      } catch (InterruptedException e) {
         return false;
      } catch (InvocationTargetException e) {
         displayErrorDialog(e.getTargetException());
         return false;
      }

      IStatus status = operation.getStatus();
      if (status.isOK()) {
         setErrorMessage(null);
      } else {
         setErrorMessage(status.getChildren()[0].getMessage());
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
         return lastSelected.destinationArtifact != currentSelected.destinationArtifact && //
         lastSelected.sourceFile != currentSelected.sourceFile && //
         lastSelected.extractor != currentSelected.extractor && //
         lastSelected.delegate != currentSelected.delegate;
      }
   }

   private static final class SelectionData {
      protected Artifact destinationArtifact;
      protected File sourceFile;
      protected IArtifactExtractor extractor;
      protected IArtifactExtractorDelegate delegate;

      private SelectionData() {
      }

      public void setValues(Artifact destinationArtifact, File sourceFile, IArtifactExtractor extractor) {
         this.destinationArtifact = destinationArtifact;
         this.sourceFile = sourceFile;
         this.extractor = extractor;
         this.delegate = extractor != null ? extractor.getDelegate() : null;
      }

      public boolean isValid() {
         return destinationArtifact != null && sourceFile != null && extractor != null && (extractor.isDelegateRequired() ? extractor.hasDelegate() : true);
      }
   }
}