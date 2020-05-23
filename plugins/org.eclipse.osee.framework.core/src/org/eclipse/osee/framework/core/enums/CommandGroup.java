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

   PREVIEW(PresentationType.PREVIEW),
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
      return CommandGroup.PREVIEW == this;
   }

   public boolean isShowIn() {
      return CommandGroup.SHOW == this;
   }
}
