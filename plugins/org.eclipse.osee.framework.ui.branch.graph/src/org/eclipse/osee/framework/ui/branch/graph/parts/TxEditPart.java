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

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.SelectionEditPolicy;
import org.eclipse.osee.framework.ui.branch.graph.figure.TxFigure;
import org.eclipse.osee.framework.ui.branch.graph.model.TxModel;
import org.eclipse.osee.framework.ui.branch.graph.utility.GraphFigureConstants;

/**
 * @author Roberto E. Escobar
 */
public class TxEditPart extends AbstractGraphicalEditPart {

   protected IFigure createFigure() {
      IFigure figure = new Panel();
      figure.setLayoutManager(new BorderLayout());

      TxModel txModel = (TxModel) getModel();
      GraphEditPart graphEditPart = (GraphEditPart) getParent().getParent();
      TxFigure txFigure = graphEditPart.getTxFigure(txModel);

      figure.add(txFigure, BorderLayout.CENTER);

      Rectangle rect =
            new Rectangle(GraphFigureConstants.TX_X_OFFSET + GraphFigureConstants.PLUS_MINUS_PADDING * 2,
                  10 + GraphFigureConstants.BRANCH_HEIGHT + txModel.getIndex() * GraphFigureConstants.TX_Y_OFFSET,
                  GraphFigureConstants.TX_WIDTH, GraphFigureConstants.TX_HEIGHT);
      ((AbstractGraphicalEditPart) getParent()).getFigure().getLayoutManager().setConstraint(txFigure, rect);

      return txFigure;
   }

   protected void refreshVisuals() {
      getFigure().setSize(GraphFigureConstants.TX_WIDTH, GraphFigureConstants.TX_HEIGHT);
      TxModel txModel = (TxModel) getModel();
      GraphEditPart graphEditPart = (GraphEditPart) getParent().getParent();
      TxFigure txFigure = graphEditPart.getTxFigure(txModel);
      txFigure.setSelected(getSelected() != SELECTED_NONE);
      graphEditPart.setConnectionVisibility();
   }

   protected void createEditPolicies() {
      installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new SelectionEditPolicy() {
         protected void hideSelection() {
            refreshVisuals();
         }

         protected void showSelection() {
            refreshVisuals();
         }
      });
   }

}