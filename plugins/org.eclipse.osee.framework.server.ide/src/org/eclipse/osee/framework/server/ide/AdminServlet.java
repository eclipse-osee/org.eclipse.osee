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
package org.eclipse.osee.framework.server.ide;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class AdminServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = -4391079960307521104L;

   private final BundleContext context;

   public AdminServlet(Log logger, BundleContext context) {
      super(logger);
      this.context = context;
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      resp.setStatus(HttpServletResponse.SC_OK);
      PrintWriter writer = resp.getWriter();

      Map<String, CommandProvider> cmds = getCommands(context);
      for (CommandProvider commandProvider : cmds.values()) {
         writer.append(commandProvider.getHelp());
      }
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      String cmd = req.getParameter("cmd");
      String argList = req.getParameter("args");

      List<String> args;
      if (Strings.isValid(argList)) {
         args = Arrays.asList(argList.split(","));
      } else {
         args = Collections.emptyList();
      }
      resp.setStatus(HttpServletResponse.SC_OK);
      CommandInterpreter interpreter = new HttpCommandInterpreter(context, resp.getWriter(), args.iterator());
      Object object = interpreter.execute(cmd);
      if (object instanceof Job) {
         Job job = (Job) object;
         try {
            job.join();
            IStatus status = job.getResult();
            interpreter.println(status.toString());
         } catch (InterruptedException ex) {
            interpreter.print(ex);
         }
      } else if (object instanceof Future<?>) {
         Future<?> future = (Future<?>) object;
         try {
            future.get();
         } catch (Exception ex) {
            interpreter.print(ex);
         }
      }

   }

   private static String commandKey(String rawCommand) {
      return "_" + rawCommand;
   }

   private static Map<String, CommandProvider> getCommands(BundleContext context) {
      Map<String, CommandProvider> data = new HashMap<>();
      ServiceTracker<CommandProvider, CommandProvider> tracker =
         new ServiceTracker<>(context, CommandProvider.class, null);
      tracker.open(true);
      try {
         Object[] services = tracker.getServices();
         for (Object service : services) {
            CommandProvider commandProvider = (CommandProvider) service;
            for (Method method : commandProvider.getClass().getMethods()) {
               String methodName = method.getName();
               if (methodName.startsWith("_")) {
                  data.put(methodName, commandProvider);
               }
            }
         }
      } finally {
         OsgiUtil.close(tracker);
      }
      return data;
   }

   private static final class HttpCommandInterpreter implements CommandInterpreter {

      private final Writer writer;
      private final BundleContext context;
      private final Iterator<String> args;

      public HttpCommandInterpreter(BundleContext context, Writer writer, Iterator<String> args) {
         this.writer = writer;
         this.context = context;
         this.args = args;
      }

      @Override
      public String nextArgument() {
         return args.hasNext() ? args.next() : null;
      }

      @Override
      public Object execute(String cmd) {
         String methodName = commandKey(cmd);
         Map<String, CommandProvider> commands = getCommands(context);
         CommandProvider commandProvider = commands.get(methodName);
         Class<?> providerClass = commandProvider.getClass();

         Object toReturn = null;
         try {
            Method method = providerClass.getMethod(methodName, CommandInterpreter.class);
            toReturn = method.invoke(commandProvider, this);
         } catch (Exception ex) {
            print(ex);
         }
         return toReturn;
      }

      @Override
      public void print(Object o) {
         print(String.valueOf(o));
      }

      @Override
      public void println() {
         print("\n");
      }

      @Override
      public void println(Object o) {
         print(String.format("%s\n", o));
      }

      @Override
      public void printStackTrace(Throwable t) {
         print(Lib.exceptionToString(t));
      }

      @SuppressWarnings("rawtypes")
      @Override
      public void printDictionary(Dictionary dic, String title) {
         print(String.format("%s:[%s]", title, dic));
      }

      @Override
      public void printBundleResource(Bundle bundle, String resource) {
         print(String.format("[%s:%s]", bundle, resource));
      }

      private void print(String message) {
         try {
            writer.append(message);
         } catch (Exception ex) {
            // Do Nothing
         }
      }
   }
}
