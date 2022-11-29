/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.framework.core.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enumeration of the command groupings for context menus.
 *
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

public enum CommandGroup {

   //@formatter:off

   /**
    * Group for commands that provide a specialized editor.
    */

   EDIT
      (
         PresentationType.SPECIALIZED_EDIT,
         false                                      /* Edit enabled commands */
      ),

   /**
    * Group for commands that generate a preview using a client side generator.
    */

   PREVIEW
      (
         PresentationType.PREVIEW,
         true                                       /* Read only commands */
      ),

   /**
    * Group for commands that generate a preview using a server side generator.
    */

   PREVIEW_SERVER
      (
         PresentationType.PREVIEW_SERVER,
         true                                       /* Read only commands */
      ),

   /**
    * Group for commands that show data using a specialized editor in read only mode.
    */

   SHOW
      (
         PresentationType.SPECIALIZED_EDIT,
         true                                       /* Read only commands */
      );

   //@formatter:on

   /**
    * {@link List} of all {@link CommandGroup} enumeration members.
    */

   private static List<CommandGroup> allGroups = List.of(CommandGroup.values());

   /**
    * {@link List} of only {@link CommandGroup} enumeration members for read only command groups.
    */

   private static List<CommandGroup> readOnlyGroups =
      Stream.of(CommandGroup.values()).filter(CommandGroup::isReadOnly).collect(Collectors.toList());

   /**
    * Gets a list of the {@link CommandGroup} members with the specified properties.
    *
    * @param readOnly when <code>true</code>, only include command groups that disallow editing.
    * @return when <code>readOnly</code> is <code>true</code>, a list of the {@link CommandGroup}s that disallow
    * editing; otherwise, a list of all {@link CommandGroup}s.
    */

   public static List<CommandGroup> getCommandGroups(boolean readOnly) {
      //@formatter:off
      return
         readOnly
            ? CommandGroup.readOnlyGroups
            : CommandGroup.allGroups;
      //@formatter:on
   }

   /**
    * Flag to indicate if the presentation commands are for read only display of data or allow editing of data.
    */

   boolean isReadOnlyPresentation;

   /**
    * The {@link PresentationType} the group of commands is for.
    */

   PresentationType presentationType;

   /**
    * Creates a new enumeration member with the specified properties.
    *
    * @param type the {@link PresentationType} of the represented command group.
    * @param isReadOnlyPresentation <code>true</code> when the represented command group does not allow editing.
    */

   private CommandGroup(PresentationType type, boolean isReadOnlyPresentation) {
      this.presentationType = type;
      this.isReadOnlyPresentation = isReadOnlyPresentation;
   }

   /**
    * Gets the {@link PresentationType} of the {@link CommandGroup}.
    *
    * @return the {@link PresentationType}.
    */

   public PresentationType getPresentationType() {
      return presentationType;
   }

   /**
    * Predicate to determine if the {@link CommandGroup} allows editing.
    *
    * @return <code>true</code>, when the {@link CommandGroup} allows editing; otherwise, <code>false</code>.
    */

   public boolean isEdit() {
      return !this.isReadOnlyPresentation;
   }

   /**
    * Predicate to determine if the {@link CommandGroup} disallows editing.
    *
    * @return <code>true</code>, when the {@link CommandGroup} disallows editing; otherwise, <code>false</code>.
    */

   public boolean isReadOnly() {
      return this.isReadOnlyPresentation;
   }

}

/* EOF */
