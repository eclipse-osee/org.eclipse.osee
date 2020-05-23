/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class IOOutputThread extends Thread {

   private final Writer output;
   private final BufferedReader input;

   public IOOutputThread(Writer output, BufferedReader input) {
      this.output = output;
      this.input = input;
   }

   @Override
   public void run() {
      try {
         final char[] buffer = new char[4096];
         int size;
         while ((size = input.read(buffer)) != -1) {
            output.write(buffer, 0, size);
            output.flush();
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      } finally {
         try {
            input.close();
            output.flush();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }
}
