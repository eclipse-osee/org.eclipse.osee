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
package org.eclipse.osee.framework.ui.branch.graph.utility;

/**
 * @author Roberto E. Escobar
 */
public class GraphFigureConstants {
   public final static int GRAPH_MARGIN = 10;

   public final static int BRANCH_WIDTH = 220;
   public final static int BRANCH_HEIGHT = 30;
   public final static int BRANCH_PADDING = 20;
   public final static int BRANCH_X_OFFSET = BRANCH_WIDTH + BRANCH_PADDING;
   public final static int BRANCH_Y_OFFSET = BRANCH_HEIGHT + BRANCH_PADDING;

   public final static int TX_WIDTH = 50;
   public final static int TX_HEIGHT = 30;
   public final static int TX_PADDING = 20;

   public final static int TX_X_OFFSET = (BRANCH_WIDTH - TX_WIDTH) / 2;
   public final static int TX_Y_OFFSET = TX_HEIGHT + TX_PADDING;

   public final static int NOTE_BORDER_WIDTH = 5;
   public final static int PLUS_MINUS_PADDING = 5;

   private GraphFigureConstants() {
   }
}
