/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.future;

/**
 * @author b1528444
 *
 */
public enum Component {

   VM("vm"),
   JMS("osee-jms");
   
   private String name;
   private String nameWithColon;
   
   private Component(String name){
      this.name = name;
      this.nameWithColon = name + ":";
   }

   @Override
   public String toString() {
      return name + ":";
   }
   
   public String getComponentName(){
      return name;
   }
   
   public String getComponentNameForRoutes(){
      return nameWithColon;
   }
}
