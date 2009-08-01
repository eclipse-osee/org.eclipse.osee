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

import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
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
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (!artifacts.isEmpty()) {
         try {
            EditorSelectionDialog dialog =
                  new EditorSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
            dialog.setMessage(String.format("Choose the editor for opening %s", artifacts));

            NativeRenderer renderer = new NativeRenderer();

            //            String dummyName = renderer.getAssociatedExtension(artifacts.iterator().next());
            //            IEditorDescriptor[] editorDescriptors = getEditorDescriptorFilters(dummyName);
            //            if (editorDescriptors != null) {
            //               //               dialog.setEditorsToFilter(editorDescriptors);
            //            }
            if (dialog.open() == Window.OK) {
               IEditorDescriptor editor = dialog.getSelectedEditor();
               if (editor != null) {
                  IFile file = renderer.getRenderedFileForOpen(artifacts);
                  openEditor(editor, file, editor.isOpenExternal());
               }
            }
            dispose();
         } catch (OseeCoreException ex) {
            OseeLog.log(WordEditorHandler.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   //   private IEditorDescriptor[] getEditorDescriptorFilters(String name) {
   //      IEditorDescriptor[] toReturn;
   //      IEditorDescriptor[] editorDescriptors = PlatformUI.getWorkbench().getEditorRegistry().getEditors(name);
   //      if (editorDescriptors != null) {
   //         Set<IEditorDescriptor> allEditors = new HashSet<IEditorDescriptor>();
   //         EditorRegistry registry = (EditorRegistry) PlatformUI.getWorkbench().getEditorRegistry();
   //         allEditors.addAll(Arrays.asList(registry.getSortedEditorsFromPlugins()));
   //         allEditors.addAll(Arrays.asList(registry.getSortedEditorsFromOS()));
   //         List<IEditorDescriptor> filter = Collections.setComplement(allEditors, Arrays.asList(editorDescriptors));
   //         toReturn = filter.toArray(new IEditorDescriptor[filter.size()]);
   //      } else {
   //         toReturn = new IEditorDescriptor[0];
   //      }
   //      return toReturn;
   //   }

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
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, "Open Editor Error", ex);
      }
   }
}
