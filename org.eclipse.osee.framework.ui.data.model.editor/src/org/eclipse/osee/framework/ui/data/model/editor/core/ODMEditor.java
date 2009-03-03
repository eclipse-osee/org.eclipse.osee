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
package org.eclipse.osee.framework.ui.data.model.editor.core;

import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteCustomizer;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.osee.framework.ui.data.model.editor.action.ODMExportAction;
import org.eclipse.osee.framework.ui.data.model.editor.action.ODMImportAction;
import org.eclipse.osee.framework.ui.data.model.editor.command.DeleteCommand;
import org.eclipse.osee.framework.ui.data.model.editor.operation.ODMLoadGraphRunnable;
import org.eclipse.osee.framework.ui.data.model.editor.part.ODMEditPartFactory;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditor extends GraphicalEditorWithFlyoutPalette {

   public static String EDITOR_ID = "org.eclipse.osee.framework.ui.data.model.editor.ODMEditor";

   private ODMPaletteFactory editorPalette;
   private ActionRegistry actionRegistry;
   private ODMOutlinePage overviewOutlinePage;
   private KeyHandler shareKeyHandler;

   public ODMEditor() {
      super();
      setEditDomain(new DefaultEditDomain(this));
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
    */
   @Override
   protected PaletteRoot getPaletteRoot() {
      if (editorPalette == null) {
         editorPalette = new ODMPaletteFactory(this);
      }
      return editorPalette.getPaletteRoot();
   }

   public void updatePalette() {
      editorPalette.updatePaletteRoot();
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditor#isSaveAsAllowed()
    */
   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditor#isDirty()
    */
   @Override
   public boolean isDirty() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == GraphicalViewer.class || adapter == EditPartViewer.class) {
         return getGraphicalViewer();
      } else if (adapter == ZoomManager.class) {
         return ((ScalableFreeformRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
      } else if (adapter == IContentOutlinePage.class) {
         return getOverviewOutlinePage();
      } else if (adapter == IActionable.class) {
         return new IActionable() {
            @Override
            public String getActionDescription() {
               return "ODM Editor description here";
            }
         };
      }
      return super.getAdapter(adapter);
   }

   @Override
   protected void configureGraphicalViewer() {
      super.configureGraphicalViewer();

      GraphicalViewer viewer = getGraphicalViewer();
      viewer.setRootEditPart(new ScalableFreeformRootEditPart());
      viewer.setEditPartFactory(new ODMEditPartFactory());

      getSite().setSelectionProvider(viewer);

      viewer.setContents("Loading graph... This can take several minutes");

      // configure the context menu provider
      ContextMenuProvider cmProvider = new ODMEditorContextMenuProvider(viewer, this);
      viewer.setContextMenu(cmProvider);

      IEditorInput input = getEditorInput();
      if (input instanceof ODMEditorInput) {
         ODMEditorInput editorInput = (ODMEditorInput) input;
         showGraphFor(editorInput);
      }

      ZoomManager zoomManager = ((ScalableFreeformRootEditPart) viewer.getRootEditPart()).getZoomManager();
      IAction zoomIn = new ZoomInAction(zoomManager);
      IAction zoomOut = new ZoomOutAction(zoomManager);
      getActionRegistry().registerAction(zoomIn);
      getActionRegistry().registerAction(zoomOut);
      getActionRegistry().registerAction(new ToggleSnapToGeometryAction(viewer));

      viewer.setKeyHandler(getCommonKeyHandler());

      // Scroll-wheel Zoom
      viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);

      // keyboard
      getSite().getKeyBindingService().registerAction(zoomIn); // deprecated
      getSite().getKeyBindingService().registerAction(zoomOut); // deprecated
      List<String> zoomContributions =
            Arrays.asList(new String[] {ZoomManager.FIT_ALL, ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH});
      zoomManager.setZoomLevelContributions(zoomContributions);

      //      viewer.addDropTargetListener((TransferDropTargetListener) new DiagramDropTargetListener(viewer));
      viewer.addDropTargetListener((TransferDropTargetListener) new ODMPaletteDropListener(viewer));
   }

   private KeyHandler getCommonKeyHandler() {
      if (shareKeyHandler == null) {
         shareKeyHandler = new GraphicalViewerKeyHandler(getViewer()) {
            @SuppressWarnings("unchecked")
            public boolean keyPressed(KeyEvent event) {
               if (event.keyCode == SWT.DEL) {
                  List objects = getGraphicalViewer().getSelectedEditParts();
                  if (objects == null || objects.isEmpty()) {
                     return true;
                  }
                  GroupRequest deleteReq = new GroupRequest(RequestConstants.REQ_DELETE);
                  deleteReq.getExtendedData().put(DeleteCommand.DELETE_FROM_ODM_DIAGRAM, Boolean.TRUE);
                  CompoundCommand compoundCmd = new CompoundCommand("Delete");
                  for (int i = 0; i < objects.size(); i++) {
                     EditPart object = (EditPart) objects.get(i);
                     Command cmd = object.getCommand(deleteReq);
                     if (cmd != null) {
                        compoundCmd.add(cmd);
                     }
                  }
                  getCommandStack().execute(compoundCmd);
                  return true;
               }
               return super.keyPressed(event);
            }
         };
         shareKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0), getActionRegistry().getAction(
               GEFActionConstants.DIRECT_EDIT));
      }
      return shareKeyHandler;
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
    */
   @Override
   protected void initializeGraphicalViewer() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util.EventObject)
    */
   @Override
   public void commandStackChanged(EventObject event) {
      firePropertyChange(IEditorPart.PROP_DIRTY);
      super.commandStackChanged(event);
   }

   private ODMOutlinePage getOverviewOutlinePage() {
      if (null == overviewOutlinePage && null != getGraphicalViewer()) {
         RootEditPart rootEditPart = getGraphicalViewer().getRootEditPart();
         if (rootEditPart instanceof ScalableFreeformRootEditPart) {
            overviewOutlinePage = new ODMOutlinePage((ScalableFreeformRootEditPart) rootEditPart, getActionRegistry());
         }
      }
      return overviewOutlinePage;
   }

   public ActionRegistry getActionRegistry() {
      if (actionRegistry == null) {
         actionRegistry = new ActionRegistry();
         actionRegistry.registerAction(new ODMImportAction(this));
         actionRegistry.registerAction(new ODMExportAction(this));
      }
      return actionRegistry;
   }

   public GraphicalViewer getViewer() {
      return getGraphicalViewer();
   }

   public void showGraphFor(ODMEditorInput editorInput) {
      setPartName(editorInput.getName() + " graph");
      ODMLoadGraphRunnable runnable = new ODMLoadGraphRunnable(getGraphicalViewer(), this, editorInput);
      Jobs.run(runnable.getName(), runnable, ODMEditorActivator.class, ODMEditorActivator.PLUGIN_ID, true);
   }

   /* (non-Javadoc)
   * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPaletteViewerProvider()
   */
   @Override
   protected PaletteViewerProvider createPaletteViewerProvider() {
      return new PaletteViewerProvider(getEditDomain()) {
         protected void configurePaletteViewer(PaletteViewer viewer) {
            super.configurePaletteViewer(viewer);
            viewer.setCustomizer(new PaletteCustomizer() {
               public void revertToSaved() {
               }

               public void save() {
               }
            });
            viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
         }

         //         protected void hookPaletteViewer(PaletteViewer viewer) {
         //            super.hookPaletteViewer(viewer);
         //            final CopyTemplateAction copy =
         //                  (CopyTemplateAction) getActionRegistry().getAction(ActionFactory.COPY.getId());
         //            viewer.addSelectionChangedListener(copy);
         //            if (menuListener == null) {
         //               menuListener = new IMenuListener() {
         //                  public void menuAboutToShow(IMenuManager manager) {
         //                     manager.appendToGroup(GEFActionConstants.GROUP_COPY, copy);
         //                  }
         //               };
         //            }
         //            viewer.getContextMenu().addMenuListener(menuListener);
         //         }
      };
   }

   //   /* (non-Javadoc)
   //    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPalettePage()
   //    */
   //   @Override
   //   protected CustomPalettePage createPalettePage() {
   //      return new CustomPalettePage(getPaletteViewerProvider()) {
   //         public void init(IPageSite pageSite) {
   //            super.init(pageSite);
   //            IAction copy = getActionRegistry().getAction(ActionFactory.COPY.getId());
   //            pageSite.getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
   //         }
   //      };
   //   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.EditorPart#getEditorInput()
    */
   @Override
   public ODMEditorInput getEditorInput() {
      return (ODMEditorInput) super.getEditorInput();
   }

}