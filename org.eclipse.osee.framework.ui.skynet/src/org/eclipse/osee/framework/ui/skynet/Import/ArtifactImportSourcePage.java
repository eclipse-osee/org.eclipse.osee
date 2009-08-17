/*
 * Created on Aug 13, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.ArtifactImportContributionManager;
import org.eclipse.osee.framework.skynet.core.importing.IWordOutlineContentHandler;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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
   private CCombo combo;
   private List handlerList;
   private final ArtifactImportContributionManager importContributionManager;

   protected ArtifactImportSourcePage() {
      super(PAGE_NAME);
      setTitle("Import artifacts into OSEE");
      setDescription("Import artifacts into Define");

      importContributionManager = new ArtifactImportContributionManager();
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   @Override
   public void handleEvent(Event arg0) {
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

      combo = new CCombo(composite, SWT.SINGLE | SWT.BORDER | SWT.FLAT | SWT.DROP_DOWN);
      combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      handlerList = new List(composite, SWT.BORDER | SWT.CHECK | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE);
      handlerList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      handlerList.setVisible(false);

      for (ArtifactExtractor artifactExtractor : importContributionManager.getArtifactSourceParser()) {
         String extractorName = artifactExtractor.getName();
         combo.add(extractorName);
         combo.setData(extractorName, artifactExtractor);
         combo.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               handleComboSelection();
            }
         });

         if ("Word Outline".equals(extractorName)) {
            combo.select(combo.getItems().length - 1);
            handleComboSelection();
         }
      }
   }

   private void handleComboSelection() {
      String key = combo.getItem(combo.getSelectionIndex());
      Object object = combo.getData(key);
      if (object instanceof ArtifactExtractor) {
         ArtifactExtractor extractor = (ArtifactExtractor) object;
         // TODO add a floating tip text similar to Java doc
         combo.setToolTipText(extractor.getDescription());
      } else {
         combo.setToolTipText("Select a source parser");
      }

      Collection<IWordOutlineContentHandler> handlers = importContributionManager.getHandler(key);
      if (!handlers.isEmpty()) {
         handlerList.removeAll();
         for (IWordOutlineContentHandler handler : handlers) {
            handlerList.add(handler.getName());
            handlerList.setData(handler.getName(), handler);
         }
         handlerList.setVisible(true);
      } else {
         handlerList.setVisible(false);
      }
      handlerList.getParent().layout();
   }
}
