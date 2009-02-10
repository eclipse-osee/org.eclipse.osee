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
package org.eclipse.osee.ats.workflow.editor;

import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.workflow.ATSXWidgetOptionResolver;
import org.eclipse.osee.ats.workflow.AtsWorkPage;
import org.eclipse.osee.ats.workflow.editor.actions.EditAction;
import org.eclipse.osee.ats.workflow.editor.model.CancelledWorkPageShape;
import org.eclipse.osee.ats.workflow.editor.model.CompletedWorkPageShape;
import org.eclipse.osee.ats.workflow.editor.model.DefaultTransitionConnection;
import org.eclipse.osee.ats.workflow.editor.model.ReturnTransitionConnection;
import org.eclipse.osee.ats.workflow.editor.model.TransitionConnection;
import org.eclipse.osee.ats.workflow.editor.model.WorkPageShape;
import org.eclipse.osee.ats.workflow.editor.model.WorkflowDiagram;
import org.eclipse.osee.ats.workflow.editor.parts.ShapesEditPartFactory;
import org.eclipse.osee.ats.workflow.editor.parts.ShapesTreeEditPartFactory;
import org.eclipse.osee.framework.db.connection.exception.OseeArgumentException;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * A graphical editor for the configuration of ATS workflows
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkflowConfigEditor extends GraphicalEditorWithFlyoutPalette implements IFrameworkTransactionEventListener {

   /** This is the root of the editor's model. */
   private WorkflowDiagram diagram;
   /** Palette component, holding the tools and shapes. */
   private static PaletteRoot PALETTE_MODEL;
   public static String EDITOR_ID = "org.eclipse.osee.ats.workflow.editor.AtsWorkflowConfigEditor";

   /** Create a new ShapesEditor instance. This is called by the Workspace. */
   public AtsWorkflowConfigEditor() {
      setEditDomain(new DefaultEditDomain(this));
      OseeEventManager.addListener(this);
   }

   public static void editWorkflow(final WorkFlowDefinition workflow) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               AWorkbench.getActivePage().openEditor(new AtsWorkflowConfigEditorInput(workflow),
                     AtsWorkflowConfigEditor.EDITOR_ID);
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   /**
    * Configure the graphical viewer before it receives contents.
    * <p>
    * This is the place to choose an appropriate RootEditPart and EditPartFactory for your editor. The RootEditPart
    * determines the behavior of the editor's "work-area". For example, GEF includes zoomable and scrollable root edit
    * parts. The EditPartFactory maps model elements to edit parts (controllers).
    * </p>
    * 
    * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
    */
   @Override
   protected void configureGraphicalViewer() {
      super.configureGraphicalViewer();

      GraphicalViewer viewer = getGraphicalViewer();
      viewer.setEditPartFactory(new ShapesEditPartFactory());
      viewer.setRootEditPart(new ScalableFreeformRootEditPart());
      viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer));

      // configure the context menu provider
      ContextMenuProvider cmProvider = new AtsWorkflowConfigEditorContextMenuProvider(viewer, getActionRegistry());
      viewer.setContextMenu(cmProvider);
      getSite().registerContextMenu(cmProvider, viewer);
      AtsPlugin.getInstance().setHelp(viewer.getControl(), "atsConfigureWorkflow");

   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditor#commandStackChanged(java.util.EventObject)
    */
   @Override
   public void commandStackChanged(EventObject event) {
      firePropertyChange(IEditorPart.PROP_DIRTY);
      super.commandStackChanged(event);
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#createPaletteViewerProvider()
    */
   @Override
   protected PaletteViewerProvider createPaletteViewerProvider() {
      return new PaletteViewerProvider(getEditDomain()) {
         @Override
         protected void configurePaletteViewer(PaletteViewer viewer) {
            super.configurePaletteViewer(viewer);
            // create a drag source listener for this palette viewer
            // together with an appropriate transfer drop target listener, this will enable
            // model element creation by dragging a CombinatedTemplateCreationEntries 
            // from the palette into the editor
            // @see ShapesEditor#createTransferDropTargetListener()
            viewer.addDragSourceListener(new TemplateTransferDragSourceListener(viewer));
         }
      };
   }

   /**
    * Create a transfer drop target listener. When using a CombinedTemplateCreationEntry tool in the palette, this will
    * enable model element creation by dragging from the palette.
    * 
    * @see #createPaletteViewerProvider()
    */
   private TransferDropTargetListener createTransferDropTargetListener() {
      return new TemplateTransferDropTargetListener(getGraphicalViewer()) {
         @Override
         protected CreationFactory getFactory(Object template) {
            return new SimpleFactory((Class<?>) template);
         }
      };
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         Result result = diagram.doSave(transaction);
         if (result.isFalse()) {
            AWorkbench.popup("Save Error", result.getText());
            return;
         }
         transaction.execute();
         diagram.getWorkFlowDefinition().loadPageData(true);
         getCommandStack().markSaveLocation();

      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.ISaveablePart#doSaveAs()
    */
   @Override
   public void doSaveAs() {
      AWorkbench.popup("ERROR", "Not implemented yet");
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class type) {
      if (type == IContentOutlinePage.class) return new ShapesOutlinePage(new TreeViewer());
      return super.getAdapter(type);
   }

   WorkflowDiagram getModel() {
      return diagram;
   }

   /* (non-Javadoc)
    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#getPaletteRoot()
    */
   @Override
   protected PaletteRoot getPaletteRoot() {
      if (PALETTE_MODEL == null) PALETTE_MODEL = AtsWorkflowConfigEditorPaletteFactory.createPalette();
      return PALETTE_MODEL;
   }

   /**
    * Set up the editor's inital content (after creation).
    * 
    * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
    */
   @Override
   protected void initializeGraphicalViewer() {
      super.initializeGraphicalViewer();
      GraphicalViewer viewer = getGraphicalViewer();
      viewer.setContents(getModel()); // set the contents of this editor

      // listen for dropped parts
      viewer.addDropTargetListener(createTransferDropTargetListener());
      createActions();
   }

   @SuppressWarnings("unchecked")
   @Override
   protected void createActions() {
      super.createActions();
      ActionRegistry registry = getActionRegistry();
      IAction action;

      action = new EditAction();
      registry.registerAction(action);
      getStackActions().add(action.getId());
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
    */
   @Override
   public boolean isSaveAsAllowed() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
    */
   @Override
   protected void setInput(IEditorInput input) {
      super.setInput(input);
      if (input instanceof AtsWorkflowConfigEditorInput) {
         AtsWorkflowConfigEditorInput editorInput = (AtsWorkflowConfigEditorInput) input;
         WorkFlowDefinition workflowDef = editorInput.workflow;
         try {
            setPartName(workflowDef.getName());
            diagram = new WorkflowDiagram(workflowDef);
            int yLoc = 0;
            WorkPageDefinition startPage = workflowDef.getStartPage();
            if (startPage == null || startPage.equals("")) {
               throw new OseeArgumentException("StartPage null for workflow " + workflowDef);
            }
            // Create states
            List<WorkPageDefinition> pages = workflowDef.getPagesOrdered();
            for (WorkPageDefinition page : workflowDef.getPages()) {
               if (!pages.contains(page)) {
                  pages.add(page);
               }
            }
            for (WorkPageDefinition pageDef : pages) {
               WorkPageShape pageShape = null;
               if (pageDef.isCancelledPage()) {
                  pageShape = new CancelledWorkPageShape(pageDef);
                  pageShape.setLocation(new Point(250, 300));
               } else if (pageDef.isCompletePage()) {
                  pageShape = new CompletedWorkPageShape(pageDef);
                  pageShape.setLocation(new Point(50, yLoc += 90));
               } else {
                  pageShape = new WorkPageShape(pageDef);
                  pageShape.setLocation(new Point(50, yLoc += 90));
               }
               pageShape.setStartPage(startPage.getId().equals(pageShape.getId()) || pageShape.getId().endsWith(
                     startPage.getId()));
               diagram.addChild(pageShape);
            }

            // Create transitions
            for (WorkPageDefinition workPageDefinition : workflowDef.getPagesOrdered()) {
               WorkPageShape pageShape = getWorkPageShape(workPageDefinition);
               AtsWorkPage atsWorkPage =
                     new AtsWorkPage(workflowDef, workPageDefinition, null, ATSXWidgetOptionResolver.getInstance());
               // Handle to pages
               Set<WorkPageDefinition> toPages = new HashSet<WorkPageDefinition>();
               toPages.addAll(atsWorkPage.getToPages());
               List<WorkPageDefinition> returnPages = atsWorkPage.getReturnPages();
               for (WorkPageDefinition toPageDef : toPages) {
                  // Don't want to show return pages twice
                  if (returnPages.contains(toPageDef)) {
                     continue;
                  }
                  WorkPageShape toPageShape = getWorkPageShape(toPageDef);
                  if (toPageDef.equals(atsWorkPage.getDefaultToPage())) {
                     new DefaultTransitionConnection(pageShape, toPageShape);
                     //                  System.out.println("Default: " + atsWorkPage.getName() + " -> " + toPageShape.getName());
                  } else {
                     new TransitionConnection(pageShape, toPageShape);
                     //                  System.out.println("To: " + atsWorkPage.getName() + " -> " + toPageShape.getName());
                  }
               }
               // Handle return pages
               for (WorkPageDefinition toPageDef : returnPages) {
                  WorkPageShape toPageShape = getWorkPageShape(toPageDef);
                  new ReturnTransitionConnection(pageShape, toPageShape);
                  //               System.out.println("Return: " + atsWorkPage.getName() + " -> " + toPageShape.getName());
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }

      } else {
         throw new IllegalStateException("Invalid editor input");
      }

   }

   private WorkPageShape getWorkPageShape(WorkPageDefinition page) {
      for (Object object : getModel().getChildren()) {
         if (object instanceof WorkPageShape) {
            if (((WorkPageShape) object).getId().equals(page.getId()) || (page.getParentId() != null && ((WorkPageShape) object).getId().equals(
                  page.getParentId()))) {
               return (WorkPageShape) object;
            }
         }
      }
      return null;
   }
   /**
    * Creates an outline pagebook for this editor.
    */
   public class ShapesOutlinePage extends ContentOutlinePage {
      /**
       * Create a new outline page for the shapes editor.
       * 
       * @param viewer a viewer (TreeViewer instance) used for this outline page
       * @throws IllegalArgumentException if editor is null
       */
      public ShapesOutlinePage(EditPartViewer viewer) {
         super(viewer);
      }

      /* (non-Javadoc)
       * @see org.eclipse.ui.part.IPage#createControl(org.eclipse.swt.widgets.Composite)
       */
      @Override
      public void createControl(Composite parent) {
         // create outline viewer page
         getViewer().createControl(parent);
         // configure outline viewer
         getViewer().setEditDomain(getEditDomain());
         getViewer().setEditPartFactory(new ShapesTreeEditPartFactory());
         // configure & add context menu to viewer
         ContextMenuProvider cmProvider =
               new AtsWorkflowConfigEditorContextMenuProvider(getViewer(), getActionRegistry());
         getViewer().setContextMenu(cmProvider);
         getSite().registerContextMenu("org.eclipse.osee.ats.config.editor.contextmenu", cmProvider,
               getSite().getSelectionProvider());
         // hook outline viewer
         getSelectionSynchronizer().addViewer(getViewer());
         // initialize outline viewer with model
         getViewer().setContents(getModel());
         // show outline viewer
      }

      /* (non-Javadoc)
       * @see org.eclipse.ui.part.IPage#dispose()
       */
      @Override
      public void dispose() {
         // unhook outline viewer
         getSelectionSynchronizer().removeViewer(getViewer());
         // dispose
         super.dispose();
      }

      /* (non-Javadoc)
       * @see org.eclipse.ui.part.IPage#getControl()
       */
      @Override
      public Control getControl() {
         return getViewer().getControl();
      }

      /**
       * @see org.eclipse.ui.part.IPageBookViewPage#init(org.eclipse.ui.part.IPageSite)
       */
      @Override
      public void init(IPageSite pageSite) {
         super.init(pageSite);
         ActionRegistry registry = getActionRegistry();
         IActionBars bars = pageSite.getActionBars();
         String id = ActionFactory.UNDO.getId();
         bars.setGlobalActionHandler(id, registry.getAction(id));
         id = ActionFactory.REDO.getId();
         bars.setGlobalActionHandler(id, registry.getAction(id));
         id = ActionFactory.DELETE.getId();
         bars.setGlobalActionHandler(id, registry.getAction(id));
      }
   }

   public void closeEditor() {
      final IEditorPart editor = this;
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(editor, false);
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (transData.branchId != AtsPlugin.getAtsBranch().getBranchId()) return;
      for (Artifact delArt : transData.cacheDeletedArtifacts) {
         if (delArt.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
            if (delArt.getInternalAttributeValue("Name").equals(getPartName())) {
               closeEditor();
            }
         }
      }
      System.out.println("Add refresh of editor if workflow mod");
   }

}