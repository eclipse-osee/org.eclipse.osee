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
import org.eclipse.osee.framework.server.admin.search.TaggerCommands;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class SearchTaggerCmd implements CommandProvider {

   public void _tag_all(CommandInterpreter ci) {
      TaggerCommands.getInstance().execute(ci);
   }

   public void _tag_all_stop(CommandInterpreter ci) {
      TaggerCommands.getInstance().executeStop(ci);
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
      sb.append("\n---OSEE Server Admin Commands---\n");
      sb.append("        tag_all - tag all attributes\n");
      sb.append("        tag_all_stop - stop tagging all attributes\n");
      return sb.toString();
   }
}
