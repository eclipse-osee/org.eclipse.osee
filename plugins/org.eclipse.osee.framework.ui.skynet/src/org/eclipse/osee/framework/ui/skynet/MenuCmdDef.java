/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet;

import java.util.HashMap;
import java.util.Map;
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
   private final PresentationType presentationType;
   private final String label;
   private final ImageDescriptor icon;
   private final String optionKey;
   private final String optionValue;
   private final String commandId;
   private Map<String, String> commandParamMap;

   public MenuCmdDef(CommandGroup commandGroup, String commandId, ImageDescriptor icon) {
      this(commandGroup, commandId, null, null, icon, null, null);
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, KeyedImage imageEnum) {
      this(commandGroup, commandId, null, null, imageEnum, null, null);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, KeyedImage imageEnum) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, imageEnum, null, null);
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, PresentationType presentationType, String label, KeyedImage imageEnum) {
      this(commandGroup, commandId, presentationType, label, imageEnum, null, null);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, KeyedImage imageEnum, String optionKey, String optionValue) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, imageEnum, optionKey, optionValue);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, ImageDescriptor icon) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, icon, null, null);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, ImageDescriptor icon, Map<String, String> commandParamMap) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, icon, null, null, commandParamMap);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, ImageDescriptor icon, String optionKey, String optionValue) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, icon, optionKey, optionValue);
   }

   public MenuCmdDef(CommandGroup commandGroup, PresentationType presentationType, String label, ImageDescriptor icon, String optionKey, String optionValue, Map<String, String> commandParamMap) {
      this(commandGroup, GENERALIZED_CMD_ID, presentationType, label, icon, optionKey, optionValue, commandParamMap);
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, PresentationType presentationType, String label, KeyedImage imageEnum, String optionKey, String optionValue) {
      this(commandGroup, commandId, presentationType, label, ImageManager.getImageDescriptor(imageEnum), optionKey,
         optionValue);
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, PresentationType presentationType, String label, ImageDescriptor icon, String optionKey, String optionValue, Map<String, String> commandParamMap) {
      this(commandGroup, commandId, presentationType, label, icon, optionKey, optionValue);
      this.commandParamMap = commandParamMap;
   }

   public MenuCmdDef(CommandGroup commandGroup, String commandId, PresentationType presentationType, String label, ImageDescriptor icon, String optionKey, String optionValue) {
      this.commandGroup = commandGroup;
      this.commandId = commandId;
      this.presentationType = presentationType;
      this.label = label;
      this.icon = icon;
      this.optionKey = optionKey;
      this.optionValue = optionValue;
   }

   public CommandGroup getcommandGroup() {
      return commandGroup;
   }

   public Map<String, String> getCommandParamMap() {
      if (commandParamMap == null) {
         commandParamMap = new HashMap<>();
      }
      if (presentationType != null) {
         commandParamMap.put(PresentationType.class.getSimpleName(), presentationType.toString());
      }
      if (optionKey != null) {
         commandParamMap.put(optionKey, optionValue);
      }
      return commandParamMap;
   }

   public String getLabel() {
      return label;
   }

   public ImageDescriptor getIcon() {
      return icon;
   }

   public String getCommandId() {
      return commandId;
   }

   @Override
   public String toString() {
      return label;
   }
}