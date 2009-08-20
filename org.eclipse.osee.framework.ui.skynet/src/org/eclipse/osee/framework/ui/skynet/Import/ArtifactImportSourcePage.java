/*
 * Created on Aug 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactSourceParserContributionManager;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactSourceParser;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactSourceParserDelegate;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactSelectPanel;
import org.eclipse.osee.framework.ui.skynet.panels.ArtifactSourceParserSelectPanel;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactTypeLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTypeFilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.HidingComposite;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

   private final ArtifactSelectPanel artifactSelectPanel;
   private final ArtifactSourceParserSelectPanel parserSelectPanel;
   private ListViewer artifactTypeList;
   private Button reuseChildArtifacts;

   private final RoughArtifactCollector mockArtifactCollector;
   private final ArtifactSourceParserContributionManager importContributionManager;

   protected ArtifactImportSourcePage() {
      super(PAGE_NAME);
      setTitle("Import artifacts into OSEE");
      setDescription("Import artifacts into Define");
      mockArtifactCollector = new RoughArtifactCollector(new RoughArtifact(RoughArtifactKind.PRIMARY));
      artifactSelectPanel = new ArtifactSelectPanel();
      importContributionManager = new ArtifactSourceParserContributionManager();
      parserSelectPanel = new ArtifactSourceParserSelectPanel(importContributionManager);
   }

   public RoughArtifactCollector getCollectedArtifacts() {
      return mockArtifactCollector;
   }

   public void setDefaultSourceFile(File resource) {
      this.defaultSourceFile = resource;
   }

   public void setDefaultDestinationArtifact(Artifact destinationArtifact) {
      artifactSelectPanel.setDefaultArtifact(destinationArtifact);
   }

   public Artifact getDefaultDestinationArtifact() {
      return artifactSelectPanel.getDefaultArtifact();
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
      handleParsing();
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

      artifactTypeList = new ListViewer(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY);
      artifactTypeList.setLabelProvider(new ArtifactTypeLabelProvider());
      artifactTypeList.setContentProvider(new ArrayContentProvider());
      artifactTypeList.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      Button selectTypes = new Button(composite, SWT.PUSH);
      selectTypes.setLayoutData(new GridData(SWT.END, SWT.TOP, false, false));
      selectTypes.setText("Select Artifact Type");
      selectTypes.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            handleAttributeTypeSelection();
         }
      });
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

   private void handleAttributeTypeSelection() {
      Collection<ArtifactType> artifactTypes = null;
      try {
         artifactTypes = ArtifactTypeManager.getAllTypes();
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         artifactTypes = Collections.emptyList();
      }
      String title = "Import as Artifact Type";
      String message = "Select what artifact type data should be imported as.";
      ArtifactTypeFilteredTreeDialog dialog = new ArtifactTypeFilteredTreeDialog(title, message, artifactTypes);
      //      Object lastSelected = attributeTypeList.getData(attributeTypeList.getSItem(0));
      //      if (lastSelected != null) {
      //         try {
      //            dialog.setInitialSelections(ArtifactTypeManager.getType(lastSelected.toString()));
      //         } catch (OseeCoreException ex) {
      //            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING,
      //                  "Dialog could not be initialized to the last Artifact Type selected", ex);
      //         }
      //      }

      int result = dialog.open();
      if (result == Window.OK) {
         ArtifactType artifactType = dialog.getSelection();
         String key = artifactType.getName();
         artifactTypeList.add(key);
         artifactTypeList.setData(key, artifactType);
      }
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

   private IArtifactSourceParser getArtifactParser() {
      return parserSelectPanel.getArtifactParser();
   }

   private IArtifactSourceParserDelegate getArtifactParserDelegate() {
      return parserSelectPanel.getArtifactParserDelegate();
   }

   public Artifact getDestinationArtifact() {
      return artifactSelectPanel.getArtifact();
   }

   private void handleParsing() {
      if (isPageComplete()) {
         mockArtifactCollector.reset();
         IOperation operation =
               new SourceToRoughArtifactOperation("Extracting data from source", getArtifactParser(), getSourceFile(),
                     mockArtifactCollector);
         if (executeOperation(operation)) {
            Set<String> names = new HashSet<String>();
            for (RoughArtifact artifact : mockArtifactCollector.getRoughArtifacts()) {
               names.addAll(artifact.getURIAttributes().keySet());
               names.addAll(artifact.getAttributes().keySet());
            }
            System.out.println(names);
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
            for (IArtifactSourceParser item : importContributionManager.getArtifactSourceParser()) {
               if (parser.equals(item.getClass().getSimpleName())) {
                  parserSelectPanel.setArtifactParser(item);
               }
            }
         }
         if (getDefaultDestinationArtifact() == null) {
            String guid = settings.get("destination.artifact.guid");
            String branch = settings.get("destination.branch.guid");

            if (Strings.isValid(guid) && Strings.isValid(branch)) {
               try {
                  Artifact artifact = ArtifactQuery.getArtifactFromId(guid, BranchManager.getBranchByGuid(branch));
                  artifactSelectPanel.setDefaultArtifact(artifact);
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

         IArtifactSourceParser parser = getArtifactParser();
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

   /*
    * (non-Javadoc)
    * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
    */
   @Override
   public boolean isPageComplete() {
      boolean result = getSourceFile() != null;
      result &= getArtifactParser() != null;
      result &= getArtifactParserDelegate() != null;
      result &= getDestinationArtifact() != null;
      return result && super.isPageComplete();
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
