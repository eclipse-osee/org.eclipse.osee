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

package org.eclipse.osee.orcs.core.internal.console;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.console.admin.Console;
import org.eclipse.osee.console.admin.ConsoleCommand;
import org.eclipse.osee.console.admin.ConsoleParameters;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.core.model.cache.BranchFilter;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
public final class BranchCommand implements ConsoleCommand {

   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public OrcsApi getOrcsApi() {
      return orcsApi;
   }

   @Override
   public String getName() {
      return "branch";
   }

   @Override
   public String getDescription() {
      return "Lists all branches";
   }

   @Override
   public String getUsage() {
      StringBuilder builder = new StringBuilder();
      builder.append("[typeIs=<");
      builder.append(Collections.toString("|", (Object[]) BranchType.values()));
      builder.append(">+]");
      builder.append("\n");
      builder.append(" [typeIsNot=<");
      builder.append(Collections.toString("|", (Object[]) BranchType.values()));
      builder.append(">+]");
      builder.append("\n");
      builder.append("[stateIs=<");
      builder.append(Collections.toString("|", (Object[]) BranchType.values()));
      builder.append(">+]");
      builder.append("\n");
      builder.append(" [stateIsNot=<");
      builder.append(Collections.toString("|", (Object[]) BranchType.values()));
      builder.append(">+]");
      builder.append("\n");
      return builder.toString();
   }

   @Override
   public Callable<?> createCallable(Console console, ConsoleParameters params) {
      return new BranchCallable(console, params);
   }

   private final class BranchCallable extends CancellableCallable<Object> {

      private final Console console;
      private final ConsoleParameters params;

      public BranchCallable(Console console, ConsoleParameters params) {
         this.console = console;
         this.params = params;
      }

      private BranchType[] toBranchType(String[] types, boolean alllIfEmpty) {
         BranchType[] toReturn = new BranchType[0];
         if (types != null && types.length > 0) {
            Set<BranchType> data = new HashSet<>();
            for (String type : types) {
               BranchType typeEnum = BranchType.fromName(type.toUpperCase());
               data.add(typeEnum);
            }
            toReturn = data.toArray(new BranchType[data.size()]);
         } else {
            if (alllIfEmpty) {
               toReturn = BranchType.values();
            }
         }
         return toReturn;
      }

      private BranchState[] toBranchState(String[] types, boolean alllIfEmpty) {
         BranchState[] toReturn = new BranchState[0];
         if (types != null && types.length > 0) {
            Set<BranchState> data = new HashSet<>();
            for (String type : types) {
               BranchState typeEnum = BranchState.fromName(type.toUpperCase());
               data.add(typeEnum);
            }
            toReturn = data.toArray(new BranchState[data.size()]);
         } else {
            if (alllIfEmpty) {
               toReturn = BranchState.values();
            }
         }
         return toReturn;
      }

      @Override
      public Object call() throws Exception {
         BranchType[] isTypes = toBranchType(params.getArray("typeIs"), true);
         BranchType[] notTypes = toBranchType(params.getArray("typeIsNot"), false);
         BranchState[] isStates = toBranchState(params.getArray("stateIs"), true);
         BranchState[] notStates = toBranchState(params.getArray("stateIsNot"), false);

         BranchFilter filter = new BranchFilter(isTypes);
         filter.setNegatedBranchTypes(notTypes);
         filter.setBranchStates(isStates);
         filter.setNegatedBranchStates(notStates);

         BranchQuery query = orcsApi.getQueryFactory().branchQuery();
         ResultSet<Branch> branches = query.excludeArchived().andIsOfType(BranchType.WORKING).getResults();

         branches.sort(new Comparator<Branch>() {
            @Override
            public int compare(Branch o1, Branch o2) {
               return 0;
            }
         });
         int count = 0;
         for (Branch aBranch : branches) {
            console.writeln("[%s] - id[%s] guid[%s] sTx[%s] bTx[%s] parent[%s] type[%s] state[%s] archive[%s] name[%s]",
               ++count, aBranch, aBranch, aBranch.getName(), aBranch.getBranchType(), aBranch.getBranchState(),
               aBranch.isArchived(), aBranch.getParentTx(), aBranch.getBaselineTx(), aBranch.getParentBranch());
         }
         return Boolean.TRUE;
      }
   }
}