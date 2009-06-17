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
package org.eclipse.osee.ote.messaging.dds.condition;

import java.util.Collection;

import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.entity.Entity;

/**
 * This class is here for future functionality that is described in the DDS specification
 * but has not been implemented or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class StatusCondition extends Condition {

   private Collection<?> enabledStatuses;
   private Entity parentEntity;
   
   /**
    * 
    */
   public StatusCondition(Entity parentEntity) {

      this.parentEntity = parentEntity;
      enabledStatuses = null; // UNSURE find out if this should be something else?
      
      // This class, and the use of it has not been implemented
      throw new NotImplementedException();
   }
   
   public ReturnCode setEnabledStatuses(Collection<?> mask) {
      return ReturnCode.ERROR;
   }
   
   public Collection<?> getEnabledStatuses() {
      return enabledStatuses;
   }
   
   /**
    * @return Returns the entity.
    */
   public Entity getParentEntity() {
      return parentEntity;
   }
}
