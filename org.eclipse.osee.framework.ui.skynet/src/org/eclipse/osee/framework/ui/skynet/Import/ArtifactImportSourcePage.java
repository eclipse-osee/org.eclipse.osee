/*
 * Created on Aug 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.TypeValidityManager;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactExtractorContributionManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractorDelegate;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactExtractorSelectPanel;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactSelectPanel;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactTypeSelectPanel;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.HidingComposite;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan C. Schmitt
 * @author Roberto E. Escobar
 */
public class ArtifactImportSourcePage extends WizardDataTransferPage {

   private static final String PAGE_NAME = "osee.define.wizardPage.artifactImportSourcePage";

   private DirectoryOrFileSelector directoryFileSelector;
   private File defaultSourceFile;
   private Button reuseChildArtifacts;

   private final ArtifactSelectPanel artifactSelectPanel;
   private final ArtifactExtractorSelectPanel parserSelectPanel;
   private final ArtifactTypeSelectPanel artifactTypeSelectPanel;

   private final RoughArtifactCollector mockArtifactCollector;
   private final ArtifactExtractorContributionManager importContributionManager;
   private final SelectionLatch selectionLatch;
   private final Collection<ArtifactType> selectedArtifactTypes;

   protected ArtifactImportSourcePage() {
      super(PAGE_NAME);
      selectedArtifactTypes = new ArrayList<ArtifactType>();
      selectionLatch = new SelectionLatch();
      mockArtifactCollector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
      artifactSelectPanel = new ArtifactSelectPanel();
      artifactTypeSelectPanel = new ArtifactTypeSelectPanel();
      importContributionManager = new ArtifactExtractorContributionManager();
      parserSelectPanel = new ArtifactExtractorSelectPanel(importContributionManager);

      setTitle("Import artifacts into OSEE");
      setDescription("Import artifacts into Define");
   }

   public RoughArtifactCollector getCollectedArtifacts() {
      return mockArtifactCollector;
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
      System.out.println("Event: " + arg0.widget);
      updateWidgetEnablements();
      updateExtractedElements();
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

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());
      setControl(composite);
      selectionLatch.setCurrentValues(getDestinationArtifact(), getSourceFile(), getArtifactParser());
      selectionLatch.latch();
   }

   private void createDestinationArtifactSelectArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Select destination artifact");
      composite.setToolTipText("Select parent artifact");
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      artifactSelectPanel.createControl(composite);
      artifactSelectPanel.addListener(this);

      reuseChildArtifacts = new Button(composite, SWT.CHECK);
      reuseChildArtifacts.setText("Re-use Artifacts");
      reuseChildArtifacts.setToolTipText("All imported artifacts will be checked against the root\n" + "import artifact and the content will be placed on the artifact\n" + "that has the same identifying attributes and level from the root");
      reuseChildArtifacts.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));
      reuseChildArtifacts.setSelection(false);
      reuseChildArtifacts.addListener(SWT.Selection, this);
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
      composite.setText("Select source parser");
      composite.setToolTipText("Select the method to be used for importing the selected file or directory");
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      parserSelectPanel.createControl(composite);
      parserSelectPanel.addListener(this);
   }

   private void createArtifactTypeSelectArea(Composite parent) {
      Composite composite =
            createHidingGroup(parent, 2, "Select artifact type to import data as",
                  "Select artifact type to import data as");
      artifactTypeSelectPanel.createControl(composite);
      artifactTypeSelectPanel.addListener(this);
   }

   private Composite createHidingGroup(Composite parent, int numberOfColumns, String text, String toolTip) {
      Composite composite = new HidingComposite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Group delegateGroup = new Group(composite, SWT.NONE);
      delegateGroup.setText(text);
      delegateGroup.setToolTipText(toolTip);
      delegateGroup.setLayout(new GridLayout(numberOfColumns, false));
      delegateGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      return delegateGroup;
   }

   public boolean isReUseSelected() {
      return reuseChildArtifacts.getSelection();
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

   @Override
   public boolean isPageComplete() {
      boolean result = getSourceFile() != null;
      result &= getArtifactParser() != null;
      result &= getDestinationArtifact() != null;
      result &= selectionLatch.areSelectionsValid() && !selectionLatch.hasChanged();
      return result && super.isPageComplete();
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
         reuseChildArtifacts.setSelection(settings.getBoolean("is.reuse.selected"));
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
         settings.put("is.reuse.selected", isReUseSelected());
      }
   }

   private synchronized void updateExtractedElements() {
      selectionLatch.setCurrentValues(getDestinationArtifact(), getSourceFile(), getArtifactParser());
      if (selectionLatch.areSelectionsValid()) {
         //         && selectionLatch.hasChanged()) {
         //      }
         selectionLatch.latch();
         System.out.println("Will Parse");
         mockArtifactCollector.reset();

         final Artifact destinationArtifact = selectionLatch.currentSelected.destinationArtifact;
         final File sourceFile = selectionLatch.currentSelected.sourceFile;
         final IArtifactExtractor extractor = selectionLatch.currentSelected.extractor;

         Collection<IOperation> ops = new ArrayList<IOperation>();
         ops.add(new SourceToRoughArtifactOperation(extractor, sourceFile, mockArtifactCollector));
         ops.add(new FilterArtifactTypesByAllowedAttributes(destinationArtifact.getBranch(), selectedArtifactTypes));
         if (executeOperation(new CompositeOperation("Extracting data from source", SkynetGuiPlugin.PLUGIN_ID, ops))) {
            System.out.println("Will Parsed");
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
      if (!status.isOK()) {
         ErrorDialog.openError(getContainer().getShell(), operation.getName(), null, // no special message
               status);
         return false;
      }
      return true;
   }

   private final class SelectionLatch {
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

   private final class SelectionData {
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
         return destinationArtifact != null && sourceFile != null && extractor != null && extractor.isDelegateRequired() ? extractor.hasDelegate() : true;
      }
   }

   private final class FilterArtifactTypesByAllowedAttributes extends AbstractOperation {
      private final Branch branch;
      private final Collection<ArtifactType> selectedArtifactTypes;

      public FilterArtifactTypesByAllowedAttributes(Branch branch, Collection<ArtifactType> selectedArtifactTypes) {
         super("Filter Artifact Types", SkynetGuiPlugin.PLUGIN_ID);
         this.branch = branch;
         this.selectedArtifactTypes = selectedArtifactTypes;
      }

      /*
       * (non-Javadoc)
       * @see
       * org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         Set<String> names = new HashSet<String>();
         for (RoughArtifact artifact : mockArtifactCollector.getRoughArtifacts()) {
            names.addAll(artifact.getURIAttributes().keySet());
            names.addAll(artifact.getAttributes().keySet());
         }
         selectedArtifactTypes.clear();
         Set<AttributeType> requiredTypes = new HashSet<AttributeType>();
         for (String name : names) {
            AttributeType type = AttributeTypeManager.getType(name);
            if (type != null) {
               requiredTypes.add(type);
            }
         }
         for (ArtifactType artifactType : TypeValidityManager.getValidArtifactTypes(branch)) {
            Collection<AttributeType> attributeType =
                  TypeValidityManager.getAttributeTypesFromArtifactType(artifactType, branch);
            if (Collections.setComplement(requiredTypes, attributeType).isEmpty()) {
               selectedArtifactTypes.add(artifactType);
            }
         }
         //         System.out.println("Required: " + requiredTypes);
         //         for (ArtifactType type : selectedArtifactTypes) {
         //            System.out.println("Artifact: " + type.getName() + " Attributes: " + TypeValidityManager.getAttributeTypesFromArtifactType(
         //                  type, branch));
         //         }
      }
   }

   //  mainPage.isReUseSelected();
   //  mainPage.getResolver();
   //      try {
   //         ArtifactType primaryArtifactType = extractor.usesTypeList() ? mainPage.getSelectedType() : null;
   //         ArtifactType secondaryArtifactType = ArtifactTypeManager.getType("Heading");
   //
   //         if (reuseArtifactRoot == null) {
   //            artifactResolver = new NewArtifactImportResolver(primaryArtifactType, secondaryArtifactType);
   //         } else { // only non-null when reuse artifacts is checked
   //            Collection<AttributeType> identifyingAttributes = attributeTypePage.getSelectedAttributeDescriptors();
   //            artifactResolver =
   //                  new RootAndAttributeBasedArtifactResolver(primaryArtifactType, secondaryArtifactType,
   //                        identifyingAttributes, false);
   //         }
}
