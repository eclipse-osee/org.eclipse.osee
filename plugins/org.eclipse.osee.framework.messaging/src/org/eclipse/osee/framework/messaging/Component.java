/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.framework.messaging;

/**
 * @author Andrew M. Finkbeiner
 */
public enum Component {

   VM("osee-vm"),
   JMS("osee-jms");

   private String name;
   private String nameWithColon;

   private Component(String name) {
      this.name = name;
      this.nameWithColon = name + ":";
   }

   @Override
   public String toString() {
      return name + ":";
   }

   public String getComponentName() {
      return name;
   }

   public String getComponentNameForRoutes() {
      return nameWithColon;
   }

   public boolean isVMComponent() {
      return this.equals(Component.VM);
   }
}
