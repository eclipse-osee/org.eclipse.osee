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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.osee.framework.ui.branch.graph.model.BranchModel;
import org.eclipse.osee.framework.ui.branch.graph.model.TxModel;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphFigureConstants;

/**
 * @author Roberto E. Escobar
 */
public class BranchEditPart extends AbstractGraphicalEditPart {

   @Override
   protected IFigure createFigure() {
      BranchModel branchModel = (BranchModel) getModel();
      GraphEditPart gPart = (GraphEditPart) getParent();

      IFigure txContainer = new Figure();
      txContainer.setBackgroundColor(ColorConstants.white);
      txContainer.setOpaque(true);

      XYLayout layout = new XYLayout();
      txContainer.setLayoutManager(layout);

      int height = branchModel.areTxsVisible() ? -1 : GraphFigureConstants.BRANCH_HEIGHT;
      Point point = getBranchFigureLocation(branchModel, gPart);
      Rectangle rect = new Rectangle(point.x, point.y, GraphFigureConstants.BRANCH_WIDTH, height);

      IFigure parentFigure = gPart.getFigure();
      parentFigure.getLayoutManager().setConstraint(txContainer, rect);

      return txContainer;
   }

   @Override
   @SuppressWarnings({"unchecked", "rawtypes"})
   protected List getModelChildren() {
      List toReturn = new ArrayList();

      BranchModel model = (BranchModel) getModel();
      toReturn.add(model.getBranch());

      if (model.areTxsVisible()) {
         int index = 0;
         for (TxModel node : model.getTxs()) {
            node.setIndex(index++);
            toReturn.add(node);
         }
      }
      return toReturn;
   }

   private Point getBranchFigureLocation(BranchModel branchModel, GraphEditPart gPart) {

      int graphLevel = branchModel.getDepth();

      int branchesAboveLevel = 0;
      for (int index = 0; index < graphLevel; index++) {
         branchesAboveLevel += gPart.getNumberOfBranchesAtGraphLevel(index);
      }
      int branchAtLevel = gPart.getNumberOfBranchesAtGraphLevel(graphLevel);

      int maxBranches = gPart.getMaxNumberOfBranchesAtAnyLevel();

      int xMiddle = maxBranches * GraphFigureConstants.BRANCH_X_OFFSET / 2;

      int xStart = xMiddle + (branchAtLevel - 1) * GraphFigureConstants.BRANCH_X_OFFSET / 2 * -1;

      xStart +=
         GraphFigureConstants.GRAPH_MARGIN + (branchModel.getIndex() - branchesAboveLevel) * GraphFigureConstants.BRANCH_X_OFFSET;

      int totalTxs = 0;
      for (int index = 0; index < graphLevel; index++) {
         totalTxs += gPart.getMaxTxForGraphLevel(index);
      }
      int yStart =
         GraphFigureConstants.GRAPH_MARGIN + GraphFigureConstants.BRANCH_Y_OFFSET * graphLevel + GraphFigureConstants.TX_Y_OFFSET * totalTxs;

      return new Point(xStart, yStart);
   }

   //   private Point getBalancedTreeLocation(BranchModel branchModel, GraphEditPart gPart) {
   //      int graphLevel = branchModel.getDepth();
   //
   //      //         int numberOfChildrenAtLevel = branchModel.getParentBranch().getChildren().size();
   //
   //      int maxDepth = branchModel.getDepth();
   //      for (BranchModel model : branchModel.getAllChildrenBelow()) {
   //         maxDepth = Math.max(model.getDepth(), maxDepth);
   //      }
   //      int maxChildrenAtAnyLevel = branchModel.getAllChildrenBelow().size();
   //      maxChildrenAtAnyLevel = maxChildrenAtAnyLevel / (maxDepth - branchModel.getDepth());
   //
   //      int branchesAboveLevel = 0;
   //      for (int index = 0; index < graphLevel; index++) {
   //         branchesAboveLevel += gPart.getNumberOfBranchesAtGraphLevel(index);
   //      }
   //      int branchAtLevel = gPart.getNumberOfBranchesAtGraphLevel(graphLevel);
   //
   //      int maxBranches = gPart.getMaxNumberOfBranchesAtAnyLevel();
   //
   //      int xMiddle = maxBranches * GraphFigureConstants.BRANCH_X_OFFSET / 2;
   //
   //      int xStart = xMiddle + (((branchAtLevel - 1) * GraphFigureConstants.BRANCH_X_OFFSET) / 2) * -1;
   //
   //      xStart +=
   //            GraphFigureConstants.GRAPH_MARGIN + (branchModel.getIndex() - branchesAboveLevel) * GraphFigureConstants.BRANCH_X_OFFSET;
   //
   //      int totalTxs = 0;
   //      for (int index = 0; index < graphLevel; index++) {
   //         totalTxs += gPart.getMaxTxForGraphLevel(index);
   //      }
   //      int yStart =
   //            GraphFigureConstants.GRAPH_MARGIN + (GraphFigureConstants.BRANCH_Y_OFFSET * graphLevel) + (GraphFigureConstants.TX_Y_OFFSET * totalTxs);
   //
   //      return new Point(xStart, yStart);
   //   }

   @Override
   protected void createEditPolicies() {
      installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SelectionEditPolicy() {
         @Override
         protected void hideSelection() {
            refreshVisuals();
         }

         @Override
         protected void showSelection() {
            refreshVisuals();
         }
      });
   }
}
