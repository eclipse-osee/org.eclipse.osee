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

import org.eclipse.osee.framework.server.admin.search.SearchTaggerCommands;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class SearchTaggerCommandProvider implements CommandProvider {

   private final SearchTaggerCommands searchTaggerCommands;

   public SearchTaggerCommandProvider() {
      this.searchTaggerCommands = new SearchTaggerCommands();
   }

   public void _tag_all(CommandInterpreter ci) {
      searchTaggerCommands.startTagAll(ci);
   }

   public void _tag_all_stop(CommandInterpreter ci) {
      searchTaggerCommands.stopTagAll(ci);
   }

   public void _drop_all_tags(CommandInterpreter ci) {
      searchTaggerCommands.startDropAll(ci);
   }

   public void _drop_all_tags_stop(CommandInterpreter ci) {
      searchTaggerCommands.stopDropAll(ci);
   }

   public void _tagger_stats(CommandInterpreter ci) {
      searchTaggerCommands.getTaggerStatistics(ci);
   }

   public void _tagger_stats_clear(CommandInterpreter ci) {
      searchTaggerCommands.clearTaggerStats();
   }

   public void _search_stats(CommandInterpreter ci) {
      searchTaggerCommands.getSearchStatistics(ci);
   }

   public void _search_stats_clear(CommandInterpreter ci) {
      searchTaggerCommands.clearSearchStats();
   }

   public void _tag(CommandInterpreter ci) {
      searchTaggerCommands.startTagItem(ci);
   }

   public void _tag_stop(CommandInterpreter ci) {
      searchTaggerCommands.stopTagItem(ci);
   }

   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n---OSEE Search & Tag Commands---\n");
      sb.append("        tag_all [<branchId>] - tag all attributes in a branch or tag all attributes in all branches if id not specified\n");
      sb.append("        tag_all_stop - stop tagging all attributes\n");
      sb.append("        tag [<gammaId> <gammaId> ...]- tag individual item\n");
      sb.append("        tag_stop - stop tagging individual items\n");
      sb.append("        drop_all_tags - drops all tags\n");
      sb.append("        drop_all_tags_stop - stop dropping all tags\n");
      sb.append("        tagger_stats - get tagger stats\n");
      sb.append("        tagger_stats_clear - clear tagger stats\n");
      sb.append("        search_stats - get tagger stats\n");
      sb.append("        search_stats_clear - clear tagger stats\n");
      return sb.toString();
   }
}
