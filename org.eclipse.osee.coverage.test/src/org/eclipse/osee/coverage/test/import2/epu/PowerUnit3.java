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
package org.eclipse.osee.coverage.test.import2.epu;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * @author Donald G. Dunne
 */
public class PowerUnit3 extends Table {

   public Image image;

   /**
    * @param parent
    * @param style
    */
   public PowerUnit3(Composite parent, int style, Image image) {
      super(parent, style);
   }

   @Override
   public void clearAll() {
      System.out.println("clear All"); // 1, 1, TestUnit2
   }

   @Override
   public Point computeSize(int wHint, int hHint, boolean changed) {
      if (getStyle() == 4) { // 2, 1, TestUnit2
         return new Point(3, 2); // 2, 2, n
      } else {
         return super.computeSize(wHint, hHint, changed); // 2, 3, TestUnit2
      }
   }

   @Override
   public int getColumnCount() {
      return super.getColumnCount(); // 3, 1, TestUnit2
   }

}
