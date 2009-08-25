package org.eclipse.osee.framework.types.bridge.wizards;

import java.io.File;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.types.bridge.internal.Activator;
import org.eclipse.osee.framework.types.bridge.operations.OseeExcelImportOperation;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

public class NewOseeTypesFileWizard extends Wizard implements INewWizard {
   private NewOseeTypesFilePage mainPage;

   public NewOseeTypesFileWizard() {
      super();
      setDialogSettings(Activator.getDefault().getDialogSettings());
      setWindowTitle("New OSEE Types File");
      setNeedsProgressMonitor(true);

      setHelpAvailable(true);
   }

   @Override
   public boolean performFinish() {
      File sourceFile = mainPage.getSourceFile();
      File destination = mainPage.getDestinationFile();

      IOperation operation = new OseeExcelImportOperation(sourceFile, destination);
      Operations.executeAsJob(operation, true);
      return true;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
   }

   @Override
   public void addPages() {
      mainPage = new NewOseeTypesFilePage(getWindowTitle());
      addPage(mainPage);
   }

}
