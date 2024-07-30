/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.core.enums.KeyScope;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

public class KeyScopeContainer extends NamedIdBase {
   private boolean selected;

   public KeyScopeContainer() {
   }

   public KeyScopeContainer(Long id, String name, boolean selected) {
      super(id, name);
      this.selected = selected;
   }

   public KeyScopeContainer(Long id, boolean selected) {
      super(id, KeyScope.fromId(id).getName());
      this.selected = selected;
   }

   public KeyScopeContainer(Long id) {
      super(id, KeyScope.fromId(id).getName());
      this.selected = true;
   }

   public KeyScopeContainer(String name, String id, boolean selected) {
      super(Long.parseLong(id), name);
      this.selected = selected;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }
}
