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
import java.util.HashMap;
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

   /**
    * Creates a new menu command definition with the following:
    * <dl>
    * <dt>Command Group:</dt>
    * <dd>The specified <code>commandGroup</code> is used for the menu grouping to add the command to.</dd>
    * <dt>Command Identifier:</dt>
    * <dd>The specified <code>commandId</code> is used for the desired menu system command.</dd>
    * <dt>Presentation Type:</dt>
    * <dd>No Presentation Type is used.</dd>
    * <dt>Label:</dt>
    * <dd>A menu command label is not used.</dd>
    * <dt>Icon:</dt>
    * <dd>The specified <code>icon</code> is used for the default menu icon.</dd>
    * <dt>Option Key:</dt>
    * <dd>An option key is not used.</dd>
    * <dt>Option Value:</dt>
    * <dd>An option value is not used.</dd>
    * <dt>Command Parameter Map:</dt>
    * <dd>An empty map is used.</dd>
    * </dl>
    *
    * @param commandGroup the menu grouping to add the command to.
    * @param commandId an identifier for the desired menu system command.
    * @param icon the default icon {@link ImageDescriptor}.
    */

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

   /**
    * Creates a new menu command definition with the following:
    * <dl>
    * <dt>Command Group:</dt>
    * <dd>The specified <code>commandGroup</code> is used for the menu grouping to add the command to.</dd>
    * <dt>Command Identifier:</dt>
    * <dd>The specified <code>commandId</code> is used for the desired menu system command.</dd>
    * <dt>Presentation Type:</dt>
    * <dd>The specified <code>presentationType</code> is used. This specifies the type of presentation made to the user
    * by the command.</dd>
    * <dt>Label:</dt>
    * <dd>The specified <code>label</code> is used for the menu command.</dd>
    * <dt>Icon:</dt>
    * <dd>The specified <code>icon</code> is used for the default menu icon.</dd>
    * <dt>Option Key:</dt>
    * <dd>The <code>optionKey</code> and <code>optionValue</code> are added to the menu command definition's command
    * parameter map.</dd>
    * <dt>Option Value:</dt>
    * <dd>The <code>optionKey</code> and <code>optionValue</code> are added to the menu command definition's command
    * parameter map.</dd>
    * <dt>Command Parameter Map:</dt>
    * <dd>The command parameters in the <code>commandParamMap</code> are copied into the menu command definition's
    * command parameter map.</dd>
    * </dl>
    *
    * @param commandGroup the menu grouping to add the command to.
    * @param commandId an identifier for the desired menu system command.
    * @param presentationType the type of presentation made by the command.
    * @param label the menu label for the command.
    * @param icon the default icon {@link ImageDescriptor}.
    * @param optionKey when <code>optionValue</code> is also specified, the <code>optionKey</code> and
    * <code>optionValue</code> are added to the menu command definition's command parameter map.
    * @param optionValue when <code>optionKey</code> is also specified, the <code>optionKey</code> and
    * <code>optionValue</code> are added to the menu command definition's command parameter map.
    * @param commandParamMap a {@link Map} of command parameters for the menu command definition.
    * @implNote Primary constructor. All other constructors should invoke this one.
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
         entrySet.add( new AbstractMap.SimpleImmutableEntry<String,String>(PresentationType.class.getSimpleName(), presentationType.name()) );
      }

      if (Objects.nonNull(optionKey) && Objects.nonNull(optionValue)) {
         entrySet.add( new AbstractMap.SimpleImmutableEntry<String,String>(optionKey, optionValue) );
      }

      @SuppressWarnings("unchecked")
      Map.Entry<String,String>[] entryArray = entrySet.toArray(new Map.Entry[0]);

      this.commandParamMap = Map.ofEntries( entryArray);
   }
   //@formatter:on

   //@formatter:off
   private MenuCmdDef
      (
         CommandGroup        commandGroup,
         String              commandId,
         Map<String, String> commandParamMap,
         ImageDescriptor     icon,
         String              label ) {

      this.commandGroup = commandGroup;
      this.commandId = commandId;
      this.commandParamMap = new HashMap<>(commandParamMap);
      this.icon = icon;
      this.label = label;
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

   public MenuCmdDef newInstance(ImageDescriptor icon) {
      //@formatter:off
      return
         new MenuCmdDef
                (
                   this.commandGroup,
                   this.commandId,
                   this.commandParamMap,
                   Objects.nonNull( icon ) ? icon : this.icon,
                   this.label
                );
   }
}