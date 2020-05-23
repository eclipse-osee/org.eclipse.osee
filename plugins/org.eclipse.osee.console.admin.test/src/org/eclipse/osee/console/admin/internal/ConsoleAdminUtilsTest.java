/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.console.admin.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.junit.Assert;
import org.junit.Test;
import org.osgi.framework.Bundle;

/**
 * Test Case for {@link ConsoleAdminUtils}
 * 
 * @author Roberto E. Escobar
 */
public class ConsoleAdminUtilsTest {

   @Test
   public void testParsingArguments() {
      List<String> args = new ArrayList<>();
      args.add("CoMMaND");
      args.add("hello=12345");
      args.add("var2=\"var1\"");
      args.add("var3='var4'");
      args.add("array1='one,two,three,four,five'");
      args.add("array2='one;two;three;four;five'");
      args.add("array3='one&two&three&four&five'");
      args.add("array4='one; two, three& four five'");
      ConsoleParameters params = ConsoleAdminUtils.parse(new MockCommandInterpreter(args.iterator()));

      Assert.assertEquals("command", params.getCommandName());

      String rawMessage = org.eclipse.osee.framework.jdk.core.util.Collections.toString(" ", args);
      rawMessage = rawMessage.replace("command", "CoMMaND");
      Assert.assertEquals(rawMessage, params.getRawString());
      Assert.assertEquals(true, params.exists("hello"));
      Assert.assertEquals(true, params.exists("var2"));
      Assert.assertEquals(false, params.exists("var1"));
      Assert.assertEquals(true, params.exists("var3"));
      Assert.assertEquals(false, params.exists("var4"));

      Assert.assertEquals(true, params.exists("array1"));
      Assert.assertEquals(true, params.exists("array2"));
      Assert.assertEquals(true, params.exists("array3"));
      Assert.assertEquals(true, params.exists("array4"));

      Assert.assertEquals(12345, params.getInt("hello"));
      Assert.assertEquals("12345", params.get("hello"));
      Assert.assertEquals("var1", params.get("var2"));
      Assert.assertEquals("var4", params.get("var3"));

      Assert.assertEquals("one,two,three,four,five", params.get("array1"));
      Assert.assertEquals("one;two;three;four;five", params.get("array2"));
      Assert.assertEquals("one&two&three&four&five", params.get("array3"));
      Assert.assertEquals("one; two, three& four five", params.get("array4"));

      for (int cnt = 1; cnt < 5; cnt++) {
         String message = String.format("count was [%s] - ", cnt);
         String[] values = params.getArray("array" + cnt);
         Assert.assertEquals(message, "one", values[0]);
         Assert.assertEquals(message, "two", values[1]);
         Assert.assertEquals(message, "three", values[2]);
         Assert.assertEquals(message, "four", values[3]);
         Assert.assertEquals(message, "five", values[4]);
      }
   }

   @Test
   public void testParsingArguments2() {
      ConsoleCommand cmd1 = new MockConsoleCommand("hijkld");
      ConsoleCommand cmd2 = new MockConsoleCommand("zsfslj");
      ConsoleCommand cmd3 = new MockConsoleCommand("123 test");
      ConsoleCommand cmd4 = new MockConsoleCommand("abcdefg");

      List<ConsoleCommand> list = new ArrayList<>();
      list.add(cmd1);
      list.add(cmd2);
      list.add(cmd3);
      list.add(cmd4);

      Collections.shuffle(list);

      list = ConsoleAdminUtils.sort(list);

      Iterator<ConsoleCommand> it = list.iterator();
      Assert.assertEquals(cmd3, it.next());
      Assert.assertEquals(cmd4, it.next());
      Assert.assertEquals(cmd1, it.next());
      Assert.assertEquals(cmd2, it.next());
   }

   private final class MockConsoleCommand implements ConsoleCommand {

      private final String name;

      public MockConsoleCommand(String name) {
         this.name = name;
      }

      @Override
      public String getName() {
         return name;
      }

      @Override
      public String getDescription() {
         return null;
      }

      @Override
      public String getUsage() {
         return null;
      }

      @Override
      public Callable<?> createCallable(Console console, ConsoleParameters params) {
         return null;
      }

      @Override
      public String toString() {
         return getName();
      }

   }

   private final class MockCommandInterpreter implements CommandInterpreter {

      private final Iterator<String> arguments;

      public MockCommandInterpreter(Iterator<String> arguments) {
         this.arguments = arguments;
      }

      @Override
      public String nextArgument() {
         return arguments.hasNext() ? arguments.next() : null;
      }

      @Override
      public Object execute(String cmd) {
         return null;
      }

      @Override
      public void print(Object o) {
         //
      }

      @Override
      public void println() {
         //
      }

      @Override
      public void println(Object o) {
         //
      }

      @Override
      public void printStackTrace(Throwable t) {
         //
      }

      @Override
      public void printDictionary(Dictionary<?, ?> dic, String title) {
         //
      }

      @Override
      public void printBundleResource(Bundle bundle, String resource) {
         //
      }

   }
}
