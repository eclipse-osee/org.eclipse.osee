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
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactSourceParserContributionManager;
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
   private File defaultResource;

   private final ArtifactSelectPanel artifactSelectPanel;
   private final ArtifactSourceParserSelectPanel parserSelectPanel;
   private ListViewer artifactTypeList;
   private Button reuseChildArtifacts;
   private final ArtifactSourceParserContributionManager importContributionManager;

   protected ArtifactImportSourcePage() {
      super(PAGE_NAME);
      setTitle("Import artifacts into OSEE");
      setDescription("Import artifacts into Define");
      artifactSelectPanel = new ArtifactSelectPanel();
      importContributionManager = new ArtifactSourceParserContributionManager();
      parserSelectPanel = new ArtifactSourceParserSelectPanel(importContributionManager);
   }

   public void setDefaultResource(File resource) {
      this.defaultResource = resource;
   }

   public void setDefaultDestinationArtifact(Artifact destinationArtifact) {
      this.artifactSelectPanel.setDefaultArtifact(destinationArtifact);
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   @Override
   public void handleEvent(Event arg0) {
      updateWidgetEnablements();
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

      reuseChildArtifacts = new Button(composite, SWT.CHECK);
      reuseChildArtifacts.setEnabled(true);
      reuseChildArtifacts.setText("Re-use Artifacts");
      reuseChildArtifacts.setToolTipText("All imported artifacts will be checked against the root\n" + "import artifact and the content will be placed on the artifact\n" + "that has the same identifying attributes and level from the root");
      reuseChildArtifacts.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 2, 1));
      reuseChildArtifacts.setSelection(false);
      reuseChildArtifacts.addListener(SWT.Selection, this);
   }

   private void createSourceFileArea(Composite parent) {
      directoryFileSelector = new DirectoryOrFileSelector(parent, SWT.NONE, "Import Source", this);
      if (defaultResource == null) {
         directoryFileSelector.setDirectorySelected(true);
      } else {
         directoryFileSelector.setDirectorySelected(!defaultResource.isFile());
         directoryFileSelector.setText(defaultResource.getAbsolutePath());
      }
   }

   private void createParserSelectionArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Select source parser");
      composite.setToolTipText("Select the method to be used for importing the selected file or directory");
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      parserSelectPanel.createControl(composite);
   }

   private void createArtifactTypeSelectArea(Composite parent) {
      Composite composite =
            createHidingGroup(parent, 2, "Select artifact type to import data as",
                  "Select artifact type to import data as");

      artifactTypeList = new ListViewer(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
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

   private File getSourceFile() {
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

   private Artifact getDestinationArtifact() {
      return artifactSelectPanel.getArtifact();
   }

   private void handleParsing() {
      try {
         getContainer().run(true, true, new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
               IOperation operation =
                     new SourceToRoughArtifactOperation("Extracting data from source", getArtifactParser(),
                           getSourceFile(), new RoughArtifactCollector(getDestinationArtifact()));
               Operations.executeWork(operation, monitor, -1);
            }
         });
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   protected void restoreWidgetValues() {
      super.restoreWidgetValues();
      IDialogSettings settings = getDialogSettings();
      if (settings != null) {
         //         settings.get(arg0)
      }
   }

   @Override
   protected void saveWidgetValues() {
      super.saveWidgetValues();
      IDialogSettings settings = getDialogSettings();
      if (settings != null) {

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
}
