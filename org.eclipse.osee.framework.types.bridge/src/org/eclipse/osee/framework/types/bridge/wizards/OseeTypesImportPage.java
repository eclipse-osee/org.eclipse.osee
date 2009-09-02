package org.eclipse.osee.framework.types.bridge.wizards;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.init.OseeTypesSetup;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.xtext.ui.core.internal.Activator;

public class OseeTypesImportPage extends WizardDataTransferPage {
   private static final String PAGE_NAME = "org.eclipse.osee.frameowkr.types.bridge.wizards.OseeTypesImportPage";
   private final OseeTypesSetup oseeTypesSetup;
   private File compositeFile;
   private final IStructuredSelection selection;
   private Button resolveDependenciesFromInstalled;
   private TreeViewer linksViewer;
   private final SelectOseeTypesPanel oseeTypesPanel;
   private final List<String> messages;

   protected OseeTypesImportPage(IStructuredSelection selection, String title) {
      super(PAGE_NAME);
      this.selection = selection;
      oseeTypesSetup = new OseeTypesSetup();
      oseeTypesPanel = new SelectOseeTypesPanel();
      oseeTypesPanel.setDefaultItem(getPreselected());
      setTitle(title);
      setDescription(title);
      messages = new ArrayList<String>();
   }

   private List<IFile> getPreselected() {
      List<IFile> resources = new ArrayList<IFile>();
      if (selection != null) {
         Iterator<?> iterator = selection.iterator();
         while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof IFile) {
               IFile file = (IFile) object;
               if (file.getFileExtension().equals(".osee")) {
                  resources.add(file);
               }
            }
         }
      }
      return resources;
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   @Override
   public void handleEvent(Event event) {
      updateWidgetEnablements();
      updateExtractedElements();
   }

   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());

      oseeTypesPanel.createControl(composite);
      oseeTypesPanel.addListener(this);

      resolveDependenciesFromInstalled = new Button(composite, SWT.CHECK);
      resolveDependenciesFromInstalled.setText("Resolve dependencies with installed components");
      resolveDependenciesFromInstalled.addListener(SWT.Selection, this);

      linksViewer = new TreeViewer(composite, SWT.BORDER);
      linksViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      linksViewer.setContentProvider(new ArrayTreeContentProvider());
      linksViewer.setLabelProvider(new LabelProvider());
      linksViewer.setInput(messages);

      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());
      setControl(composite);
   }

   public File getTypesToImport() {
      return compositeFile != null ? compositeFile : null;
   }

   private void setCompositeFile(File compositeFile) {
      this.compositeFile = compositeFile;
   }

   @Override
   protected boolean validateSourceGroup() {
      return oseeTypesPanel.getSelected() != null;
   }

   @Override
   protected boolean validateDestinationGroup() {
      return getTypesToImport() != null;
   }

   private synchronized void updateExtractedElements() {
      // Determine what is required for update;
      if (oseeTypesPanel.getSelected() != null) {
         File file = getTypesToImport();
         if (file != null) {
            file.delete();
            setCompositeFile(null);
         }
         OseeLog.log(Activator.class, Level.INFO, "Resolving imports");

         final List<IFile> selectedFiles = new ArrayList<IFile>(oseeTypesPanel.getSelected());
         final HashCollection<IFile, String> requiredImports = new HashCollection<IFile, String>();
         final List<String> runResults = new ArrayList<String>();
         final boolean resolveWithInstalled = resolveDependenciesFromInstalled.getSelection();

         Collection<IOperation> ops = new ArrayList<IOperation>();
         ops.add(new ExtractRequiredImports(selectedFiles, requiredImports));
         ops.add(new CreateCombinedFile(requiredImports, resolveWithInstalled, oseeTypesSetup, runResults));
         if (executeOperation(new CompositeOperation("Resolving imports", SkynetGuiPlugin.PLUGIN_ID, ops))) {
            messages.clear();
            messages.addAll(runResults);
            linksViewer.refresh();
            setPageComplete(determinePageCompletion());
         }
      }
   }

   protected boolean executeOperation(final IOperation operation) {
      try {
         getContainer().run(true, true, new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
               Operations.executeWork(operation, monitor, -1);
            }
         });
      } catch (InterruptedException e) {
         return false;
      } catch (InvocationTargetException e) {
         displayErrorDialog(e.getTargetException());
         return false;
      }

      IStatus status = operation.getStatus();
      if (status.isOK()) {
         setErrorMessage(null);
      } else {
         setErrorMessage(status.getChildren()[0].getMessage());
      }

      return true;
   }

   private final class CreateCombinedFile extends AbstractOperation {
      private final HashCollection<IFile, String> requiredImports;
      private final OseeTypesSetup oseeTypesSetup;
      private final boolean resolveWithInstalled;
      private final List<String> messages;

      public CreateCombinedFile(HashCollection<IFile, String> requiredImports, boolean resolveWithInstalled, OseeTypesSetup oseeTypesSetup, List<String> messages) {
         super("Create combined file", Activator.PLUGIN_ID);
         this.requiredImports = requiredImports;
         this.oseeTypesSetup = oseeTypesSetup;
         this.resolveWithInstalled = resolveWithInstalled;
         this.messages = messages;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         Map<String, URL> resolved = new LinkedHashMap<String, URL>();
         Map<String, URL> installedElements = null;

         // TODO Order required imports;
         // TODO Resolve required based on what's being imported and if enabled what's installed 
         for (IFile iFile : requiredImports.keySet()) {
            iFile.getRawLocationURI();
            //            messages.add();
            if (resolveWithInstalled) {
               if (installedElements == null) {
                  installedElements = oseeTypesSetup.getOseeTypeExtensions();
               }
               //               installedElements.get(key)
            }
            messages.add("Messaged must be reported"); // Report any unresolved or resolved;
         }
         if (resolved != null) {
            File file = oseeTypesSetup.createCombinedFile(resolved);
            setCompositeFile(file);
         }

      }
   }

   private final class ExtractRequiredImports extends AbstractOperation {
      private final List<IFile> selectedItems;
      private final HashCollection<IFile, String> requiredImports;

      public ExtractRequiredImports(List<IFile> selectedItems, HashCollection<IFile, String> requiredImports) {
         super("Extract imports", Activator.PLUGIN_ID);
         this.selectedItems = selectedItems;
         this.requiredImports = requiredImports;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         Pattern pattern = Pattern.compile("import\\s+\"(.*)?\"");
         Matcher matcher = pattern.matcher("");
         double workPercentage = 1.0 / selectedItems.size();
         for (IFile file : selectedItems) {
            InputStream inputStream = null;
            try {
               inputStream = file.getContents();
               String inputString = Lib.inputStreamToString(inputStream);
               matcher.reset(inputString);
               while (matcher.matches()) {
                  requiredImports.put(file, matcher.group(1));
               }
            } finally {
               monitor.worked(calculateWork(workPercentage));
               if (inputStream != null) {
                  inputStream.close();
               }
            }
         }
      }
   }
}