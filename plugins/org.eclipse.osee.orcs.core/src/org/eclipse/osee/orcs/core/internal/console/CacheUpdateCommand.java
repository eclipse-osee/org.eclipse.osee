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
package org.eclipse.osee.orcs.core.internal.console;

import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;

/**
 * @author Roberto E. Escobar
 */
public class CacheUpdateCommand implements ConsoleCommand {

   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "cache_update";
   }

   @Override
   public String getDescription() {
      return "Updates server type caches";
   }

   @Override
   public String getUsage() {
      StringBuilder builder = new StringBuilder();
      builder.append(" - Update server type caches.");
      return builder.toString();
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new CacheUpdateCallable(console);
   }

   private class CacheUpdateCallable extends CancellableCallable<Boolean> {

      private final Console console;

      public CacheUpdateCallable(Console console) {
         this.console = console;
      }

      private OrcsTypes getOrcTypes() {
         return getOrcsApi().getOrcsTypes();
      }

      @Override
      public Boolean call()  {
         OrcsTypes orcsTypes = getOrcTypes();
         orcsTypes.invalidateAll();
         console.writeln("Type caches invalidated.");
         return Boolean.TRUE;
      }
   }

}