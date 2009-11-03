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
package org.eclipse.osee.coverage.test.import1.apu;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

/**
 * @author Donald G. Dunne
 */
public class AuxPowerUnit2 extends Table {

   public Image image;

   /**
    * @param parent
    * @param style
    */
   public AuxPowerUnit2(Composite parent, int style, Image image) {
      super(parent, style);
   }

   // NOTE: This method will be duplicated to show error case; duplicated via SampleJavaFileParser
   @Override
   public void clear(int[] indices) {
      if (getStyle() == 4) { // 1, 1, TestUnit2
         System.out.println("clear it"); // 1, 2, n
      } else {
         for (int x = 0; x < 34; x++) {
            System.err.println("clear");// 1, 3, n
         }
      }
   }

}
