/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

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
