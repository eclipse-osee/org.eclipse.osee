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
package org.eclipse.osee.coverage.test.import4.epu;

import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Donald G. Dunne
 */
public class PowerUnit1 extends Table {

   public Image image;

   /**
    * @param parent
    * @param style
    */
   public PowerUnit1(Composite parent, int style, Image image) {
      super(parent, style);
   }

   public Image getImage() {
      try {
         if (getStyle() == 4) { // 1, 1, TestUnit2
            return this.image; // 1, 2, n
         } else {
            return this.image; // 1, 3, TestUnit2
         }
      } catch (IllegalArgumentException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex); // 1, 4, n

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex); // 1, 5, n
      }
      return null; // 1, 6, n
   }

   @Override
   public void clear(int[] indices) {
      try {
         if (getStyle() == 4) { // 2, 1, TestUnit2
            System.out.println("clear it"); // 2, 2, n
         } else {
            for (int x = 0; x < 34; x++) {
               System.err.println("clear");// 2, 2, n
            }
         }
      } catch (IllegalArgumentException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex); // 2, 4, n

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex); // 2, 5, n
      }
   }

   @Override
   public void clearAll() {
      System.out.println("clear All"); // 3, 1, TestUnit2
   }

   @Override
   public Point computeSize(int wHint, int hHint, boolean changed) {
      if (getStyle() == 4) { // 4, 1, TestUnit2
         return new Point(3, 2); // 4, 2, n
      } else {
         return super.computeSize(wHint, hHint, changed); // 4, 3, TestUnit2
      }
   }

   @Override
   public void deselect(int start, int end) {
      super.deselect(start, end); // 5, 1, TestUnit2
   }

   public void deselectAdded(int start, int end) {
      super.deselect(start, end); // 6, 1, TestUnit5
   }

   @Override
   public TableColumn getColumn(int index) {
      return super.getColumn(index); // 7, 1, TestUnit2
   }

   @Override
   public int getColumnCount() {
      return super.getColumnCount(); // 8, 1, TestUnit2
   }

   public int initAdded() {
      return super.getColumnCount(); // 9, 1, TestUnit4
   }

}
