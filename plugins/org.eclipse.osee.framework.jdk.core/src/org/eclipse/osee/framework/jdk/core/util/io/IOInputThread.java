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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class IOInputThread extends Thread {

   private Reader input;
   private BufferedWriter output;

   /**
    * 
    */
   public IOInputThread(Reader input, BufferedWriter output) {
      super();
      this.output = output;
      this.input = input;
   }

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
