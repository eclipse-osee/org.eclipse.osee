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
package org.eclipse.osee.framework.ui.jdk.swing;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * @author Robert A. Fisher
 */
public class EasyGridConstraint extends GridBagConstraints {
   private static final long serialVersionUID = -1745538537160837428L;

   public EasyGridConstraint setGrid(int x, int y) {
      this.gridx = x;
      this.gridy = y;

      return this;
   }

   public EasyGridConstraint setHeight(int height) {
      this.gridheight = height;

      return this;
   }

   public EasyGridConstraint setWidth(int width) {
      this.gridwidth = width;

      return this;
   }

   public EasyGridConstraint setWeight(double x, double y) {
      this.weightx = x;
      this.weighty = y;

      return this;
   }

   public EasyGridConstraint setXPadding(int x) {
      this.ipadx = x;

      return this;
   }

   public EasyGridConstraint setYPadding(int y) {
      this.ipadx = y;

      return this;
   }

   public EasyGridConstraint setXInsets(int x) {
      this.insets.left = x;
      this.insets.right = x;

      return this;
   }

   public EasyGridConstraint setYInsets(int y) {
      this.insets.top = y;
      this.insets.bottom = y;

      return this;
   }

   public EasyGridConstraint setInsets(int top, int left, int bottom, int right) {
      this.insets.top = top;
      this.insets.left = left;
      this.insets.bottom = bottom;
      this.insets.right = right;

      return this;
   }

   public EasyGridConstraint fillBoth() {
      this.fill = GridBagConstraints.BOTH;

      return this;
   }

   public EasyGridConstraint fillVert() {
      this.fill = GridBagConstraints.VERTICAL;

      return this;
   }

   public EasyGridConstraint fillHorz() {
      this.fill = GridBagConstraints.HORIZONTAL;

      return this;
   }

   public EasyGridConstraint anchorNorth() {
      this.anchor = GridBagConstraints.NORTH;

      return this;
   }

   public EasyGridConstraint anchorNorthEast() {
      this.anchor = GridBagConstraints.NORTHEAST;

      return this;
   }

   public EasyGridConstraint anchorNorthWest() {
      this.anchor = GridBagConstraints.NORTHWEST;

      return this;
   }

   public EasyGridConstraint anchorSouth() {
      this.anchor = GridBagConstraints.SOUTH;

      return this;
   }

   public EasyGridConstraint anchorSouthEast() {
      this.anchor = GridBagConstraints.SOUTHEAST;

      return this;
   }

   public EasyGridConstraint anchorSouthWest() {
      this.anchor = GridBagConstraints.SOUTHWEST;

      return this;
   }

   public EasyGridConstraint anchorEast() {
      this.anchor = GridBagConstraints.EAST;

      return this;
   }

   public EasyGridConstraint anchorWest() {
      this.anchor = GridBagConstraints.WEST;

      return this;
   }

   public static GridBagConstraints setConstraints(int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, char fill, String anchor) {
      return setConstraints(gridx, gridy, weightx, weighty, gridwidth, gridheight, fill, anchor, 0, 0, null);
   }

   public static GridBagConstraints setConstraints(int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, char fill, String anchor, Insets insets) {
      return setConstraints(gridx, gridy, weightx, weighty, gridwidth, gridheight, fill, anchor, 0, 0, insets);
   }

   /**
    * A wrapper for setting the constraints of a GridBag object.
    * 
    * @param gridx Specifies the cell at the left of the component's display area, where the leftmost cell has gridx =
    *           0.
    * @param gridy Specifies the cell at the top of the component's display area, where the topmost cell has gridy = 0.
    * @param weightx Specifies how to distribute extra horizontal space.
    * @param weighty Specifies how to distribute extra vertical space.
    * @param gridwidth Specifies the number of cells in a row for the component's display area.
    * @param gridheight Specifies the number of cells in a column for the component's display area.
    * @param fill This field is used when the component's display area is larger than the component's requested size.
    * @param anchor This field is used when the component is smaller than its display area.
    * @param insets Specifies the external padding of the component -- the minimum amount of space between the component
    *           and the edges of its display area.
    * @param ipadx Specifies the component's horizontal internal padding
    * @param ipady Specifies the component's vertical internal padding
    * @return java.awt.GridBagConstraints
    */
   public static GridBagConstraints setConstraints(int gridx, int gridy, double weightx, double weighty, int gridwidth, int gridheight, char fill, String anchor, int ipadx, int ipady, Insets insets) {
      // gridx, gridy, weightx, weighty, gridwidth, gridheight, char fill, String anchor, insets, ipadx, ipady

      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = gridx;
      gbc.gridy = gridy;
      gbc.weightx = weightx;
      gbc.weighty = weighty;
      gbc.gridwidth = gridwidth;
      gbc.gridheight = gridheight;
      gbc.ipadx = ipadx;
      gbc.ipady = ipady;
      if (insets != null) {
         gbc.insets = insets;
      }

      switch (fill) {
         case 'V':
            gbc.fill = GridBagConstraints.VERTICAL;
            break;
         case 'H':
            gbc.fill = GridBagConstraints.HORIZONTAL;
            break;
         case 'B':
            gbc.fill = GridBagConstraints.BOTH;
            break;
      }

      if (anchor.equals("N")) {
         gbc.anchor = GridBagConstraints.NORTH;
      } else if (anchor.equals("NE")) {
         gbc.anchor = GridBagConstraints.NORTHEAST;
      } else if (anchor.equals("E")) {
         gbc.anchor = GridBagConstraints.EAST;
      } else if (anchor.equals("SE")) {
         gbc.anchor = GridBagConstraints.SOUTHEAST;
      } else if (anchor.equals("S")) {
         gbc.anchor = GridBagConstraints.SOUTH;
      } else if (anchor.equals("SW")) {
         gbc.anchor = GridBagConstraints.SOUTHWEST;
      } else if (anchor.equals("W")) {
         gbc.anchor = GridBagConstraints.WEST;
      } else if (anchor.equals("NW")) {
         gbc.anchor = GridBagConstraints.NORTHWEST;
      }
      return gbc;
   }

}
