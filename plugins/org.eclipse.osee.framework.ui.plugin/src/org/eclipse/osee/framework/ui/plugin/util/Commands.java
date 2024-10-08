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

package org.eclipse.osee.framework.ui.plugin.util;

import java.util.Map;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandManager;
import org.eclipse.core.commands.IParameter;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.services.IServiceLocator;

/**
 * @author Robert A. Fisher
 */
public final class Commands {

   // This is a utility class and should not ever be instantiated
   private Commands() {
      // Utility Class
   }

   /**
    * Sets up a local command with no id and uses that to retrieve a CommandContributionItem.
    * 
    * @param site may not be null
    * @param shortCommandId may not be null
    * @param name may not be null
    * @param parameterDefs may be null
    * @param parameters may be null
    * @param image may be null
    * @param mnemonic may be null
    * @param tooltip may be null
    * @param helpContextId may be null
    */
   public static CommandContributionItem getLocalCommandContribution(IWorkbenchPartSite site, String shortCommandId,
      String name, IParameter[] parameterDefs, Map<String, String> parameters, ImageDescriptor image, String mnemonic,
      String tooltip, String helpContextId) {
      return createCommandContributionItem(site, site.getId(), shortCommandId, name, parameterDefs, parameters, image,
         mnemonic, tooltip, helpContextId);
   }

   public static CommandContributionItem createCommandContributionItem(IServiceLocator site, String id,
      String shortCommandId, String name, IParameter[] parameterDefs, Map<String, String> parameters,
      ImageDescriptor image, String mnemonic, String tooltip, String helpContextId) {
      ICommandService commandService = site.getService(ICommandService.class);
      String commandId = id;

      if (shortCommandId != null) {
         commandId = commandId + "." + shortCommandId;
      }

      Command command = commandService.getCommand(commandId);
      if (!command.isDefined()) {
         Category category = commandService.getCategory(CommandManager.AUTOGENERATED_CATEGORY_ID);
         command.define(name, null, category, parameterDefs, null, helpContextId);
      }

      // Use the commandId as the id so calling code can get to the commandId
      return new CommandContributionItem(
         new CommandContributionItemParameter(site, command.getId(), command.getId(), parameters, image, null, null,
            name, mnemonic, tooltip, CommandContributionItem.STYLE_PUSH, helpContextId, false));
   }
}
