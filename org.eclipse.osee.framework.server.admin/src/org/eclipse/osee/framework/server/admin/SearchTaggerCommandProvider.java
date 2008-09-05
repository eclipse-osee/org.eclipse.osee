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

import java.io.File;
import org.eclipse.osee.framework.server.admin.search.SearchTaggerCommands;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class SearchTaggerCommandProvider implements CommandProvider {

   public void _tag_all(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().startTagAll(ci);
   }

   public void _tag_all_stop(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().stopTagAll(ci);
   }

   public void _drop_all_tags(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().startDropAll(ci);
   }

   public void _drop_all_tags_stop(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().stopDropAll(ci);
   }

   public void _tagger_stats(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().getTaggerStatistics(ci);
   }

   public void _tagger_stats_clear(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().clearTaggerStats();
   }

   public void _search_stats(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().getSearchStatistics(ci);
   }

   public void _search_stats_clear(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().clearSearchStats();
   }

   public void _tag(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().startTagItem(ci);
   }

   public void _tag_stop(CommandInterpreter ci) {
      SearchTaggerCommands.getInstance().stopTagItem(ci);
   }

   public void _configini(CommandInterpreter ci) {
      StringBuilder sb = new StringBuilder();
      sb.append("eclipse.ignoreApp=true\n");
      sb.append("osgi.bundles= \\\n");

      String arg = ci.nextArgument();

      File folder = new File(arg);
      File[] files = folder.listFiles();
      for (File f : files) {
         if (!f.isDirectory()) {
            sb.append(f.toURI());
            sb.append("@start, \\\n");
         }
      }
      System.out.println(sb.toString());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
    */
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
