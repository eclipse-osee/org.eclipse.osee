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
package org.eclipse.osee.framework.server.admin;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.model.cache.AttributeTypeCache;
import org.eclipse.osee.framework.core.operation.CommandInterpreterLogger;
import org.eclipse.osee.framework.core.operation.MutexSchedulingRule;
import org.eclipse.osee.framework.core.operation.OperationLogger;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osee.framework.server.admin.search.SearchStats;
import org.eclipse.osee.framework.server.admin.search.TagItemOperation;
import org.eclipse.osee.framework.server.admin.search.TaggerAllOperation;
import org.eclipse.osee.framework.server.admin.search.TaggerDropAllOperation;
import org.eclipse.osee.framework.server.admin.search.TaggerStats;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public final class SearchTaggerCommandProvider implements CommandProvider {
   private final ISchedulingRule tagMutex = new MutexSchedulingRule();
   private Job tagAllJob;

   public Job _tag_all(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);

      Set<Integer> branchIds = new LinkedHashSet<Integer>();
      boolean tagOnlyMissingGammas = false;
      String arg = ci.nextArgument();
      while (arg != null) {
         if (Strings.isValid(arg)) {
            if (arg.equals("-missing")) {
               tagOnlyMissingGammas = true;
            } else {
               branchIds.add(new Integer(arg));
            }
         }
         arg = ci.nextArgument();
      }

      AttributeTypeCache attTypeCache = Activator.getOseeCachingService().getAttributeTypeCache();

      tagAllJob =
         Operations.executeAsJob(new TaggerAllOperation(logger, attTypeCache, branchIds, tagOnlyMissingGammas), false,
            tagMutex);
      return tagAllJob;
   }

   public void _tag_all_stop(CommandInterpreter ci) {
      tagAllJob.cancel();
   }

   public Job _drop_all_tags(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new TaggerDropAllOperation(logger), false, tagMutex);
   }

   public Job _tagger_stats(CommandInterpreter ci) {
      TaggerStats stats = new TaggerStats(new CommandInterpreterLogger(ci));
      return Operations.executeAsJob(stats, false);
   }

   public void _tagger_stats_clear(CommandInterpreter ci) {
      Activator.getSearchTagger().clearStatistics();
   }

   public Job _search_stats(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);
      return Operations.executeAsJob(new SearchStats(logger), false);
   }

   public void _search_stats_clear(CommandInterpreter ci) {
      Activator.getSearchEngine().clearStatistics();
   }

   public Job _tag(CommandInterpreter ci) {
      OperationLogger logger = new CommandInterpreterLogger(ci);

      Set<Long> gammas = new HashSet<Long>();
      String arg;
      while ((arg = ci.nextArgument()) != null) {
         gammas.add(new Long(arg));
      }
      return Operations.executeAsJob(new TagItemOperation(logger, gammas), false, tagMutex);
   }

   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n---OSEE Search & Tag Commands---\n");
      sb.append("        tag_all [-missing] [<branchId>] - tag all attributes in a branch or tag all attributes in all branches if id not specified\n");
      sb.append("        tag_all_stop - stop tagging all attributes\n");
      sb.append("        tag [<gammaId> <gammaId> ...]- tag individual item\n");
      sb.append("        tagger_stats - get tagger stats\n");
      sb.append("        tagger_stats_clear - clear tagger stats\n");
      sb.append("        search_stats - get tagger stats\n");
      sb.append("        search_stats_clear - clear tagger stats\n");
      return sb.toString();
   }
}
