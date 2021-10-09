/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef.model;

/**
 * CompositeLayoutItem that creates box around layoutItems with name label on box
 *
 * @author Donald G. Dunne
 */
public class GroupCompositeLayoutItem extends CompositeLayoutItem {

   public GroupCompositeLayoutItem(int numColumns, String name, LayoutItem... layoutItems) {
      super(numColumns, layoutItems);
      setName(name);
   }

   @Override
   public boolean isGroupComposite() {
      return true;
   }

}
