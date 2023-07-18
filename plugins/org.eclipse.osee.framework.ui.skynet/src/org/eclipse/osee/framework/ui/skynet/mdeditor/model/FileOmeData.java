/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.mdeditor.model;

import java.io.File;
import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XText;
import org.eclipse.osee.framework.ui.ws.AWorkspace;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Donald G. Dunne
 */
public class FileOmeData extends AbstractOmeData {

   private final FileEditorInput editorInput;
   private XText editText;

   public FileOmeData(FileEditorInput editorInput) {
      this.editorInput = editorInput;
   }

   @Override
   public String getEditorName() {
      return "OME: " + editorInput.getFile().getName();
   }

   @Override
   public boolean isDirty() {
      return false;
   }

   @Override
   public void setWidget(XText editText) {
      this.editText = editText;
   }

   @Override
   public void doSave() {
      String value = editText.get();
      try {
         Lib.writeStringToFile(value, new File(editorInput.getFile().getFullPath().toOSString()));
      } catch (IOException ex) {
         System.err.println(Lib.exceptionToString(ex));
      }
   }

   @Override
   public void onSaveException(OseeCoreException ex) {
      XResultData rd = new XResultData();
      rd.logf("Save Exception\n\n%s", Lib.exceptionToString(ex));
      XResultDataUI.report(rd, "OME Save Editor");
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void load() {
      try {
         File file = AWorkspace.iFileToFile(editorInput.getFile());
         String value = Lib.fileToString(file);
         editText.set(value);
      } catch (IOException ex) {
         System.err.println(Lib.exceptionToString(ex));
      }
   }

   @Override
   public XText createXText(boolean enabled) {
      editText = new XText(enabled ? "Enter Markdown" : "Markdown (Read-Only)");
      return editText;
   }

   @Override
   public void uponCreate(XText editText) {
      // do nothing
   }

   @Override
   public boolean isEditable() {
      if (editable == null) {
         File file = AWorkspace.iFileToFile(editorInput.getFile());
         editable = file.canWrite();
      }
      return editable;
   }

}
