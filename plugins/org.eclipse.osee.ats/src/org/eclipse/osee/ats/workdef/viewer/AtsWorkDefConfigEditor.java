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
package org.eclipse.osee.ats.workdef.viewer;

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
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workdef.viewer.model.DefaultTransitionConnection;
import org.eclipse.osee.ats.workdef.viewer.model.ReturnTransitionConnection;
import org.eclipse.osee.ats.workdef.viewer.model.StateDefShape;
import org.eclipse.osee.ats.workdef.viewer.model.TransitionConnection;
import org.eclipse.osee.ats.workdef.viewer.model.WorkDefinitionDiagram;
import org.eclipse.osee.ats.workdef.viewer.parts.ShapesEditPartFactory;
import org.eclipse.osee.ats.workdef.viewer.parts.ShapesTreeEditPartFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * A graphical editor for the configuration of ATS workflows<br>
 * <REM2>
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkDefConfigEditor extends GraphicalEditorWithFlyoutPalette {

   /** This is the root of the editor's model. */
   private WorkDefinitionDiagram diagram;
   /** Palette component, holding the tools and shapes. */
   private static PaletteRoot PALETTE_MODEL;
   public static String EDITOR_ID = "org.eclipse.osee.ats.workdef.viewer.AtsWorkflowConfigEditor";

   /** Create a new ShapesEditor instance. This is called by the Workspace. */
   public AtsWorkDefConfigEditor() {
      setEditDomain(new DefaultEditDomain(this));
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
      ContextMenuProvider cmProvider = new AtsWorkDefConfigEditorContextMenuProvider(viewer, getActionRegistry());
      viewer.setContextMenu(cmProvider);
      getSite().registerContextMenu(cmProvider, viewer);

   }

   @Override
   public void commandStackChanged(EventObject event) {
      firePropertyChange(IEditorPart.PROP_DIRTY);
      super.commandStackChanged(event);
   }

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

   @Override
   public void doSave(IProgressMonitor monitor) {
      // do nothing
   }

   @Override
   public void doSaveAs() {
      // do nothing
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public Object getAdapter(Class type) {
      if (type != null && type.isAssignableFrom(IContentOutlinePage.class)) {
         return new ShapesOutlinePage(new TreeViewer());
      }
      return super.getAdapter(type);
   }

   WorkDefinitionDiagram getModel() {
      return diagram;
   }

   @Override
   protected PaletteRoot getPaletteRoot() {
      if (PALETTE_MODEL == null) {
         PALETTE_MODEL = AtsWorkDefConfigEditorPaletteFactory.createPalette(this);
      }
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

   @Override
   public boolean isSaveAsAllowed() {
      return true;
   }

   @Override
   protected void setInput(IEditorInput input) {
      super.setInput(input);
      if (input instanceof AtsWorkDefConfigEditorInput) {
         AtsWorkDefConfigEditorInput editorInput = (AtsWorkDefConfigEditorInput) input;
         IAtsWorkDefinition workflowDef = editorInput.workflow;
         try {
            setPartName(workflowDef.getName());
            diagram = new WorkDefinitionDiagram(workflowDef);
            int yLocNormalState = 20;
            int yLocCancelledState = 40;
            IAtsStateDefinition startPage = workflowDef.getStartState();
            if (startPage == null || startPage.getName().equals("")) {
               throw new OseeArgumentException("StartPage null for workflow " + workflowDef);
            }
            // Create states
            List<IAtsStateDefinition> stateDefs =
               AtsClientService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(workflowDef);
            for (IAtsStateDefinition stateDef : workflowDef.getStates()) {
               if (!stateDefs.contains(stateDef)) {
                  stateDefs.add(stateDef);
               }
            }
            for (IAtsStateDefinition pageDef : stateDefs) {
               StateDefShape stateShape = new StateDefShape(pageDef);
               if (pageDef.getStateType().isCancelledState()) {
                  stateShape.setLocation(new Point(350, yLocCancelledState));
                  yLocCancelledState += 90;
               } else {
                  stateShape.setLocation(new Point(50, yLocNormalState));
                  yLocNormalState += 90;
               }
               diagram.addChild(stateShape);
            }

            // Create transitions
            for (IAtsStateDefinition stateDef : AtsClientService.get().getWorkDefinitionService().getStatesOrderedByOrdinal(
               workflowDef)) {
               StateDefShape pageShape = getStateDefShape(stateDef);
               // Handle to pages
               Set<IAtsStateDefinition> toPages = new HashSet<>();
               toPages.addAll(pageShape.getStateDefinition().getToStates());
               List<IAtsStateDefinition> returnStateDefs =
                  pageShape.getStateDefinition().getOverrideAttributeValidationStates();
               for (IAtsStateDefinition toStateDef : toPages) {
                  // Don't want to show return pages twice
                  if (returnStateDefs.contains(toStateDef)) {
                     continue;
                  }
                  StateDefShape toStateDefShape = getStateDefShape(toStateDef);
                  if (toStateDef.equals(stateDef.getDefaultToState())) {
                     new DefaultTransitionConnection(pageShape, toStateDefShape);
                     //                  System.out.println("Default: " + atsWorkPage.getName() + " -> " + toPageShape.getName());
                  } else {
                     new TransitionConnection(pageShape, toStateDefShape);
                     //                  System.out.println("To: " + atsWorkPage.getName() + " -> " + toPageShape.getName());
                  }
               }
               // Handle return pages
               for (IAtsStateDefinition toPageDef : returnStateDefs) {
                  StateDefShape toPageShape = getStateDefShape(toPageDef);
                  new ReturnTransitionConnection(pageShape, toPageShape);
                  //               System.out.println("Return: " + atsWorkPage.getName() + " -> " + toPageShape.getName());
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }

      } else {
         throw new IllegalStateException("Invalid editor input");
      }

   }

   private StateDefShape getStateDefShape(IAtsStateDefinition stateDef) {
      for (Object object : getModel().getChildren()) {
         if (object instanceof StateDefShape) {
            if (((StateDefShape) object).getId().equals(stateDef.getName())) {
               return (StateDefShape) object;
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
       * @param viewer a viewer (TreeViewer instance) used for this outline page if editor is null
       */
      public ShapesOutlinePage(EditPartViewer viewer) {
         super(viewer);
      }

      @Override
      public void createControl(Composite parent) {
         // create outline viewer page
         getViewer().createControl(parent);
         // configure outline viewer
         getViewer().setEditDomain(getEditDomain());
         getViewer().setEditPartFactory(new ShapesTreeEditPartFactory());
         // configure & add context menu to viewer
         ContextMenuProvider cmProvider =
            new AtsWorkDefConfigEditorContextMenuProvider(getViewer(), getActionRegistry());
         getViewer().setContextMenu(cmProvider);
         getSite().registerContextMenu("org.eclipse.osee.ats.config.editor.contextmenu", cmProvider,
            getSite().getSelectionProvider());
         // hook outline viewer
         getSelectionSynchronizer().addViewer(getViewer());
         // initialize outline viewer with model
         getViewer().setContents(getModel());
         // show outline viewer
      }

      @Override
      public void dispose() {
         // unhook outline viewer
         getSelectionSynchronizer().removeViewer(getViewer());
         // dispose
         super.dispose();
      }

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
         @Override
         public void run() {
            if (AWorkbench.getActivePage() != null) {
               AWorkbench.getActivePage().closeEditor(editor, false);
            }
         }
      });
   }

   @Override
   public boolean isDirty() {
      return false;
   }

}