/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.plugin.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardDataTransferPage;

/**
 * @author Robert A. Fisher
 */
public class DirectoryOrFileSelector extends Composite implements Listener {

   private final Button radDirectory;
   private final Button radSingleFile;
   private final Text txtDirectory;
   private final Text txtFiles;
   private final Button btnDirectory;
   private final Button btnSingleFile;
   private final int singleOrMulti;

   public static final String FILE_SEPARATOR = ",";

   /**
    * Creates a DirectoryOrFileSelector with only single file selection
    */
   public DirectoryOrFileSelector(Composite parent, int style, String name, Listener listener) {
      this(parent, style, name, listener, false);
   }

   public DirectoryOrFileSelector(Composite parent, int style, String name, Listener listener, boolean multiFileSelect) {
      super(parent, style);
      GridLayout gdMain = new GridLayout();
      gdMain.marginHeight = 0;
      gdMain.marginWidth = 0;
      setLayout(gdMain);
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Group composite = new Group(this, style);
      composite.setText(name);
      GridLayout gd = new GridLayout();
      gd.numColumns = 3;
      composite.setLayout(gd);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      singleOrMulti = multiFileSelect ? SWT.MULTI : SWT.SINGLE;

      radDirectory = new Button(composite, SWT.RADIO);
      radDirectory.setText("Directory:");
      radDirectory.addListener(SWT.Selection, this);
      radDirectory.addListener(SWT.Selection, listener);
      txtDirectory = new Text(composite, SWT.SINGLE | SWT.BORDER);
      txtDirectory.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      txtDirectory.addListener(SWT.Modify, this);
      txtDirectory.addListener(SWT.Modify, listener);
      btnDirectory = new Button(composite, SWT.PUSH);
      btnDirectory.setText("&Browse...");
      btnDirectory.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            File directory = selectDirectory();
            if (directory != null && directory.isDirectory()) {
               txtDirectory.setText(directory.getPath());
            }
         }

      });

      radSingleFile = new Button(composite, SWT.RADIO);
      radSingleFile.setText("File" + (multiFileSelect ? "(s):" : ":"));
      radSingleFile.addListener(SWT.Selection, this);
      radSingleFile.addListener(SWT.Selection, listener);

      txtFiles = new Text(composite, SWT.SINGLE | SWT.BORDER);
      GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
      layoutData.widthHint = 250;
      txtFiles.setLayoutData(layoutData);
      txtFiles.addListener(SWT.Modify, this);
      txtFiles.addListener(SWT.Modify, listener);
      btnSingleFile = new Button(composite, SWT.PUSH);
      btnSingleFile.setText("&Browse...");
      btnSingleFile.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Iterable<File> files = selectFiles();
            List<String> paths = new LinkedList<>();
            for (File file : files) {
               if (file != null && file.isFile()) {
                  paths.add(file.getPath());
               }
            }
            setText(Collections.toString(FILE_SEPARATOR, paths));
         }

      });

   }

   private void updateWidgetEnablements() {
      boolean directorySelected = radDirectory.getSelection();

      txtDirectory.setEnabled(directorySelected);
      btnDirectory.setEnabled(directorySelected);

      txtFiles.setEnabled(!directorySelected);
      btnSingleFile.setEnabled(!directorySelected);
   }

   /**
    * @return new File if path is valid, null otherwise.
    */
   public Iterable<File> getSelection() {
      String path = isDirectorySelected() ? txtDirectory.getText() : txtFiles.getText();
      return Strings.isValid(path) ? getFiles(path) : null;
   }

   /**
    * @return single selection if valid, null otherwise
    */
   public File getSingleSelection() {
      File toReturn = null;
      Iterable<File> selection = getSelection();
      if (selection != null) {
         toReturn = selection.iterator().next();
      }
      return toReturn;
   }

   private Iterable<File> getFiles(String paths) {
      List<File> toReturn = new LinkedList<>();
      for (String path : paths.split(FILE_SEPARATOR)) {
         toReturn.add(new File(path));
      }
      return toReturn;
   }

   public String getText() {
      return (isDirectorySelected() ? txtDirectory : txtFiles).getText();
   }

   public boolean isDirectorySelected() {
      return radDirectory.getSelection();
   }

   public void setDirectorySelected(boolean selected) {
      radDirectory.setSelection(selected);
      radSingleFile.setSelection(!selected);

      updateWidgetEnablements();
   }

   public void setText(String text) {
      (isDirectorySelected() ? txtDirectory : txtFiles).setText(text);
   }

   public boolean validate(WizardDataTransferPage wizardPage) {
      Iterable<File> files = getSelection();
      boolean toReturn = false;
      if (files != null) {
         if (isDirectorySelected()) {
            toReturn = files.iterator().next().isDirectory();
         } else {
            toReturn = true;
            for (File file : files) {
               toReturn &= file.isFile();
            }
         }
      }

      if (toReturn == false) {
         wizardPage.setErrorMessage(getText() + " is not a " + (isDirectorySelected() ? "directory" : "file"));
      }
      return toReturn;
   }

   private String getFilterPath() {
      String toReturn = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
      Iterable<File> file = getSelection();
      if (file != null) {
         File first = file.iterator().next();
         if (Strings.isValid(first.getAbsolutePath())) {
            toReturn = first.getAbsolutePath();
         }
      }
      return toReturn;
   }

   private Iterable<File> selectFiles() {
      FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | singleOrMulti);
      dialog.setFilterPath(getFilterPath());

      String path = dialog.open();

      if (path != null) {
         String selectedPath = dialog.getFilterPath();
         List<File> files = new LinkedList<>();
         for (String filename : dialog.getFileNames()) {
            files.add(new File(selectedPath + IPath.SEPARATOR + filename));
         }
         return files;
      } else {
         return null;
      }
   }

   private File selectDirectory() {
      DirectoryDialog dialog = new DirectoryDialog(getShell(), SWT.OPEN);
      dialog.setFilterPath(getFilterPath());

      String path = dialog.open();

      if (path != null) {
         return new File(path);
      } else {
         return null;
      }
   }

   @Override
   public void handleEvent(Event event) {
      updateWidgetEnablements();
   }
}
