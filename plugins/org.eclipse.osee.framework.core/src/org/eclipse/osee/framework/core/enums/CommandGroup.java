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

/**
 * @author Roberto E. Escobar
 */
public enum CommandGroup {

   /**
    * Group for commands that generate a preview using a client side generator.
    */

   PREVIEW(PresentationType.PREVIEW),

   /**
    * Group for commands that generate a preview using a server side generator.
    */

   PREVIEW_SERVER(PresentationType.PREVIEW_SERVER),

   EDIT(PresentationType.SPECIALIZED_EDIT),
   SHOW(PresentationType.SPECIALIZED_EDIT);

   public static CommandGroup[] getReadOnly() {
      return new CommandGroup[] {PREVIEW, SHOW};
   }

   PresentationType presentationType;

   CommandGroup(PresentationType type) {
      this.presentationType = type;
   }

   public PresentationType getPresentationType() {
      return presentationType;
   }

   public boolean isEdit() {
      return CommandGroup.EDIT == this;
   }

   public boolean isPreview() {
      //@formatter:off
      return
            ( CommandGroup.PREVIEW        == this )
         || ( CommandGroup.PREVIEW_SERVER == this );
      //@formatter:on
   }

   public boolean isShowIn() {
      return CommandGroup.SHOW == this;
   }
}
