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
package org.eclipse.osee.framework.ui.branch.graph.parts;

import org.eclipse.draw2d.ActionEvent;
import org.eclipse.draw2d.ActionListener;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.ui.branch.graph.figure.BranchFigure;
import org.eclipse.osee.framework.ui.branch.graph.figure.PlusMinus;
import org.eclipse.osee.framework.ui.branch.graph.model.GraphCache;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphFigureConstants;

/**
 * @author Roberto E. Escobar
 */
public class BranchDataEditPart extends AbstractGraphicalEditPart {

   @Override
   protected IFigure createFigure() {
      BranchId branch = (BranchId) getModel();
      GraphEditPart graphEditPart = (GraphEditPart) getParent().getParent();
      BranchFigure branchFigure = graphEditPart.getFigure(branch);

      Panel figure = new Panel();
      figure.setLayoutManager(new BorderLayout());
      figure.setBackgroundColor(ColorConstants.white);
      figure.setOpaque(true);

      Panel container = new Panel();
      container.setOpaque(true);
      GridLayout layout = new GridLayout();
      layout.verticalSpacing = GraphFigureConstants.PLUS_MINUS_PADDING;
      layout.horizontalSpacing = GraphFigureConstants.PLUS_MINUS_PADDING;
      container.setLayoutManager(layout);

      PlusMinus control = new PlusMinus();
      control.setPreferredSize(9, 9);
      container.add(control);
      control.getModel().addActionListener(new PlusMinusFigureMouseListener());
      figure.add(container, BorderLayout.LEFT);
      figure.add(branchFigure, BorderLayout.CENTER);
      return figure;
   }

   @Override
   protected void refreshVisuals() {
      getFigure().setSize(220, 30);
      super.refreshVisuals();
   }

   @Override
   protected void createEditPolicies() {
      // do nothing
   }

   private final class PlusMinusFigureMouseListener implements ActionListener {
      @Override
      public void actionPerformed(ActionEvent event) {
         BranchId branch = (BranchId) getModel();
         GraphEditPart graphEditPart = (GraphEditPart) getParent().getParent();
         GraphCache graphCache = (GraphCache) graphEditPart.getModel();
         //BranchModel model =
         graphCache.getBranchModel(branch);

         // TODO: prune the tree
         //         List<BranchModel> children = model.getChildren();
         //         if (children.size() > 0) {
         //            boolean isVisible = !children.get(0).isVisible();
         //            model.setTxsVisible(isVisible);
         //            for (BranchModel child : model.getAllChildrenBelow()) {
         //               child.setVisible(isVisible);
         //            }
         //            viewer.setContents(graphCache);
         //         }
      }
   }
}
