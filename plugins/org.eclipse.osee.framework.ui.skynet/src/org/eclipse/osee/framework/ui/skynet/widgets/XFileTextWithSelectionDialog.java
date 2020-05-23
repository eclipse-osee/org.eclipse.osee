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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.io.File;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Donald G. Dunne
 */
public class XFileTextWithSelectionDialog extends XText {

   private String defaultFileSelection;
   private final Type type;
   public static enum Type {
      File,
      Directory
   };

   public XFileTextWithSelectionDialog(String displayLabel, Type type, String defaultFileSelection) {
      this(displayLabel, type);
      this.defaultFileSelection = defaultFileSelection;
   }

   public XFileTextWithSelectionDialog(String displayLabel, Type type) {
      super(displayLabel);
      this.type = type;
   }

   public XFileTextWithSelectionDialog() {
      this("", Type.File);
   }

   public XFileTextWithSelectionDialog(String displayLabel) {
      this(displayLabel, Type.File);
   }

   @Override
   public void createControls(final Composite parent, int horizontalSpan, boolean fillText) {
      int lhspan = horizontalSpan;
      if (!verticalLabel && lhspan < 3) {
         lhspan = 3;
      }
      super.createControls(parent, lhspan, fillText);

      Button fileDialog = new Button(getStyledText().getParent(), SWT.NONE);
      fileDialog.setText("Select " + type.name());
      if (Strings.isValid(defaultFileSelection)) {
         set(defaultFileSelection);
      }
      fileDialog.addSelectionListener(new SelectionListener() {

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (type == Type.File) {
               FileDialog dialog = new FileDialog(Displays.getActiveShell(), SWT.OPEN);
               dialog.setFilterExtensions(new String[] {"*.*"});
               File dir = new File(defaultFileSelection != null ? defaultFileSelection : "");
               if (dir.isFile() || dir.isDirectory()) {
                  dialog.setFilterPath(defaultFileSelection);
               } else {
                  dialog.setFilterPath("c:\\");
               }

               String result = dialog.open();
               if (Strings.isValid(result)) {
                  setText(dialog.getFilterPath() + File.separatorChar + dialog.getFileName());
               }
            } else if (type == Type.Directory) {
               DirectoryDialog dialog = new DirectoryDialog(Displays.getActiveShell(), SWT.OPEN);
               File dir = new File(defaultFileSelection != null ? defaultFileSelection : "");
               if (dir.isFile() || dir.isDirectory()) {
                  dialog.setFilterPath(defaultFileSelection);
               } else {
                  dialog.setFilterPath("c:\\");
               }

               String result = dialog.open();
               if (Strings.isValid(result)) {
                  setText(result);
               }
            }
         }
      });
   }

   /**
    * @return the defaultFileSelection
    */
   public String getDefaultFileSelection() {
      return defaultFileSelection;
   }

   /**
    * @param defaultFileSelection the defaultFileSelection to set
    */
   public void setDefaultFileSelection(String defaultFileSelection) {
      this.defaultFileSelection = defaultFileSelection;
   }

}
