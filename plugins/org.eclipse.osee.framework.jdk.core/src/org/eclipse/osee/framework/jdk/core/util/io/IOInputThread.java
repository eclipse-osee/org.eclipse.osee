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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class IOInputThread extends Thread {

   private final Reader input;
   private final BufferedWriter output;

   public IOInputThread(Reader input, BufferedWriter output) {
      super();
      this.output = output;
      this.input = input;
   }

   @Override
   public void run() {
      int character;
      try {
         //*
         while ((character = input.read()) != -1) {
            output.write(character);
            output.flush();
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      } finally {
         try {
            input.close();
            //output.flush();
         } catch (IOException ex) {
            ex.printStackTrace();
         }
      }
   }
}
