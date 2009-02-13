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
package org.eclipse.osee.framework.ui.data.model.editor;

import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.CopyTemplateAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteCustomizer;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.osee.framework.ui.data.model.editor.operation.ODMLoadGraphRunnable;
import org.eclipse.osee.framework.ui.data.model.editor.part.ODMEditPartFactory;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Roberto E. Escobar
 */
public class ODMEditor extends GraphicalEditorWithFlyoutPalette {

   public static String EDITOR_ID = "org.eclipse.osee.framework.ui.data.model.editor.ODMEditor";

   public static final String FILTER_CONNECTIONS = "ODM.filter.connections";

   private ODMPalette editorPalette;
   private ActionRegistry actionRegistry;
   private ThumbnailOutlinePage overviewOutlinePage;

   public ODMEditor() {
      super();
      setEditDomain(new ODMEditDomain(this));
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
    */
   @Override
   protected PaletteRoot getPaletteRoot() {
      if (editorPalette == null) {
         editorPalette = new ODMPalette(this);
      }
      return editorPalette.getPaletteRoot();
   }

   public void updatePalette() {
      PaletteViewer paletteViewer = getPaletteViewerProvider().getEditDomain().getPaletteViewer();

      PaletteCustomizer paletteCustomizer = paletteViewer.getCustomizer();
      paletteCustomizer.save();

      editorPalette.updatePaletteRoot();

      //      ((ODMPalette)getPaletteRoot().a.updatePaletteRoot();
      //
      //      List<PaletteEntry> entries = null;
      //      for (PaletteEntry entry : entries) {
      //         if (paletteCustomizer.canDelete(entry)) {
      //            paletteCustomizer.performDelete(entry);
      //         }
      //      }
      //
      //      editorPalette.updatePaletteRoot();
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
      }
      return super.getAdapter(adapter);
   }

   @Override
   protected void configureGraphicalViewer() {
      super.configureGraphicalViewer();

      GraphicalViewer viewer = getGraphicalViewer();
      viewer.setRootEditPart(new ScalableFreeformRootEditPart());
      viewer.setEditPartFactory(new ODMEditPartFactory(this));

      viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));
      viewer.addDropTargetListener(createTransferDropTargetListener());
      createActions();

      viewer.setContents("Loading graph... This can take several minutes");

      // configure the context menu provider
      ContextMenuProvider cmProvider = new ODMEditorContextMenuProvider(viewer, getActionRegistry());
      viewer.setContextMenu(cmProvider);
      getSite().registerContextMenu(cmProvider, viewer);

      IEditorInput input = getEditorInput();
      if (input instanceof ODMEditorInput) {
         ODMEditorInput editorInput = (ODMEditorInput) input;
         showGraphFor(editorInput);
      }

      // zoom stuff
      ZoomManager zoomManager = ((ScalableFreeformRootEditPart) viewer.getRootEditPart()).getZoomManager();
      IAction zoomIn = new ZoomInAction(zoomManager);
      IAction zoomOut = new ZoomOutAction(zoomManager);
      getActionRegistry().registerAction(zoomIn);
      getActionRegistry().registerAction(zoomOut);

      // keyboard
      getSite().getKeyBindingService().registerAction(zoomIn); // deprecated
      getSite().getKeyBindingService().registerAction(zoomOut); // deprecated
      List<String> zoomContributions =
            Arrays.asList(new String[] {ZoomManager.FIT_ALL, ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH});
      zoomManager.setZoomLevelContributions(zoomContributions);
      // mouse wheel
      viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);

   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
    */
   @Override
   protected void initializeGraphicalViewer() {
      super.initializeGraphicalViewer();
   }

   /**
    * Create a transfer drop target listener. When using a CombinedTemplateCreationEntry tool in the palette, this will
    * enable model element creation by dragging from the palette.
    * 
    * @see #createPaletteViewerProvider()
    */
   private TransferDropTargetListener createTransferDropTargetListener() {
      return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
         @SuppressWarnings("unchecked")
         @Override
         protected CreationFactory getFactory(Object template) {
            return new SimpleFactory((Class) template);
         }
      };
   }

   private ThumbnailOutlinePage getOverviewOutlinePage() {
      if (null == overviewOutlinePage && null != getGraphicalViewer()) {
         RootEditPart rootEditPart = getGraphicalViewer().getRootEditPart();
         if (rootEditPart instanceof ScalableRootEditPart) {
            overviewOutlinePage = new ThumbnailOutlinePage((ScalableRootEditPart) rootEditPart);
         }
      }
      return overviewOutlinePage;
   }

   public ActionRegistry getActionRegistry() {
      if (actionRegistry == null) actionRegistry = new ActionRegistry();
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
         private IMenuListener menuListener;

         protected void configurePaletteViewer(PaletteViewer viewer) {
            super.configurePaletteViewer(viewer);
            viewer.setCustomizer(new ODMPaletteCustomizer());
            viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
         }

         protected void hookPaletteViewer(PaletteViewer viewer) {
            super.hookPaletteViewer(viewer);
            final CopyTemplateAction copy =
                  (CopyTemplateAction) getActionRegistry().getAction(ActionFactory.COPY.getId());
            viewer.addSelectionChangedListener(copy);
            if (menuListener == null) menuListener = new IMenuListener() {
               public void menuAboutToShow(IMenuManager manager) {
                  manager.appendToGroup(GEFActionConstants.GROUP_COPY, copy);
               }
            };
            viewer.getContextMenu().addMenuListener(menuListener);
         }
      };
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPalettePage()
    */
   @Override
   protected CustomPalettePage createPalettePage() {
      return new CustomPalettePage(getPaletteViewerProvider()) {
         public void init(IPageSite pageSite) {
            super.init(pageSite);
            IAction copy = getActionRegistry().getAction(ActionFactory.COPY.getId());
            pageSite.getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
         }
      };
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.EditorPart#getEditorInput()
    */
   @Override
   public ODMEditorInput getEditorInput() {
      return (ODMEditorInput) super.getEditorInput();
   }

}