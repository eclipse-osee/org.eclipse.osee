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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.draw2d.parts.Thumbnail;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

/**
 * @author Roberto E. Escobar
 */
public class BranchGraphOutlinePage extends Page implements IContentOutlinePage {

   private Composite composite;
   private Canvas overview;
   private ScalableRootEditPart rootEditPart;
   private Thumbnail thumbnail;

   public BranchGraphOutlinePage(ScalableRootEditPart rootEditPart) {
      super();
      this.rootEditPart = rootEditPart;
   }

   public void addSelectionChangedListener(ISelectionChangedListener listener) {

   }

   public void createControl(Composite parent) {
      composite = new Composite(parent, SWT.BORDER);
      composite.setLayout(new FillLayout(SWT.VERTICAL));

      overview = new Canvas(composite, SWT.BORDER);
      overview.setBackground(ColorConstants.white);
      overview.setLayout(new FillLayout(SWT.VERTICAL));

      thumbnail = new ScrollableThumbnail((Viewport) rootEditPart.getFigure());
      thumbnail.setSource(rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS));

      LightweightSystem lws = new LightweightSystem();
      lws.setControl(overview);
      lws.setContents(thumbnail);
   }

   public void dispose() {
      if (null != thumbnail) thumbnail.deactivate();
      super.dispose();
   }

   public Control getControl() {
      return composite;
   }

   public ISelection getSelection() {
      return StructuredSelection.EMPTY;
   }

   public void removeSelectionChangedListener(ISelectionChangedListener listener) {

   }

   public void setFocus() {
      if (getControl() != null) getControl().setFocus();
   }

   public void setSelection(ISelection selection) {

   }

   public void setTreeContent(Object object) {
      //      viewer.setInput(object != null ? object : "Data not available");
   }

   public void init(IPageSite pageSite) {
      super.init(pageSite);
   }

}