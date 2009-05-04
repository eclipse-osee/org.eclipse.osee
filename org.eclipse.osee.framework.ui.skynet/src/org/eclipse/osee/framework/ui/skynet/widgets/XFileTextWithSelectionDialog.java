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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.io.File;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

/**
 * @author Donald G. Dunne
 */
public class XFileTextWithSelectionDialog extends XText {

   private String defaultFileSelection;
   private final Type type;
   public static enum Type {
      File, Directory
   };

   public XFileTextWithSelectionDialog(String displayLabel, Type type) {
      super(displayLabel);
      this.type = type;
   }

   public XFileTextWithSelectionDialog() {
      this("", Type.File);
   }

   /**
    * @param displayLabel
    */
   public XFileTextWithSelectionDialog(String displayLabel) {
      this(displayLabel, Type.File);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#createWidgets(org.eclipse.swt.widgets.Composite, int, boolean)
    */
   @Override
   public void createControls(final Composite parent, int horizontalSpan, boolean fillText) {
      super.createControls(parent, horizontalSpan, fillText);

      Button fileDialog = new Button(getStyledText().getParent(), SWT.NONE);
      fileDialog.setText("Select " + type.name());
      fileDialog.addSelectionListener(new SelectionListener() {

         public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
         }

         public void widgetSelected(SelectionEvent e) {
            if (type == Type.File) {
               FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
               dialog.setFilterExtensions(new String[] {"*.*"});
               String defaultDir = getDefaultFileSelection();
               File dir = new File(defaultDir != null ? defaultDir : "");
               if (dir.isFile() || dir.isDirectory())
                  dialog.setFilterPath(defaultDir);
               else
                  dialog.setFilterPath("c:\\");

               String result = dialog.open();
               if (result != null && !result.equals("")) {
                  setText(dialog.getFilterPath() + File.separatorChar + dialog.getFileName());
               }
            } else if (type == Type.Directory) {
               DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
               String defaultDir = getDefaultFileSelection();
               File dir = new File(defaultDir != null ? defaultDir : "");
               if (dir.isFile() || dir.isDirectory())
                  dialog.setFilterPath(defaultDir);
               else
                  dialog.setFilterPath("c:\\");

               String result = dialog.open();
               if (result != null && !result.equals("")) {
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
