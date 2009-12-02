package org.eclipse.osee.framework.types.bridge.wizards;

import java.io.File;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkspace;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

public class ResourceSelectionPage extends WizardDataTransferPage {
   private static final String PAGE_NAME = "osee.define.wizardPage.artifactImportSourcePage";

   private Text text;

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

      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Select destination...");
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());

      text = new Text(composite, SWT.SINGLE | SWT.BORDER);
      text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      text.addListener(SWT.Modify, this);

      Button button = new Button(composite, SWT.PUSH);
      button.setText("&Browse...");
      button.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
            File file = getFile();
            if (file != null && Strings.isValid(file.getAbsolutePath())) {
               dialog.setFilterPath(file.getAbsolutePath());
            } else {
               dialog.setFilterPath(AWorkspace.getWorkspacePath());
            }
            String path = dialog.open();

            File directory = path != null ? new File(path) : null;
            if (directory != null && directory.isDirectory()) {
               text.setText(directory.getPath());
            }
         }
      });

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());
      setControl(composite);
   }

   public File getFile() {
      return new File(text.getText());
   }

}
