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
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * TODO It appears that this class is not being used.
 * 
 * @author Donald G. Dunne
 */
public class AEditor {

   /**
    * Jump to line number of an iFile
    */
   public static void goToLine(IFile targetIFile, int lineNumber) {
      if (targetIFile == null || targetIFile.equals("")) {
         return;
      }
      lineNumber = (lineNumber >= 0) ? --lineNumber : -1;
      final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

      IEditorPart editor = page.getActiveEditor();
      if (editor != null) {
         IEditorInput input = editor.getEditorInput();
         if (input instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) input).getFile();
            if (targetIFile.equals(file)) {
               page.activate(editor);
            }
         }
      }

      try {
         // if jarIFile is java file,
         String editorId = IDE.getEditorDescriptor(targetIFile).getId();
         editor = IDE.openEditor(page, targetIFile, editorId);
         IEditorInput editorInput = editor.getEditorInput();
         if (editor instanceof AbstractDecoratedTextEditor) {
            ITextEditor textEditor = (ITextEditor) editor;
            IDocument document = textEditor.getDocumentProvider().getDocument(editorInput);
            IRegion lineInformation = document.getLineInformation(lineNumber);
            textEditor.selectAndReveal(lineInformation.getOffset(), 0);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static boolean openEditor(String filename) {
      IFile iFile = AWorkspace.getIFile(filename);
      return openEditor(iFile);
   }

   public static boolean openEditor(IFile iFile) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         IDE.openEditor(page, iFile, true);
      } catch (PartInitException e) {
         e.printStackTrace();
         return false;
      }
      return true;
   }

   public static IFile getActiveEditorIFile() {
      IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      if (workbenchWindow != null) {
         IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
         if (workbenchPage != null) {
            IEditorPart editorPart = workbenchPage.getActiveEditor();
            if (editorPart != null) {
               IEditorInput editorInput = editorPart.getEditorInput();
               if (editorInput != null) {
                  if (editorInput instanceof IFileEditorInput) return ((IFileEditorInput) editorInput).getFile();
               }
            }
         }
      }
      return null;
   }

   public static IEditorReference[] getEditorReferences() {
      IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      if (workbenchWindow != null) {
         IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
         if (workbenchPage != null) {
            return workbenchPage.getEditorReferences();
         }
      }
      return null;
   }

   /**
    * @return line number of selected text in active editor
    */
   public static ITextSelection getSelectedText() {
      IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
      if (window != null) {
         IWorkbenchPage page = window.getActivePage();
         IEditorPart activeEditorPart = page.getActiveEditor();
         if (activeEditorPart != null) {
            IEditorSite editorSite = page.getActiveEditor().getEditorSite();
            ISelectionProvider selectionProvider = editorSite.getSelectionProvider();
            if (selectionProvider != null) {
               ISelection selection = selectionProvider.getSelection();
               if (selection instanceof ITextSelection) {
                  return (ITextSelection) selection;
               }
            }
         }
      }
      return null;
   }

   /**
    * @return line number of selected text in active editor
    */
   public static int getLineNumber() {
      return getSelectedText().getStartLine() + 1;
   }

}
