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
package org.eclipse.osee.framework.ui.branch.graph.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * @author Roberto E. Escobar
 */
public class ContainerFigure extends Figure {

   public ContainerFigure() {
      ToolbarLayout layout = new ToolbarLayout();
      layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
      layout.setStretchMinorAxis(false);
      layout.setSpacing(2);
      setLayoutManager(layout);
   }

   public Dimension getPreferredSize(int wHint, int hHint) {
      Dimension size = super.getPreferredSize(wHint, hHint);
      size.height = Math.max(size.height, 10);
      return size;
   }
}
