package org.eclipse.osee.framework.types.bridge.wizards;

import java.io.File;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

public class NewOseeTypesFilePage extends WizardDataTransferPage {
   private static final String PAGE_NAME = "org.eclipse.osee.framework.types.bridge.wizards.NewOseeTypeFilePage";

   private DirectoryOrFileSelector sourceFileSelector;
   private DirectoryOrFileSelector destinationFileSelector;

   protected NewOseeTypesFilePage(String title) {
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

      sourceFileSelector = new DirectoryOrFileSelector(composite, SWT.NONE, "File", this);
      sourceFileSelector.addListener(SWT.Selection, this);

      destinationFileSelector = new DirectoryOrFileSelector(composite, SWT.NONE, "File", this);
      destinationFileSelector.addListener(SWT.Selection, this);

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());
      setControl(composite);
   }

   @Override
   protected void restoreWidgetValues() {
      IDialogSettings settings = getDialogSettings();
      if (settings != null) {
         restoreSelector(settings, "new.osee.types.source", sourceFileSelector);
         restoreSelector(settings, "new.osee.types.destination", destinationFileSelector);
      }
   }

   private void restoreSelector(IDialogSettings settings, String key, DirectoryOrFileSelector selector) {
      String file = settings.get("key");
      boolean isSelected = settings.getBoolean(key + ".isDir");
      if (Strings.isValid(file)) {
         selector.setText(file);
         selector.setDirectorySelected(isSelected);
      }
   }

   private void saveSelector(IDialogSettings settings, String key, DirectoryOrFileSelector selector) {
      File file = selector.getFile();
      if (file != null) {
         settings.put(key, file.getAbsolutePath());
         settings.put(key + ".isDir", file.isDirectory());
      }
   }

   @Override
   protected void saveWidgetValues() {
      IDialogSettings settings = getDialogSettings();
      if (settings != null) {
         saveSelector(settings, "new.osee.types.source", sourceFileSelector);
         saveSelector(settings, "new.osee.types.destination", destinationFileSelector);
      }
   }

   @Override
   public boolean isPageComplete() {
      return getSourceFile() != null && getDestinationFile() != null && super.isPageComplete();
   }

   public File getSourceFile() {
      return sourceFileSelector.getFile();
   }

   public File getDestinationFile() {
      return destinationFileSelector.getFile();
   }
}
