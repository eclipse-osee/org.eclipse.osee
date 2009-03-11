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
package org.eclipse.osee.framework.ui.data.model.editor.wizard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Lib;
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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class FileOrFolderSelectPanel extends Composite {

   public static enum ButtonType {
      CHECK_BOX, RADIO;
   }

   private final boolean isFolderSelect;
   private final String label;
   private final String[] filterExtensions;
   private final ListenerRelay eventRelay;
   private final ButtonType buttonType;
   private final Set<Listener> listeners;
   private final int fileSelectStyle;

   private boolean isSelected;
   private Button button;
   private String resource;
   private String defaultFileName;

   public static FileOrFolderSelectPanel createFileSelectPanel(Composite parent, int style, String label, int fileSelectStyle, ButtonType buttonType, String[] filterExtensions) {
      return new FileOrFolderSelectPanel(parent, style, label, fileSelectStyle, buttonType, false, filterExtensions);
   }

   public static FileOrFolderSelectPanel createFolderSelectPanel(Composite parent, int style, String label, int fileSelectStyle, ButtonType buttonType) {
      return new FileOrFolderSelectPanel(parent, style, label, fileSelectStyle, buttonType, true, null);
   }

   private FileOrFolderSelectPanel(Composite parent, int style, String label, int fileSelectStyle, ButtonType buttonType, boolean isFolderSelect, String[] filterExtensions) {
      super(parent, style);
      this.fileSelectStyle = fileSelectStyle;
      this.isSelected = false;
      this.buttonType = buttonType;
      this.label = label;
      this.filterExtensions = filterExtensions;
      this.listeners = Collections.synchronizedSet(new HashSet<Listener>());
      this.eventRelay = new ListenerRelay();
      this.isFolderSelect = isFolderSelect;

      GridLayout layout = new GridLayout(3, false);
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      this.setLayout(layout);
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      createControl(this);
   }

   private void createControl(Composite composite) {
      button = new Button(composite, buttonType == ButtonType.CHECK_BOX ? SWT.CHECK : SWT.RADIO);
      button.setText(label);
      button.setSelection(isSelected);

      final Text text = new Text(composite, SWT.BORDER | SWT.READ_ONLY);
      GridData gridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
      gridData.widthHint = 100;
      text.setLayoutData(gridData);

      final Button fileDialogButton = new Button(composite, SWT.PUSH);
      fileDialogButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(
            isFolderSelect ? ISharedImages.IMG_OBJ_FOLDER : ISharedImages.IMG_OBJ_FILE));
      fileDialogButton.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            String result = null;
            if (isFolderSelect) {
               DirectoryDialog directoryDialog = new DirectoryDialog(getShell(), fileSelectStyle);
               if (Strings.isValid(resource)) {
                  directoryDialog.setFilterPath(resource);
               } else {
                  if (Strings.isValid(defaultFileName)) {
                     directoryDialog.setFilterPath(defaultFileName);
                  }
               }
               result = directoryDialog.open();
            } else {
               FileDialog fileDialog = new FileDialog(getShell(), fileSelectStyle);
               if (filterExtensions != null && filterExtensions.length > 0) {
                  fileDialog.setFilterExtensions(filterExtensions);
               }
               if (Strings.isValid(resource)) {
                  fileDialog.setFilterPath(Lib.removeExtension(resource));
               }

               if (Strings.isValid(resource)) {
                  fileDialog.setFileName(Lib.removeExtension(resource));
               } else {
                  if (Strings.isValid(defaultFileName)) {
                     fileDialog.setFileName(defaultFileName);
                  }
               }
               fileDialog.setOverwrite(true);
               result = fileDialog.open();
            }
            if (Strings.isValid(result)) {
               resource = result;
               text.setText(resource);
            }
         }
      });

      button.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            isSelected = button.getSelection();
            text.setEnabled(isSelected);
            fileDialogButton.setEnabled(isSelected);
         }
      });
      text.addListener(SWT.Modify, eventRelay);
      fileDialogButton.addListener(SWT.Selection, eventRelay);
      button.addListener(SWT.Selection, eventRelay);
   }

   public void setSelected(boolean setSelected) {
      isSelected = setSelected;
      if (button != null && !button.isDisposed()) {
         button.setSelection(isSelected);
      }
   }

   public boolean isSelected() {
      return isSelected;
   }

   public String getSelectedResource() {
      return resource;
   }

   public void setResource(String resource) {
      this.resource = resource;
   }

   public void setDefaultFileName(String defaultFileName) {
      this.defaultFileName = defaultFileName;
   }

   public boolean isFile() {
      return !isFolderSelect;
   }

   public void addListener(Listener listener) {
      synchronized (listeners) {
         listeners.add(listener);
      }
   }

   public void removeListener(Listener listener) {
      synchronized (listeners) {
         listeners.remove(listener);
      }
   }

   private final class ListenerRelay implements Listener {

      /* (non-Javadoc)
       * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
       */
      @Override
      public void handleEvent(Event event) {
         synchronized (listeners) {
            for (Listener listener : listeners) {
               listener.handleEvent(event);
            }
         }
      }

   }
}
