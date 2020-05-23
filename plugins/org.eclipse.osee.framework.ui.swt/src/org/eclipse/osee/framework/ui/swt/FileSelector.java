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

package org.eclipse.osee.framework.ui.swt;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * @author Robert A. Fisher
 */
public class FileSelector extends Composite {

   private final Label radSingleFile;
   private final Text txtSingleFile;
   private final Button btnSingleFile;
   private final String defaultPath;
   private final String[] allowedExtensions;

   public FileSelector(Composite parent, int style, String name, String defaultPath, Listener listener, String... allowedExtensions) {
      super(parent, style);
      this.defaultPath = defaultPath;
      this.allowedExtensions = allowedExtensions;
      setLayout(new GridLayout());
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      Group composite = new Group(this, style);
      composite.setText(name);
      GridLayout gd = new GridLayout();
      gd.numColumns = 3;
      composite.setLayout(gd);
      composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      radSingleFile = new Label(composite, SWT.NONE);
      radSingleFile.setText("File:");
      txtSingleFile = new Text(composite, SWT.SINGLE | SWT.BORDER);
      txtSingleFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      txtSingleFile.addListener(SWT.Modify, listener);
      btnSingleFile = new Button(composite, SWT.PUSH);
      btnSingleFile.setText("&Browse...");
      btnSingleFile.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            File file = selectFile();
            if (file != null && file.isFile()) {
               txtSingleFile.setText(file.getPath());
            }
         }

      });
   }

   public File getFile() {
      return new File(getText());
   }

   public String getText() {
      return txtSingleFile.getText();
   }

   public void setText(String text) {
      txtSingleFile.setText(text);
   }

   private File selectFile() {
      FileDialog dialog = new FileDialog(getShell(), SWT.OPEN | SWT.SINGLE);
      dialog.setFilterExtensions(allowedExtensions);
      dialog.setFilterPath(defaultPath);

      String path = dialog.open();
      return path != null ? new File(path) : null;
   }

}
