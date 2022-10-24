/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.framework.ui.skynet;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Ryan D. Brooks
 */
public class MenuCmdDef {
   private static final String GENERALIZED_CMD_ID = "org.eclipse.osee.framework.ui.skynet.renderer.command";

   private final CommandGroup commandGroup;
   private final String commandId;
   private final Map<String, String> commandParamMap;
   private final ImageDescriptor icon;
   private final String label;

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, ImageDescriptor icon) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, icon, (String) null, (String) null,
         (Map<String, String>) null);
   }

   public MenuCmdDef(CommandGroup commandGroup, String label, ImageDescriptor icon, Map<String, String> commandParamMap) {
      this(commandGroup, GENERALIZED_CMD_ID, commandGroup.getPresentationType(), label, icon, (String) null,
         (String) null, commandParamMap);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, ImageDescriptor icon, Map<String, String> commandParamMap) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, icon, (String) null, (String) null,
         commandParamMap);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, ImageDescriptor icon, String optionKey, String optionValue) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, icon, optionKey, optionValue,
         (Map<String, String>) null);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, ImageDescriptor icon, String optionKey, String optionValue, Map<String, String> commandParamMap) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, icon, optionKey, optionValue, commandParamMap);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, KeyedImage imageEnum) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, ImageManager.getImageDescriptor(imageEnum),
         (String) null, (String) null, (Map<String, String>) null);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, KeyedImage imageEnum, String optionKey, String optionValue) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, ImageManager.getImageDescriptor(imageEnum),
         optionKey, optionValue, (Map<String, String>) null);
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, ImageDescriptor icon) {
      this(commandGroup, commandId, (PresentationType) null, (String) null, icon, (String) null, (String) null,
         (Map<String, String>) null);
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, KeyedImage imageEnum) {
      this(commandGroup, commandId, (PresentationType) null, (String) null, ImageManager.getImageDescriptor(imageEnum),
         (String) null, (String) null, (Map<String, String>) null);
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, PresentationType presentationType, String label, ImageDescriptor icon, String optionKey, String optionValue) {
      this(commandGroup, commandId, presentationType, label, icon, optionKey, optionValue, (Map<String, String>) null);
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, PresentationType presentationType, String label, KeyedImage imageEnum) {
      this(commandGroup, commandId, presentationType, label, ImageManager.getImageDescriptor(imageEnum), (String) null,
         (String) null, (Map<String, String>) null);
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, PresentationType presentationType, String label, KeyedImage imageEnum, String optionKey, String optionValue) {
      this(commandGroup, commandId, presentationType, label, ImageManager.getImageDescriptor(imageEnum), optionKey,
         optionValue, (Map<String, String>) null);
   }

   /*
    * Primary constructor. All other constructors should invoke this one.
    */

   //@formatter:off
   public
      MenuCmdDef
         (
            CommandGroup       commandGroup,
            String             commandId,
            PresentationType   presentationType,
            String             label,
            ImageDescriptor    icon,
            String             optionKey,
            String             optionValue,
            Map<String,String> commandParamMap
         ) {

      this.commandGroup = commandGroup;
      this.commandId = commandId;
      this.icon = icon;
      this.label = label;

      var entrySet =
         Objects.nonNull( commandParamMap )
            ? new HashSet<Map.Entry<String,String>>( commandParamMap.entrySet() )
            : new HashSet<Map.Entry<String,String>>();

      if (Objects.nonNull(presentationType )) {
         entrySet.add( new AbstractMap.SimpleImmutableEntry<String,String>(PresentationType.class.getSimpleName(), presentationType.toString()) );
      }

      if (Objects.nonNull(optionKey) && Objects.nonNull(optionValue)) {
         entrySet.add( new AbstractMap.SimpleImmutableEntry<String,String>(optionKey, optionValue) );
      }

      @SuppressWarnings("unchecked")
      Map.Entry<String,String>[] entryArray = entrySet.toArray(new Map.Entry[0]);

      this.commandParamMap = Map.ofEntries( entryArray);
   }
   //@formatter:on

   public CommandGroup getcommandGroup() {
      return this.commandGroup;
   }

   public String getCommandId() {
      return this.commandId;
   }

   public Map<String, String> getCommandParamMap() {
      return this.commandParamMap;
   }

   public ImageDescriptor getIcon() {
      return this.icon;
   }

   public String getLabel() {
      return this.label;
   }

   @Override
   public String toString() {
      return this.label;
   }
}