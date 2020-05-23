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

package org.eclipse.osee.ats.api.config;

import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Donald G. Dunne
 */
public abstract class JaxAtsConfigObject extends JaxAtsObject implements IAtsConfigObject {

   protected boolean active = false;

   public JaxAtsConfigObject() {
      this(Id.SENTINEL, "");
   }

   public JaxAtsConfigObject(Long id, String name) {
      super(id, name);
   }

   @Override
   public boolean isActive() {
      return active;
   }

   @Override
   public void setActive(boolean active) {
      this.active = active;
   }

}