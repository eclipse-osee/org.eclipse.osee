/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.eclipse.osee.framework.jdk.core.util.io.OutputRedirector;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Ryan D. Brooks
 */
@Ignore
public class ProcessesTest {

   public static final ExecutorService executor = Executors.newCachedThreadPool();
   private static final String argument = "progress";

   @Test
   public void testProcessCancel() throws IOException, InterruptedException, ExecutionException {
      ProcessBuilder builder = new ProcessBuilder("java", "-cp", ".\\bin",
         "org.eclipse.osee.framework.jdk.core.util.ProcessesTest", argument, "bye");
      builder.redirectErrorStream(true);
      Process process = builder.start();

      StringWriter stringWriter = new StringWriter();
      BufferedReader stdOutputIn = new BufferedReader(new InputStreamReader(process.getInputStream()));
      Future<Long> outFuture = Processes.executor.submit(new OutputRedirector(stringWriter, stdOutputIn));
      Thread.sleep(500);
      process.destroy();
      Assert.assertEquals(argument, stringWriter.toString());
      Assert.assertEquals(new Long(argument.length()), outFuture.get());
   }

   @Test
   public void testExecuteCommandToString() {
      commandToStringHelper("java version ", "java", "-version");
      commandToStringHelper("java.io.IOException: Cannot run program", "bogus command");
      commandToStringHelper("Could not create the Java", "java", "-alsdfk");
   }

   private void commandToStringHelper(String expected, String... callAndArgs) {
      String actual = Processes.executeCommandToString(callAndArgs);
      Assert.assertTrue(String.format("expected: [%s] actual [%s]", expected, actual), actual.contains(expected));
   }

   /**
    * this method is invoked from testProcessCancel in a separate process
    */
   public static final void main(String[] args) throws IOException, InterruptedException {
      System.out.print(args[0]);
      Thread.sleep(3000);
      System.in.read();
      System.out.println(args[1]);
   }
}
