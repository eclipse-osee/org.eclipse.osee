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
package org.eclipse.osee.framework.jini.service.core;

public class OwnerEntry extends FormmatedEntry {

   private static final long serialVersionUID = 2648767166516408200L;
   public String owner;

   public OwnerEntry() {
      owner = System.getProperty("user.name");
   }

   public String getOwner() {
      return owner;
   }

   public String getFormmatedString() {
      return "Owner : " + owner;
   }
}
