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
package org.eclipse.osee.framework.ui.branch.graph.core;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteCustomizer;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.ui.branch.graph.Activator;
import org.eclipse.osee.framework.ui.branch.graph.model.GraphCache;
import org.eclipse.osee.framework.ui.branch.graph.operation.LoadGraphOperation;
import org.eclipse.osee.framework.ui.branch.graph.parts.GraphEditPartFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Roberto E. Escobar
 */
public class BranchGraphEditor extends GraphicalEditorWithFlyoutPalette {

   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.branch.graph.BranchGraphEditor";

   private BranchGraphOutlinePage overviewOutlinePage;
   private ActionRegistry actionRegistry;
   private BranchGraphPaletteProvider paletteProvider;
   private KeyHandler shareKeyHandler;

   public BranchGraphEditor() {
      setEditDomain(new DefaultEditDomain(this));
   }

   @Override
   public ActionRegistry getActionRegistry() {
      if (actionRegistry == null) {
         actionRegistry = new ActionRegistry();
      }
      return actionRegistry;
   }

   @Override
   public void setFocus() {
      Control control = overviewOutlinePage.getControl();
      if (control != null) {
         control.setFocus();
      }
   }

   public void showGraphFor(BranchGraphEditorInput editorInput) {
      setPartName(editorInput.getName() + " Graph");
      LoadGraphOperation task =
         new LoadGraphOperation(getSite().getPart(), getGraphicalViewer(), this, editorInput.getBranch());
      Jobs.runInJob(task.getName(), task, Activator.class, Activator.PLUGIN_ID, true);
   }

   @SuppressWarnings("rawtypes")
   @Override
   public Object getAdapter(Class adapter) {
      if (adapter == GraphicalViewer.class || adapter == EditPartViewer.class) {
         return getGraphicalViewer();
      } else if (adapter == ZoomManager.class) {
         return ((ScalableRootEditPart) getGraphicalViewer().getRootEditPart()).getZoomManager();
      } else if (adapter == IContentOutlinePage.class) {
         return getOverviewOutlinePage();
      }
      return super.getAdapter(adapter);
   }

   public void refresh() {
      getGraphicalViewer().setContents("Loading graph... This can take several minutes");
      showGraphFor((BranchGraphEditorInput) getEditorInput());
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      OseeLog.log(BranchGraphEditor.class, Level.WARNING, "method not implemented!");
   }

   @Override
   public void doSaveAs() {
      OseeLog.log(BranchGraphEditor.class, Level.WARNING, "method not implemented!");
   }

   @Override
   public void init(IEditorSite site, IEditorInput input) {
      setSite(site);
      setInput(input);
   }

   @Override
   public boolean isDirty() {
      return false;
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   public GraphicalViewer getViewer() {
      return getGraphicalViewer();
   }

   private BranchGraphOutlinePage getOverviewOutlinePage() {
      if (null == overviewOutlinePage && null != getGraphicalViewer()) {
         RootEditPart rootEditPart = getGraphicalViewer().getRootEditPart();
         if (rootEditPart instanceof ScalableRootEditPart) {
            overviewOutlinePage = new BranchGraphOutlinePage((ScalableRootEditPart) rootEditPart);
         }
      }
      return overviewOutlinePage;
   }

   @SuppressWarnings("deprecation")
   @Override
   protected void configureGraphicalViewer() {
      super.configureGraphicalViewer();

      GraphicalViewer viewer = getGraphicalViewer();
      viewer.setRootEditPart(new ScalableRootEditPart());
      viewer.setEditPartFactory(new GraphEditPartFactory(viewer));

      getSite().setSelectionProvider(viewer);

      viewer.setContents("Loading graph... This can take several minutes");

      ContextMenuProvider cmProvider = new BranchGraphEditorContextMenuProvider(viewer, this);
      viewer.setContextMenu(cmProvider);

      IEditorInput input = getEditorInput();
      if (input instanceof BranchGraphEditorInput) {
         BranchGraphEditorInput editorInput = (BranchGraphEditorInput) input;
         showGraphFor(editorInput);
      }

      ZoomManager zoomManager = ((ScalableRootEditPart) viewer.getRootEditPart()).getZoomManager();
      IAction zoomIn = new ZoomInAction(zoomManager);
      IAction zoomOut = new ZoomOutAction(zoomManager);
      getActionRegistry().registerAction(zoomIn);
      getActionRegistry().registerAction(zoomOut);

      viewer.setKeyHandler(getCommonKeyHandler());

      // Scroll-wheel Zoom
      viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);

      //      IHandlerService accessControlService = (IHandlerService) getSite().getService(IHandlerService.class);
      //      accessControlService.activateHandler(zoomIn.getActionDefinitionId(), new ActionHandler(zoomIn));
      //      accessControlService.activateHandler(zoomOut.getActionDefinitionId(), new ActionHandler(zoomOut));
      getSite().getKeyBindingService().registerAction(zoomIn);
      getSite().getKeyBindingService().registerAction(zoomOut);
      List<String> zoomContributions =
         Arrays.asList(new String[] {ZoomManager.FIT_ALL, ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH});
      zoomManager.setZoomLevelContributions(zoomContributions);

      try {
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(
            "org.eclipse.ui.views.ContentOutline");
      } catch (PartInitException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      //      viewer.setHelpContext(MAIN_HELP_CONTEXT);
   }

   protected KeyHandler getCommonKeyHandler() {
      if (shareKeyHandler == null) {
         shareKeyHandler = new GraphicalViewerKeyHandler(getViewer());
         shareKeyHandler.put(KeyStroke.getPressed(SWT.F2, 0),
            getActionRegistry().getAction(GEFActionConstants.DIRECT_EDIT));
      }
      return shareKeyHandler;
   }

   @Override
   protected void initializeGraphicalViewer() {
      OseeLog.log(BranchGraphEditor.class, Level.WARNING, "method not implemented!");
   }

   @Override
   protected PaletteRoot getPaletteRoot() {
      if (paletteProvider == null) {
         paletteProvider = new BranchGraphPaletteProvider(this);
      }
      return paletteProvider.getPaletteRoot();
   }

   @Override
   protected PaletteViewerProvider createPaletteViewerProvider() {
      return new PaletteViewerProvider(getEditDomain()) {
         @Override
         protected void configurePaletteViewer(PaletteViewer viewer) {
            super.configurePaletteViewer(viewer);
            viewer.setCustomizer(new PaletteCustomizer() {
               @Override
               public void revertToSaved() {
                  // do nothing
               }

               @Override
               public void save() {
                  // do nothing
               }
            });
            viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
         }
      };
   }

   @Override
   protected CustomPalettePage createPalettePage() {
      return new CustomPalettePage(getPaletteViewerProvider()) {
         @Override
         public void init(IPageSite pageSite) {
            super.init(pageSite);
            IAction copy = getActionRegistry().getAction(ActionFactory.COPY.getId());
            pageSite.getActionBars().setGlobalActionHandler(ActionFactory.COPY.getId(), copy);
         }
      };
   }

   public void setOutlineContent(GraphCache graph) {
      getOverviewOutlinePage().setTreeContent(graph);
   }
}
