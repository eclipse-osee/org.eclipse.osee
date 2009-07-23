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
package org.eclipse.osee.framework.jdk.core.util;

import java.io.File;
import java.io.IOException;

/**
 * @author Donald G. Dunne
 */
public class AFile {

   public static String justFilename(String filename) {
      File file = new File(filename);
      return file.getName();
   }

   public static String justPath(String filename) {
      File file = new File(filename);
      filename = filename.replaceAll(file.getName(), "");
      return filename;
   }

   /**
    * Use the Lib method directly - the original implementation of this method was not memory efficient and suppressed
    * exceptions
    * 
    * @param stream
    */
   public static String readFile(String filename) {
      return readFile(new File(filename));
   }

   /**
    * Use the Lib method directly - the original implementation of this method was not memory efficient and suppressed
    * exceptions
    * 
    * @param stream
    */
   public static String readFile(File file) {
      try {
         return Lib.fileToString(file);
      } catch (IOException ex) {
         ex.printStackTrace();
         return null;
      }
   }
}