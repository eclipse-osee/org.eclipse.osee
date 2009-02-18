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
package org.eclipse.osee.framework.ui.data.model.editor.part;

import java.util.List;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.osee.framework.ui.data.model.editor.figure.CompartmentFigure;
import org.eclipse.osee.framework.ui.data.model.editor.part.ArtifactEditPart.ArtifactInternalsModel;

/**
 * @author Roberto E. Escobar
 */
public class InternalArtifactEditPart extends AbstractGraphicalEditPart {

   public InternalArtifactEditPart(Object model) {
      super();
      setModel(model);
   }

   protected void createEditPolicies() {
   }

   protected IFigure createFigure() {
      return new CompartmentFigure();
   }

   protected List getModelChildren() {
      if (getModel() instanceof ArtifactInternalsModel) {
         return ((ArtifactInternalsModel) getModel()).getChildren();
      } else {
         return (List) getModel();
      }
   }

   public boolean isSelectable() {
      return false;
   }
}
