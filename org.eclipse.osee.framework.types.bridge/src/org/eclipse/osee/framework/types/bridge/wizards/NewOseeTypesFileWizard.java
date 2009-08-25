package org.eclipse.osee.framework.types.bridge.wizards;

import java.io.File;
import java.util.Iterator;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
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
      IContainer parent = null;
      Object object = selection.getFirstElement();
      if (object instanceof IFile) {
         IFile iFile = (IFile) object;
         parent = iFile.getParent();
      } else if (object instanceof IContainer) {
         parent = (IContainer) object;
      }
      mainPage = new NewOseeTypesFilePage(getWindowTitle(), parent);
   }

   @Override
   public void addPages() {
      addPage(mainPage);
   }

}
