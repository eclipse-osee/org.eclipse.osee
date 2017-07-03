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
package org.eclipse.osee.framework.ui.skynet.commandHandlers.renderer.handlers;

import java.util.HashMap;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.EditorSelectionDialog;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Roberto E. Escobar
 */
public class OtherEditorHandler extends AbstractEditorHandler {

   @Override
   public Object executeWithException(ExecutionEvent event, IStructuredSelection selection) throws OseeCoreException {
      if (!artifacts.isEmpty()) {
         EditorSelectionDialog dialog =
            new EditorSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
         dialog.setMessage(String.format("Choose the editor for opening %s", artifacts));

         NativeRenderer renderer = new NativeRenderer(new HashMap<RendererOption, Object>());

         if (dialog.open() == Window.OK) {
            IEditorDescriptor editor = dialog.getSelectedEditor();
            if (editor != null) {
               IFile file = renderer.renderToFile(artifacts, PresentationType.SPECIALIZED_EDIT);
               openEditor(editor, file, editor.isOpenExternal());
            }
         }
      }
      return null;
   }

   private void openEditor(IEditorDescriptor editor, IFile file, boolean openUsingDescriptor) {
      if (file == null) {
         return;
      }
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         if (openUsingDescriptor) {
            ((WorkbenchPage) page).openEditorFromDescriptor(new FileEditorInput(file), editor, true, null);
         } else {
            String editorId = editor == null ? IEditorRegistry.SYSTEM_EXTERNAL_EDITOR_ID : editor.getId();

            page.openEditor(new FileEditorInput(file), editorId, true,
               IWorkbenchPage.MATCH_INPUT | IWorkbenchPage.MATCH_ID);
            // only remember the default editor if the open succeeds
            IDE.setDefaultEditor(file, editorId);
         }
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Open Editor Error", ex);
      }
   }
}
