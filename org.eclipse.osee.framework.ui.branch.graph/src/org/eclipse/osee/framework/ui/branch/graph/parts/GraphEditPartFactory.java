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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.branch.graph.model.BranchModel;
import org.eclipse.osee.framework.ui.branch.graph.model.GraphCache;
import org.eclipse.osee.framework.ui.branch.graph.model.TxModel;

/**
 * @author Roberto E. Escobar
 */
public class GraphEditPartFactory implements EditPartFactory {

   private GraphicalViewer viewer;

   public GraphEditPartFactory(GraphicalViewer viewer) {
      this.viewer = viewer;
   }

   public EditPart createEditPart(EditPart context, Object model) {
      EditPart editPart = null;
      if (model instanceof String) {
         editPart = new LabelEditPart((String) model);
      } else if (model instanceof GraphCache) {
         editPart = new GraphEditPart(viewer);
      } else if (model instanceof BranchModel) {
         editPart = new BranchEditPart();
      } else if (model instanceof Branch) {
         editPart = new BranchDataEditPart();
      } else if (model instanceof TxModel) {
         editPart = new TxEditPart();
      }
      if (editPart == null) {
         throw new RuntimeException(String.format("Error no EditPart defined for: [%s]", model.getClass().getName()));
      } else {
         editPart.setModel(model);
      }
      return editPart;
   }

}
