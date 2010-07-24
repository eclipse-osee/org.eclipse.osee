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
package org.eclipse.osee.framework.skynet.core.utility;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class IncrementingNum {
   private static int atsDevNum;

   /**
    * Get an incrementing number. This number only resets on new workspace creation. Should not be used for anything but
    * developmental purposes.
    */
   public static int get() {
      try {
         File numFile = OseeData.getFile("atsDevNum.txt");
         if (numFile.exists() && atsDevNum == 0) {
            try {
               atsDevNum = new Integer(AFile.readFile(numFile).replaceAll("\\s", ""));
            } catch (NumberFormatException ex) {
            } catch (NullPointerException ex) {
            }
         }
         atsDevNum++;
         Lib.writeStringToFile(String.valueOf(atsDevNum), numFile);
         return atsDevNum;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return 99;
   }

}
