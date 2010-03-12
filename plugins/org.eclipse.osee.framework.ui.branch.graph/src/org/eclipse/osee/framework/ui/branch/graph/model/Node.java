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
package org.eclipse.osee.framework.ui.branch.graph.model;

import java.io.Serializable;

/**
 * @author Roberto E. Escobar
 */
public abstract class Node extends Model implements Serializable {

   private static final long serialVersionUID = -6334027982115287426L;

   private transient boolean isVisible;
   private transient boolean isSelected;
   private transient int index;

   public Node() {
      this.isVisible = true;
   }

   public boolean isVisible() {
      return isVisible;
   }

   public void setVisible(boolean isVisible) {
      this.isVisible = isVisible;
   }

   public boolean isSelected() {
      return isSelected;
   }

   public void setSelected(boolean selected) {
      this.isSelected = selected;
   }

   public int getIndex() {
      return index;
   }

   public void setIndex(int index) {
      this.index = index;
   }

}
