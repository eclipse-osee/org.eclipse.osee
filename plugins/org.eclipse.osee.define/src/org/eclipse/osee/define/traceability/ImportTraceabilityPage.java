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
package org.eclipse.osee.define.traceability;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.define.internal.Activator;
import org.eclipse.osee.define.traceability.TraceUnitExtensionManager.TraceHandler;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.DirectoryOrFileSelector;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Ryan D. Brooks
 */
public class ImportTraceabilityPage extends WizardDataTransferPage {
   public static final String PAGE_NAME = "org.eclipse.osee.define.wizardPage.importTraceabilityPage";

   private static final String TRACE_UNIT_HANDLER_GROUP = "Select trace unit parser";
   private static final String GIT_CODE_STRUCTURE_GROUP = "Select to use Git codebase";
   private static final String INCLUDE_IMPD_GROUP = "Include IMPD";

   private DirectoryOrFileSelector directoryFileSelector;
   private BranchSelectComposite branchSelectComposite;
   private final Map<Button, Boolean> traceUnitHandlers;
   private boolean isGitCodeStructure = false;
   private boolean includeImpd = false;

   private IResource currentResourceSelection;

   public ImportTraceabilityPage(IStructuredSelection selection) {
      super(PAGE_NAME);

      setTitle("Import traceability into OSEE Define");
      setDescription("Import relations between artifacts");
      traceUnitHandlers = new HashMap<>();

      if (selection != null && selection.size() == 1) {
         Object firstElement = selection.getFirstElement();
         if (firstElement instanceof IAdaptable) {
            currentResourceSelection = ((IAdaptable) firstElement).getAdapter(IResource.class);
         }
      }
   }

   @Override
   public void createControl(Composite parent) {
      initializeDialogUnits(parent);

      Composite composite = new Composite(parent, SWT.NULL);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));
      composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
      composite.setFont(parent.getFont());

      createSourceGroup(composite);
      createParserSelectArea(composite);
      createGitStructureCheckbox(composite);
      createIncludeImpdCheckbox(composite);
      restoreWidgetValues();
      updateWidgetEnablements();
      setPageComplete(determinePageCompletion());

      setControl(composite);
   }

   /**
    * The <code>WizardResourceImportPage</code> implementation of this <code>Listener</code> method handles all events
    * and enablements for controls on this page. Subclasses may extend.
    *
    * @param event Event
    */
   @Override
   public void handleEvent(Event event) {
      setPageComplete(determinePageCompletion());
   }

   @Override
   protected boolean determinePageCompletion() {
      return super.determinePageCompletion() && preprocessInput(getImportFile());
   }

   protected void createSourceGroup(Composite parent) {
      directoryFileSelector = new DirectoryOrFileSelector(parent, SWT.NONE, "Import Source", this);

      if (currentResourceSelection == null) {
         // Select directory as the default
         directoryFileSelector.setDirectorySelected(true);
      } else {
         directoryFileSelector.setDirectorySelected(currentResourceSelection.getType() != IResource.FILE);
         directoryFileSelector.setText(currentResourceSelection.getLocation().toString());
      }

      Group composite = new Group(parent, SWT.NONE);
      composite.setText("Destination Branch");
      GridLayout gd = new GridLayout();
      composite.setLayout(gd);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      branchSelectComposite = new BranchSelectComposite(composite, SWT.BORDER, false);

      setPageComplete(determinePageCompletion());
   }

   protected void createParserSelectArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText(TRACE_UNIT_HANDLER_GROUP);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      try {
         for (TraceHandler handler : TraceUnitExtensionManager.getInstance().getAllTraceHandlers()) {
            createTraceHandler(composite, handler.getName(), handler.getId());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   private void createGitStructureCheckbox(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText(GIT_CODE_STRUCTURE_GROUP);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Button handlerButton = new Button(composite, SWT.CHECK);
      handlerButton.setText("Use Git Code Structure");
      handlerButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();
            if (source instanceof Button) {
               Button button = (Button) source;
               isGitCodeStructure = button.getSelection();
            }
         }
      });
   }

   private void createIncludeImpdCheckbox(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      composite.setText(INCLUDE_IMPD_GROUP);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Button handlerButton = new Button(composite, SWT.CHECK);
      handlerButton.setText("Include IMPD");
      handlerButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();
            if (source instanceof Button) {
               Button button = (Button) source;
               includeImpd = button.getSelection();
            }
         }
      });
   }

   private void createTraceHandler(Composite parent, String text, String handlerId) {
      Button handlerButton = new Button(parent, SWT.CHECK);
      handlerButton.setText(text);
      handlerButton.setData(handlerId);
      handlerButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Object source = e.getSource();
            if (source instanceof Button) {
               Button button = (Button) source;
               traceUnitHandlers.put(button, button.getSelection());
            }
            setPageComplete(determinePageCompletion());
         }
      });
      traceUnitHandlers.put(handlerButton, false);
   }

   public String[] getTraceUnitHandlerIds() {
      List<String> selectedIds = new ArrayList<>();
      for (Button button : traceUnitHandlers.keySet()) {
         Boolean value = traceUnitHandlers.get(button);
         if (value != null && value == true) {
            selectedIds.add((String) button.getData());
         }
      }
      return selectedIds.toArray(new String[selectedIds.size()]);
   }

   /*
    * @see WizardPage#becomesVisible
    */
   @Override
   public void setVisible(boolean visible) {
      super.setVisible(visible);
      // policy: wizards are not allowed to come up with an error message
      if (visible) {
         setErrorMessage(null);
      }
   }

   @Override
   protected boolean validateSourceGroup() {
      return directoryFileSelector.validate(this);
   }

   public File getImportFile() {
      return directoryFileSelector.getSingleSelection();
   }

   public BranchId getSelectedBranch() {
      return branchSelectComposite.getSelectedBranch();
   }

   public boolean isGitBased() {
      return isGitCodeStructure;
   }

   public boolean includeImpd() {
      return includeImpd;
   }

   @Override
   protected boolean allowNewContainerName() {
      return false;
   }

   private boolean preprocessInput(final File file) {
      final StringBuilder errorMessage = new StringBuilder();
      try {
         getContainer().run(true, true, new IRunnableWithProgress() {

            @Override
            public void run(IProgressMonitor monitor) {
               if (file != null) {
                  try {
                     int count = 0;
                     for (String path : Lib.readListFromFile(file, true)) {
                        File toCheck = new File(path);
                        if (!toCheck.exists()) {
                           count++;
                           errorMessage.append(String.format("\nPath does not exist: [%s]", path));
                        } else if (!toCheck.isDirectory()) {
                           count++;
                           errorMessage.append(String.format("\nNot a directory: [%s]", path));
                        }
                     }
                     if (count > 0) {
                        errorMessage.insert(0, String.format("%d paths have errors:", count));
                     }
                  } catch (IOException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
                  }

               }
            }
         });
      } catch (InvocationTargetException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
      } catch (InterruptedException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
      }

      if (errorMessage.length() != 0) {
         setErrorMessage(errorMessage.toString());
         return false;
      } else {
         setErrorMessage(null);
         setMessage(null);
         return true;
      }

   }

}