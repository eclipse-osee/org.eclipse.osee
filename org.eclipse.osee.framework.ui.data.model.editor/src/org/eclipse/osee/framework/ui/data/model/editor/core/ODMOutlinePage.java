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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.ui.data.model.editor.outline.OutlineTreePartFactory;
import org.eclipse.osee.framework.ui.data.model.editor.utility.ODMImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Roberto E. Escobar
 */
public class ODMOutlinePage extends Page implements IContentOutlinePage {

   private Composite composite;
   private Canvas overview;
   private ScalableFreeformRootEditPart rootEditPart;
   private Thumbnail thumbnail;
   private TreeViewer viewer;
   private ActionRegistry registry;

   public ODMOutlinePage(ScalableFreeformRootEditPart rootEditPart, ActionRegistry registry) {
      super();
      this.rootEditPart = rootEditPart;
      this.registry = registry;
   }

   public void addSelectionChangedListener(ISelectionChangedListener listener) {

   }

   public void createControl(Composite parent) {
      composite = new SashForm(parent, SWT.BORDER);
      composite.setLayout(new FillLayout(SWT.VERTICAL));

      SashForm sash = (SashForm) composite;
      sash.setOrientation(SWT.VERTICAL);

      overview = new Canvas(composite, SWT.BORDER);
      overview.setBackground(ColorConstants.white);
      overview.setLayout(new FillLayout(SWT.VERTICAL));

      thumbnail = new ScrollableThumbnail((Viewport) rootEditPart.getFigure());
      thumbnail.setBorder(new MarginBorder(3));
      thumbnail.setSource(rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS));

      LightweightSystem lws = new LightweightSystem(overview);
      lws.setContents(thumbnail);

      Group panel = new Group(composite, SWT.NONE);
      panel.setLayout(new GridLayout());
      panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      panel.setText("Hierarchy");

      viewer = new TreeViewer();
      viewer.createControl(panel);
      viewer.setEditDomain(new EditDomain());
      viewer.setEditPartFactory(new OutlineTreePartFactory());
      //      viewer.setContents);
      //      viewer.addDragSourceListener(new OutlineDragSourceListener(viewer));
      sash.setWeights(new int[] {2, 8});
   }

   public void setContents(Object contents) {
      viewer.setContents(contents);
   }

   public void dispose() {
      if (null != thumbnail) {
         thumbnail.deactivate();
      }
      super.dispose();
   }

   public Control getControl() {
      return overview;
   }

   public ISelection getSelection() {
      return StructuredSelection.EMPTY;
   }

   public void removeSelectionChangedListener(ISelectionChangedListener listener) {

   }

   public void setFocus() {
      if (getControl() != null) {
         getControl().setFocus();
      }
   }

   public void setSelection(ISelection selection) {
   }

   public void init(IPageSite pageSite) {
      super.init(pageSite);

      IActionBars bars = pageSite.getActionBars();
      String id = ActionFactory.UNDO.getId();
      bars.setGlobalActionHandler(id, registry.getAction(id));
      id = ActionFactory.REDO.getId();
      bars.setGlobalActionHandler(id, registry.getAction(id));
      bars.updateActionBars();

      IToolBarManager manager = bars.getToolBarManager();
      ImageDescriptor img = ImageManager.getImageDescriptor(ODMImage.EXPAND_ALL);
      IAction action = new Action("Expand All", img) {
         public void run() {
            expand(((Tree) viewer.getControl()).getItems());
         }

         private void expand(TreeItem[] items) {
            for (int i = 0; i < items.length; i++) {
               expand(items[i].getItems());
               items[i].setExpanded(true);
            }
         }
      };
      action.setToolTipText("Expand All");
      manager.add(action);
   }
}