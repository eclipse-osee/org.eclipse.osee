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

package org.eclipse.osee.framework.ui.branch.graph.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.OrderedLayout;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;

/**
 * @author Roberto E. Escobar
 */
public class ContainerFigure extends Figure {

   public ContainerFigure() {
      ToolbarLayout layout = new ToolbarLayout();
      layout.setMinorAlignment(OrderedLayout.ALIGN_CENTER);
      layout.setStretchMinorAxis(false);
      layout.setSpacing(2);
      setLayoutManager(layout);
   }

   @Override
   public Dimension getPreferredSize(int wHint, int hHint) {
      Dimension size = super.getPreferredSize(wHint, hHint);
      size.height = Math.max(size.height, 10);
      return size;
   }
}
