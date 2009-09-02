package org.eclipse.osee.framework.types.bridge.wizards;

import java.io.File;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

public class ResourceSelectionPage extends WizardDataTransferPage {
   private static final String PAGE_NAME = "osee.define.wizardPage.artifactImportSourcePage";

   private DirectoryOrFileSelector fileSelector;

   protected ResourceSelectionPage(String title) {
      super(PAGE_NAME);
      setTitle(title);
      setDescription(title);
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   @Override
   public void handleEvent(Event event) {
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());
   }

   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());

      fileSelector = new DirectoryOrFileSelector(composite, SWT.NONE, "File", this);
      fileSelector.addListener(SWT.Selection, this);

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());
      setControl(composite);
   }

   public File getFile() {
      return fileSelector.getFile();
   }

}
