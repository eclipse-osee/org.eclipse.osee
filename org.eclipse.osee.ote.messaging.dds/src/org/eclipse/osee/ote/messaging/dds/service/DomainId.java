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
package org.eclipse.osee.ote.messaging.dds.service;

/**
 * Stores the data necessary to discern virtual DDS networks within a physical network.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class DomainId {
   private long id;

   /**
    * @param id - The id value for this <code>DomainId</code>.
    */
   public DomainId(long id) {
      super();
      this.id = id;
   }

   /**
    * @return Returns the id.
    */
   public long getId() {
      return id;
   }

   public boolean equals(Object obj) {
     
      if (obj instanceof DomainId) {
         DomainId domainId = (DomainId)obj;
         return domainId.id == this.id;
      }
      
      return false;
   }

   public int hashCode() {
      int result = 17;
      result = 1313723*result + (int)(id^(id>>>32));
      
      return result;
   }
}
