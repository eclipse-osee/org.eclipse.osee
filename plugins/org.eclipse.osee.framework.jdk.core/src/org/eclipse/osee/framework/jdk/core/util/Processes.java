/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.jdk.core.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.eclipse.osee.framework.jdk.core.util.io.OutputRedirector;

/**
 * @author Ryan D. Brooks
 */
public final class Processes {
   public static final ExecutorService executor = Executors.newCachedThreadPool();

   public static int handleProcess(Process process, Writer output)
      throws InterruptedException, ExecutionException, TimeoutException {
      BufferedReader stdOutputIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
      Future<Long> outFuture = executor.submit(new OutputRedirector(output, stdOutputIn));

      BufferedReader stdErrorIn = new BufferedReader(new InputStreamReader(process.getErrorStream()));
      Future<Long> errorFuture = executor.submit(new OutputRedirector(output, stdErrorIn));

      process.waitFor();
      int exitCode = process.exitValue();
      outFuture.get(5, TimeUnit.MINUTES);
      errorFuture.get(5, TimeUnit.SECONDS);
      return exitCode;
   }

   public static int handleProcess(Process process) {
      try {
         return Processes.handleProcess(process, new PrintWriter(System.out, true));
      } catch (Exception ex) {
         ex.printStackTrace();
         return -1;
      }
   }

   public static int executeCommandToStdOut(String... callAndArgs) {
      return executeCommand(new BufferedWriter(new PrintWriter(System.out, true)), callAndArgs);
   }

   public static String executeCommandToString(String... callAndArgs) {
      StringWriter stringWriter = new StringWriter();
      executeCommand(stringWriter, callAndArgs);
      return stringWriter.toString();
   }

   public static int executeCommand(Writer output, String... callAndArgs) {
      return executeCommand(output, null, callAndArgs);
   }

   public static int executeCommand(Writer output, File directory, String... callAndArgs) {
      int result = -1;
      Process process = null;
      try {
         ProcessBuilder builder = new ProcessBuilder(callAndArgs);
         builder.directory(directory);
         builder.redirectErrorStream(true);
         process = builder.start();

         result = Processes.handleProcess(process, output);
      } catch (Exception ex) {
         try {
            output.write(Lib.exceptionToString(ex));
         } catch (IOException ex1) {
            ex.printStackTrace();
         }
      } finally {
         if (process != null) {
            process.destroy();
         }
      }
      return result;
   }

   public static int printAndExec(Writer output, File directory, String... callAndArgs) {
      try {
         for (int j = 0; j < callAndArgs.length; j++) {
            output.write(callAndArgs[j] + " ");
         }
         output.write("\n");
         output.flush();
      } catch (IOException ex) {
         ex.printStackTrace();
      }

      return executeCommand(output, directory, callAndArgs);
   }
}