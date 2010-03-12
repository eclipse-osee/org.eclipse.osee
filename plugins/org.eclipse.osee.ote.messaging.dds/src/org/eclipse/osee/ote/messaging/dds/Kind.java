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
package org.eclipse.osee.ote.messaging.dds;

/**
 * Base class for the various *Kind enumerations specified in the DDS specification. This provides
 * the basic structure and accessor methods that are common for these enumerations.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public abstract class Kind{
   private String kindName;
   private long kindId;
   
   /**
    * @param kindName
    * @param kindId
    */
   protected Kind(String kindName, long kindId) {
      super();
      this.kindName = kindName;
      this.kindId = kindId;
   }
   
   public String getKindName() {
      return kindName;
   }
   
   public long getKindId() {
      return kindId;
   }
}
