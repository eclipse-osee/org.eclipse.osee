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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;

/**
 * @author Roberto E. Escobar
 */
public class InheritanceEditPart extends ConnectionEditPart {

   public InheritanceEditPart(Object connectionModel) {
      super(connectionModel);
   }

   protected IFigure createFigure() {
      PolylineConnection connection = new PolylineConnection();
      PolygonDecoration decoration = new PolygonDecoration();
      //      decor.setScale(14, 6);
      decoration.setScale(7, 3);
      decoration.setBackgroundColor(ColorConstants.white);
      connection.setTargetDecoration(decoration);
      connection.setLineStyle(Graphics.LINE_DOT);
      return connection;
   }
}
