/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
