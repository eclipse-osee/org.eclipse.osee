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
package org.eclipse.osee.coverage.import12.ase;

import java.util.logging.Level;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class Survive1 extends Button {

   public Image image;

   public Survive1(Composite parent, int style, Image image) {
      super(parent, style);
   }

   @Override
   public String getText() {
      try {
         if (getStyle() == 4) { // 1, 1, TestUnit2
            return "Navigate Here"; // 1, 2, TestUnit2
         } else {
            return "Navigate There"; // 1, 3, TestUnit2
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex); // 1, 4, n
      }
      return "Navigate";
   }

}
