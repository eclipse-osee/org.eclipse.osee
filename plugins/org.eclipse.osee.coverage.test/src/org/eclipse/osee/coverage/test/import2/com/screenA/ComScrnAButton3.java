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
package org.eclipse.osee.coverage.test.import2.com.screenA;

import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class ComScrnAButton3 extends Button {

   public Image image;

   /**
    * @param parent
    * @param style
    */
   public ComScrnAButton3(Composite parent, int style, Image image) {
      super(parent, style);
   }

   @Override
   public Image getImage() {
      try {
         if (getStyle() == 4) { // 1, 1, TestUnit1|TestUnit3
            return this.image; // 1, 2, n
         } else {
            return super.getImage(); // 1, 3, TestUnit3
         }
      } catch (IllegalArgumentException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex); // 1, 4, n

      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex); // 1, 5, n
      }
      return null; // 1, 6, n
   }

}
