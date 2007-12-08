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
package org.eclipse.osee.framework.ui.plugin.event;

public class CoreEventManager extends EventManager {

   private final static CoreEventManager reference = new CoreEventManager();

   public static CoreEventManager getInstance() {
      return reference;
   }

   private CoreEventManager() {

   }
}
