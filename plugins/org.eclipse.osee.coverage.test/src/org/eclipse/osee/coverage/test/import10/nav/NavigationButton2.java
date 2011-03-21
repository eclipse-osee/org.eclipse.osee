/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.test.import10.nav;

import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class NavigationButton2 extends Button {

   public Image image;

   public NavigationButton2(Composite parent, int style, Image image) {
      super(parent, style);
   }

   @Override
   public String getText() {
      try {
         if (getStyle() == 4) { // 1, 1, TestUnit2
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex); // 1, 2, n
      }
      //    - If coverage item CM is a custom disposition and import is Not_Covered, DO NOT overwrite coverage item
      // Note: CoveragePackage will have to be changed programatically in test for this case cause can't import disposition item
      // Note: This line was n in import09
      // After test setup, stored coverage package, this line will be Deactivated_Code
      // After import10, this line will be Deactivated_Code (no change from test setup)
      return "Navigate"; // 1, 3, n
   }

   @Override
   public Image getImage() {
      try {
         if (getStyle() == 4) { // 2, 1, TestUnit2
            //   - If coverage item CM is Test_Unit and import item is Not_Covered, overwrite with Not_Covered
            // Note: This line was TestUnit2 in import09
            // After test setup, stored coverage package will be TestUnit2 (no change from import09)
            // After import10, this line will be Not_Covered (one less # covered item, one less Test_Unit, one more Not_Covered)
            return this.image; // 2, 2, n
         } else {
            //   - If coverage item CM is a custom disposition and import is Test_Unit, overwrite with Test_Unit and clear rationale
            // Note: CoveragePackage will have to be changed programatically in test for this case cause can't import disposition item
            // Note: This line was TestUnit2 in import09
            // After test setup, stored coverage package, this line will be Deactivated_Code
            // After import10, this line will be Test_Unit, (same # covered items, one less Deactived_Code, one more Test_Unit)
            return super.getImage(); // 2, 3, TestUnit2
         }
      } catch (IllegalArgumentException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex); // 2, 4, n
      }
      return null; // 2, 5, TestUnit2
   }

}
