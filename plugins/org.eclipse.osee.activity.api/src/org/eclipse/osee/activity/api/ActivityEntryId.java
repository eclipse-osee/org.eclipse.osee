/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.activity.api;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Ryan D. Brooks
 */
@XmlRootElement
public class ActivityEntryId implements Identity<Long> {

   private Long id;

   public void setGuid(long id) {
      this.id = id;
   }

   @Override
   public Long getGuid() {
      return id;
   }

   @Override
   public int hashCode() {
      return getGuid().hashCode();
   }

   @SuppressWarnings("unchecked")
   @Override
   public boolean equals(Object obj) {
      boolean equal = false;
      if (obj instanceof Identity) {
         Identity<Long> identity = (Identity<Long>) obj;
         if (getGuid() == identity.getGuid()) {
            equal = true;
         } else if (getGuid() != null) {
            equal = getGuid().equals(identity.getGuid());
         }
      }
      return equal;
   }

   @Override
   public String toString() {
      return "ActivityEntryId [id=" + id + "]";
   }
}
