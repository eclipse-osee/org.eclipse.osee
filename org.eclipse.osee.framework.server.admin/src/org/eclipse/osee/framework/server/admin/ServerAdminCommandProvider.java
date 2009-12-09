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

import java.util.Arrays;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.server.admin.management.AdminCommands;
import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Roberto E. Escobar
 */
public class ServerAdminCommandProvider implements CommandProvider {

   private final AdminCommands adminCommands;

   public ServerAdminCommandProvider() {
      this.adminCommands = new AdminCommands();
   }

   //   public void _native_content_fix(CommandInterpreter ci) {
   //      CompressedContentFix.getInstance().execute(ci);
   //   }
   //
   //   public void _native_content_fix_stop(CommandInterpreter ci) {
   //      CompressedContentFix.getInstance().executeStop(ci);
   //   }
   //
   //   public void _convert(CommandInterpreter ci) {
   //      DataConversion.getInstance().convert(ci);
   //   }
   //
   //   public void _convertstop(CommandInterpreter ci) {
   //      DataConversion.getInstance().convertStop(ci);
   //   }

   public void _server_status(CommandInterpreter ci) {
      adminCommands.getServerStatus(ci);
   }

   public void _server_process_requests(CommandInterpreter ci) {
      adminCommands.setServletRequestProcessing(ci);
   }

   public void _add_osee_version(CommandInterpreter ci) {
      adminCommands.addServerVersion(ci);
   }

   public void _remove_osee_version(CommandInterpreter ci) {
      adminCommands.removeServerVersion(ci);
   }

   public void _osee_version(CommandInterpreter ci) {
      adminCommands.getServerVersion(ci);
   }

   public void _reload_cache(CommandInterpreter ci) {
      adminCommands.reloadCache(ci);
   }

   public void _clear_cache(CommandInterpreter ci) {
      adminCommands.clearCache(ci);
   }

   @Override
   public String getHelp() {
      StringBuilder sb = new StringBuilder();
      sb.append("\n---OSEE Server Admin Commands---\n");
      sb.append("        server_status - displays server status\n");
      sb.append("        server_process_requests [true | false]- command servlets to accept/reject requests\n");
      sb.append("        osee_version - displays the supported osee versions\n");
      sb.append("        add_osee_version [version string]- add the version string to the list of supported osee versions\n");
      sb.append("        remove_osee_version [version string]- removes the version string from the list of supported osee versions\n");
      sb.append("        change_attribute_uri_to_guid - renames attribute data stored on disk from HRID to Guid and updates database\n");
      sb.append(String.format("        reload_cache %s? - reloads server caches\n", Arrays.deepToString(
            OseeCacheEnum.values()).replaceAll(",", " | ")));
      sb.append(String.format("        clear_cache %s? - decaches all objects from the specified caches\n",
            Arrays.deepToString(OseeCacheEnum.values()).replaceAll(",", " |")));

      //      sb.append("        native_content_fix - converts some data\n");
      //      sb.append("        native_content_fix_stop - stop the conversion\n");
      //      sb.append("        convert - converts some data\n");
      //      sb.append("        convertstop - stop the conversion\n");
      return sb.toString();
   }
}
