/*
 * Created on Aug 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactSourceParserContributionManager;
import org.eclipse.osee.framework.skynet.core.importing.IArtifactSourceParser;
import org.eclipse.osee.framework.skynet.core.importing.IArtifactSourceParserDelegate;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.ArtifactTypeFilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.HidingComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan C. Schmitt
 * @author Roberto E. Escobar
 */
public class ArtifactImportSourcePage extends WizardDataTransferPage {

   private static final String PAGE_NAME = "osee.define.wizardPage.artifactImportSourcePage";

   private DirectoryOrFileSelector directoryFileSelector;
   private File selectedResource;
   private Combo parserCombo;
   private Combo parserComboDelegate;
   private List artifactTypeList;
   private final ArtifactSourceParserContributionManager importContributionManager;

   protected ArtifactImportSourcePage() {
      super(PAGE_NAME);
      setTitle("Import artifacts into OSEE");
      setDescription("Import artifacts into Define");

      importContributionManager = new ArtifactSourceParserContributionManager();
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

      createSourceFileArea(composite);
      createParserSelectionArea(composite);
      createParseOptionsArea(composite);
      createArtifactTypeSelectArea(composite);

      populateData();

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());
      setControl(composite);
   }

   private void createSourceFileArea(Composite parent) {
      directoryFileSelector = new DirectoryOrFileSelector(parent, SWT.NONE, "Import Source", this);
      if (selectedResource == null) {
         directoryFileSelector.setDirectorySelected(true);
      } else {
         directoryFileSelector.setDirectorySelected(!selectedResource.isFile());
         directoryFileSelector.setText(selectedResource.getAbsolutePath());
      }
   }

   private void createParserSelectionArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Select source parser");
      composite.setToolTipText("Select the method to be used for importing the selected file or directory");
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      parserCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
      parserCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      parserCombo.addListener(SWT.Selection, this);
   }

   private void createParseOptionsArea(Composite parent) {
      Composite delegateGroup =
            createHidingGroup(parent, 1, "Select additional parse option", "Select additional parse option");

      parserComboDelegate = new Combo(delegateGroup, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
      parserComboDelegate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      parserComboDelegate.addListener(SWT.Selection, this);
   }

   private void createArtifactTypeSelectArea(Composite parent) {
      Composite composite =
            createHidingGroup(parent, 2, "Select artifact type to import data as",
                  "Select artifact type to import data as");
      artifactTypeList = new List(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
      artifactTypeList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

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

   private void handleComboSelection() {
      String key = parserCombo.getItem(parserCombo.getSelectionIndex());
      Object object = parserCombo.getData(key);
      IArtifactSourceParser sourceParser = null;
      if (object instanceof IArtifactSourceParser) {
         sourceParser = (IArtifactSourceParser) object;
         // TODO add a floating tip text similar to Java doc/Content Assist
         parserCombo.setToolTipText(sourceParser.getDescription());
      } else {
         parserCombo.setToolTipText("Select a source parser");
      }

      Collection<IArtifactSourceParserDelegate> delegates =
            importContributionManager.getArtifactSourceParserDelegate(sourceParser);
      if (!delegates.isEmpty()) {
         parserComboDelegate.removeAll();
         for (IArtifactSourceParserDelegate handler : delegates) {
            parserComboDelegate.add(handler.getName());
            parserComboDelegate.setData(handler.getName(), handler);
         }
         parserComboDelegate.select(0);
      }
      parserComboDelegate.getParent().getParent().setVisible(!delegates.isEmpty());
      artifactTypeList.getParent().getParent().setVisible(sourceParser != null && sourceParser.usesTypeList());
      parserCombo.getParent().getParent().getParent().layout();
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

   private void populateData() {
      for (IArtifactSourceParser sourceParser : importContributionManager.getArtifactSourceParser()) {
         String extractorName = sourceParser.getName();
         parserCombo.add(extractorName);
         parserCombo.setData(extractorName, sourceParser);
         parserCombo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               handleComboSelection();
            }
         });
      }
      parserCombo.select(parserCombo.getItemCount() - 1);
      handleComboSelection();
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

   /* (non-Javadoc)
    * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
    */
   @Override
   public boolean isPageComplete() {
      boolean result = directoryFileSelector.getFile() != null;
      result &= parserCombo.getSelectionIndex() != -1;
      if (parserComboDelegate.isVisible()) {
         result &= parserComboDelegate.getSelectionIndex() != -1;
      }
      return result && super.isPageComplete();
   }
}
