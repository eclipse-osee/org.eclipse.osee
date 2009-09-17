/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.types.bridge.wizards;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.init.OseeTypesSetup;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.types.bridge.operations.ResolveImportsOperation;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.xtext.resource.ClassloaderClasspathUriResolver;
import org.eclipse.xtext.ui.core.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class OseeTypesImportPage extends WizardDataTransferPage {
   private static final String PAGE_NAME = "org.eclipse.osee.frameowkr.types.bridge.wizards.OseeTypesImportPage";

   private final OseeTypesSetup oseeTypesSetup;
   private File compositeFile;
   private final IStructuredSelection selection;
   private TreeViewer linksViewer;
   private final SelectOseeTypesPanel oseeTypesPanel;
   private final List<LinkNode> messages;
   private Button reportChanges;
   private Button persistChanges;
   private Button useCompareEditor;

   protected OseeTypesImportPage(IStructuredSelection selection, String title) {
      super(PAGE_NAME);
      this.selection = selection;
      oseeTypesSetup = new OseeTypesSetup();
      oseeTypesPanel = new SelectOseeTypesPanel();
      oseeTypesPanel.setDefaultItem(getPreselected());
      setTitle(title);
      setDescription("Select *.osee files to import");
      messages = new ArrayList<LinkNode>();
   }

   private List<IFile> getPreselected() {
      List<IFile> resources = new ArrayList<IFile>();
      if (selection != null) {
         Iterator<?> iterator = selection.iterator();
         while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof IFile) {
               IFile file = (IFile) object;
               if (file.getFileExtension().equals("osee")) {
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

      Label sectionTitle = new Label(composite, SWT.NONE);
      sectionTitle.setText("Resolved dependencies:");
      sectionTitle.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, true, false));

      linksViewer = new TreeViewer(composite, SWT.BORDER);
      linksViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      linksViewer.setContentProvider(new LinkNodeContentProvider());
      linksViewer.setLabelProvider(new LinkNodeLabelProvider());
      linksViewer.setInput(messages);

      createOptions(composite);

      restoreWidgetValues();
      updateWidgetEnablements();
      updateExtractedElements();
      setPageComplete(determinePageCompletion());
      setControl(composite);
   }

   private void createOptions(Composite parent) {
      Group composite = new Group(parent, SWT.NULL);
      composite.setLayout(new GridLayout(1, false));
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setFont(parent.getFont());
      composite.setText("Options");

      reportChanges = new Button(composite, SWT.CHECK);
      reportChanges.setText("Report Changes");
      reportChanges.setSelection(true);

      useCompareEditor = new Button(composite, SWT.CHECK);
      useCompareEditor.setText("Use Compare Editor");
      useCompareEditor.setSelection(true);

      persistChanges = new Button(composite, SWT.CHECK);
      persistChanges.setText("Persist Changes");
      persistChanges.setSelection(false);
   }

   public boolean isPersistAllowed() {
      return persistChanges.getSelection();
   }

   public boolean isReportChanges() {
      return reportChanges.getSelection();
   }

   public boolean useCompareEditor() {
      return useCompareEditor.getSelection();
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
      if (oseeTypesPanel.getSelected() != null) {
         setErrorMessage(null);
         File file = getTypesToImport();
         if (file != null) {
            file.delete();
            setCompositeFile(null);
         }
         OseeLog.log(Activator.class, Level.INFO, "Resolving imports");

         final List<IFile> selectedFiles = new ArrayList<IFile>(oseeTypesPanel.getSelected());
         final List<LinkNode> dependencyData = new ArrayList<LinkNode>();

         Collection<IOperation> ops = new ArrayList<IOperation>();
         ops.add(new ResolveImportsOperation(new ClassloaderClasspathUriResolver(), selectedFiles, dependencyData));
         ops.add(new CreateCombinedFile(dependencyData));
         if (executeOperation(new CompositeOperation("Resolving imports", SkynetGuiPlugin.PLUGIN_ID, ops))) {
            messages.clear();
            messages.addAll(dependencyData);
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
      private final List<LinkNode> dependencyData;

      public CreateCombinedFile(List<LinkNode> dependencyData) {
         super("Create combined file", Activator.PLUGIN_ID);
         this.dependencyData = dependencyData;
      }

      private void loadMap(LinkNode node, Map<String, URL> map) throws MalformedURLException {
         for (LinkNode child : node.getChildren()) {
            loadMap(child, map);
         }
         String path = node.getUri().toString();
         map.put(path, new URL(node.getUri().toString()));
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         Map<String, URL> resolvedEntries = new LinkedHashMap<String, URL>();
         for (LinkNode node : dependencyData) {
            loadMap(node, resolvedEntries);
         }
         File file = oseeTypesSetup.createCombinedFile(resolvedEntries);
         setCompositeFile(file);
      }
   }
}