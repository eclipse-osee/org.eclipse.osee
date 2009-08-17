/*
 * Created on Aug 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.util.Collection;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactSourceParserContributionManager;
import org.eclipse.osee.framework.skynet.core.importing.IArtifactSourceParser;
import org.eclipse.osee.framework.skynet.core.importing.IArtifactSourceParserDelegate;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
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
   private File selectedResource;
   private Combo parserCombo;
   private Combo parserComboDelegate;
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
      System.out.println(arg0);
   }

   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());

      createSourceFileArea(composite);
      createSourceParseArea(composite);

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

   private void createSourceParseArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Select source parser");
      composite.setToolTipText("Select the method to be used for importing the selected file or directory");
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      parserCombo = new Combo(composite, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
      parserCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      parserCombo.addListener(SWT.Selection, this);

      parserComboDelegate = new Combo(composite, SWT.SINGLE | SWT.BORDER | SWT.DROP_DOWN);
      parserComboDelegate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      parserComboDelegate.setVisible(false);
      parserComboDelegate.addListener(SWT.Selection, this);

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
         parserComboDelegate.setVisible(true);
      } else {
         parserComboDelegate.setVisible(false);
      }
      parserComboDelegate.getParent().layout();
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
