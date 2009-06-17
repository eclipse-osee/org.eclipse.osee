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
package org.eclipse.osee.ote.ui.test.manager.wizards;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The
 * page will only accept file name without the extension OR with the extension that matches the
 * expected one (mpe).
 */

public class TestManagerNewWizardPage extends WizardPage {
   private Text containerText;

   private Text fileText;

   private ISelection selection;

   /**
    * Constructor for SampleNewWizardPage.
    * 
    * @param selection
    */
   public TestManagerNewWizardPage(ISelection selection) {
      super("wizardPage");
      setTitle("Test Manager Editor File");
      setDescription("This wizard creates a new Test Manager file.");
      this.selection = selection;
   }

   /**
    * @see IDialogPage#createControl(Composite)
    */
   public void createControl(Composite parent) {
      Composite container = new Composite(parent, SWT.NULL);
      GridLayout layout = new GridLayout();
      container.setLayout(layout);
      layout.numColumns = 3;
      layout.verticalSpacing = 9;
      Label label = new Label(container, SWT.NULL);
      label.setText("&Container:");

      containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      containerText.setLayoutData(gd);
      containerText.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            dialogChanged();
         }
      });

      Button button = new Button(container, SWT.PUSH);
      button.setText("Browse...");
      button.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            handleBrowse();
         }
      });
      label = new Label(container, SWT.NULL);
      label.setText("&File name:");

      fileText = new Text(container, SWT.BORDER | SWT.SINGLE);
      gd = new GridData(GridData.FILL_HORIZONTAL);
      fileText.setLayoutData(gd);
      fileText.addModifyListener(new ModifyListener() {
         public void modifyText(ModifyEvent e) {
            dialogChanged();
         }
      });
      initialize();
      dialogChanged();
      setControl(container);
   }

   public String getContainerName() {
      return containerText.getText();
   }

   public String getFileName() {
      return fileText.getText();
   }

   /**
    * Ensures that both text fields are set.
    */

   private void dialogChanged() {
      String container = getContainerName();
      String fileName = getFileName();
      IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
      IResource resource = root.findMember(new Path(container));
      IContainer rcontainer = (IContainer) resource;

      if (container.length() == 0) {
         updateStatus("File container must be specified");
         return;
      }
      if (fileName.length() == 0) {
         updateStatus("File name must be specified");
         return;
      }

      // verify extension isn't there
      if ((fileName.endsWith("tmc") == true) || (fileName.endsWith(".")) == true) {
         updateStatus("Do not add .tmc extenstion.");
         return;
      }
      // verify title is alpha-numeric with spaces and dashes
      Matcher m = Pattern.compile("^[\\w]+([\\w_]*[\\w])?$").matcher(fileName);
      boolean match = false;
      while (m.find()) {
         match = true;
      }
      if (!match) {
         updateStatus("Filename must be alpha-numeric with \"_\" \n" + "And can not begin or end with a space.");
         return;
      }
      // verify existing file doesn't exist
      final IFile file = rcontainer.getFile(new Path(fileName + ".tmc"));
      if (file.exists()) {
         updateStatus("File with this name already exists.");
         return;
      }
      int dotLoc = fileName.lastIndexOf('.');
      if (dotLoc != -1) {
         String ext = fileName.substring(dotLoc + 1);
         if (ext.equalsIgnoreCase("tmc") == false) {
            updateStatus("File extension must be \"tmc\"");
            return;
         }
      }
      updateStatus(null);
   }

   /**
    * Uses the standard container selection dialog to choose the new value for the container field.
    */

   private void handleBrowse() {
      ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(),
            ResourcesPlugin.getWorkspace().getRoot(), false, "Select new file container");
      if (dialog.open() == ContainerSelectionDialog.OK) {
         Object[] result = dialog.getResult();
         if (result.length == 1) {
            containerText.setText(((Path) result[0]).toOSString());
         }
      }
   }

   /**
    * Tests if the current workbench selection is a suitable container to use.
    */

   private void initialize() {
      if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
         IStructuredSelection ssel = (IStructuredSelection) selection;
         if (ssel.size() > 1)
            return;
         Object obj = ssel.getFirstElement();
         if (obj instanceof IResource) {
            IContainer container;
            if (obj instanceof IContainer)
               container = (IContainer) obj;
            else
               container = ((IResource) obj).getParent();
            containerText.setText(container.getFullPath().toString());
         }
      }
   }

   private void updateStatus(String message) {
      setErrorMessage(message);
      setPageComplete(message == null);
   }
}