package org.eclipse.osee.framework.types.bridge.wizards;

import java.io.File;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.types.bridge.internal.Activator;
import org.eclipse.osee.framework.types.bridge.operations.XTextToOseeTypeOperation;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

public class OseeTypesImportWizard extends Wizard implements IImportWizard {
   private OseeTypesImportPage mainPage;
   private IStructuredSelection selection;

   public OseeTypesImportWizard() {
      super();
      setDialogSettings(Activator.getDefault().getDialogSettings());
      setWindowTitle("OSEE Types Import Wizard");
      setNeedsProgressMonitor(true);

      setHelpAvailable(true);
   }

   @Override
   public boolean performFinish() {
      File file = mainPage.getTypesToImport();
      IOperation operation = new XTextToOseeTypeOperation(null, file.toURI());
      Operations.executeAsJob(operation, true);
      return true;
   }

   @Override
   public void init(IWorkbench workbench, IStructuredSelection selection) {
      this.selection = selection;
   }

   @Override
   public void addPages() {
      mainPage = new OseeTypesImportPage(selection, getWindowTitle());
      addPage(mainPage);
   }
}
