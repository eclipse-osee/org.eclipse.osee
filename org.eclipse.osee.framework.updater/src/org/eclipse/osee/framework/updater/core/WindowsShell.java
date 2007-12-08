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
package org.eclipse.osee.framework.updater.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

/*
 * This class wraps up cmds as .bat files and runs them... this allows us to determine when the .bat file is 
 * complete.... running some things as just a cmd doesn't give us a consistant indication
 * of when a cmd is completed.
 */
public class WindowsShell {//TODO move this to the correct project after the big structure refactor

   //   private PrintWriter writer;
   //   private StringWriter stringWriter;
   //   private int currentOutput = 0;

   private PrintStream ps;
   private final String prompt;
   private File wd;

   /*
    * don't cd it'll screw up
    */
   public WindowsShell(File workingDir) throws IOException {
      this.wd = workingDir;
      prompt = workingDir.getAbsolutePath() + ">";
      ProcessBuilder builder = new ProcessBuilder();
      builder.directory(workingDir);
      builder.command(new String[] {"cmd"});
      Process proc = builder.start();
      handleProcessOutput(proc);
      ps = new PrintStream(proc.getOutputStream());//.write("installdb.bat")
   }

   private void handleProcessOutput(Process proc) {
      final InputStream err = proc.getErrorStream();
      final InputStream out = proc.getInputStream();

      Thread errT = new Thread(new Runnable() {
         public void run() {
            processInputStream(err, System.err);
         }
      });
      Thread outT = new Thread(new Runnable() {
         public void run() {
            processInputStream(out, System.out);
         }
      });

      errT.start();
      outT.start();
   }

   private void processInputStream(InputStream in, PrintStream out) {
      int val;
      try {
         StringBuilder builder = new StringBuilder();
         while ((val = in.read()) != -1) {
            builder.append((char) val);
            if (val == '\n' || val == '\r') {
               if (builder.length() > 0) builder.delete(0, builder.length() - 1);
            }
            if (in.available() == 0) {
               if (prompt.compareToIgnoreCase(builder.toString().trim()) == 0) {
                  notifyPrompt();
               }
            }
            out.print((char) val);
         }
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public synchronized void cmd(String cmd) throws InterruptedException, IOException {
      OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(new File(wd, "launchHelper.bat")));
      osw.append(cmd);
      osw.flush();
      osw.close();
      ps.println("launchHelper.bat");
      ps.flush();
      this.wait();
   }

   public void close() {
      ps.println("exit");
      ps.flush();
   }

   /**
    * 
    */
   private synchronized void notifyPrompt() {
      this.notify();
   }

}
