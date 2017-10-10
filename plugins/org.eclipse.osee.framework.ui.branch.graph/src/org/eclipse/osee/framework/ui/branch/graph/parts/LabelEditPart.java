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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

/**
 * @author Roberto E. Escobar
 */
public class LabelEditPart extends AbstractGraphicalEditPart {

   private final String data;

   public LabelEditPart(String data) {
      this.data = data;
   }

   @Override
   protected IFigure createFigure() {
      return new Label(data);
   }

   @Override
   protected void createEditPolicies() {
      // do nothing
   }

}
