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

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;

/**
 * @author Roberto E. Escobar
 */
public class BranchExchangeCheckCommand implements ConsoleCommand {

   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "branch_check_exchange";
   }

   @Override
   public String getDescription() {
      return "Checks the integrity of a branch exchange file.";
   }

   @Override
   public String getUsage() {
      return "uri=<EXCHANGE_FILE_LOCATION> - exchange file location relative to exchange storage path";
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      List<String> importFiles = Arrays.asList(params.getArray("uri"));
      OrcsBranch orcsBranch = getOrcsApi().getBranchOps();
      return new CheckBranchExchangeCallable(console, orcsBranch, importFiles);
   }

   private static class CheckBranchExchangeCallable extends CancellableCallable<Boolean> {

      private final Console console;
      private final OrcsBranch orcsBranch;
      private final List<String> importFiles;

      public CheckBranchExchangeCallable(Console console, OrcsBranch orcsBranch, List<String> importFiles) {
         this.console = console;
         this.orcsBranch = orcsBranch;
         this.importFiles = importFiles;
      }

      @Override
      public Boolean call() throws Exception {
         for (String fileToImport : importFiles) {
            console.writeln("Checking branch exchange [%]", fileToImport);
            URI uriToCheck = new URI("exchange://" + fileToImport);
            Callable<URI> callable = orcsBranch.checkBranchExchangeIntegrity(uriToCheck);
            callable.call();
            checkForCancelled();
         }
         return Boolean.TRUE;
      }
   }
}
