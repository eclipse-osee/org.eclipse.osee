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
package org.eclipse.osee.ote.messaging.dds.entity;

import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.StatusKind;
import org.eclipse.osee.ote.messaging.dds.condition.StatusCondition;
import org.eclipse.osee.ote.messaging.dds.listener.Listener;

/**
 * Base class for all <code>Entity</code> objects. Provides common functionality needed for all Entities such as listeners, enabled, etc.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public abstract class Entity {

   private EntityFactory parentFactory;
   private Listener listener;
   
   @SuppressWarnings("unused")
   private StatusKind statusMask; // DONT_NEED This has not been implemented, but is called out in the spec
   private StatusCondition statusCondition; //DONT_NEED This has not been implemented, but is called out in the spec
   private StatusKind[] statusChanges; //DONT_NEED This has not been implemented, but is called out in the spec
   private boolean enabled;

   /**
    * Creates a default <code>Entity</code> with enabled set as passed, the listener attached, and a reference to the parent creating this.
    * 
    * @param enabled The value to set for enabled. If true, <code>enabled()</code> is run.
    * @param listener The listener to be attached to this.
    * @param parentFactory The parent which is creating this.
    */
   public Entity(boolean enabled, Listener listener, EntityFactory parentFactory) {
      super();

      this.parentFactory = parentFactory;
      this.listener = listener;

      // DONT_NEED This has not been implemented, but is called out in the spec
      this.statusMask = null;
      this.statusCondition = null;
      this.statusChanges = null;

      // Default enabled to false, if it is passed as true then call enable() to set this as enabled.
      this.enabled = false; // This is correct
      if (enabled)
         this.enable();
   }

   /**
    * Sets the <code>Listener</code> attached to this <code>Entity</code>. If a listener is already set, this will replace it.
    * <p>
    * PARTIAL - The statusMask is not currently being used.
    * 
    * @param listener The listener to attach to this.
    * @param statusMask The mask for this listener
    * @return {@link ReturnCode#OK}
    */
   protected ReturnCode setBaseListener(Listener listener, StatusKind statusMask) {
      this.listener = listener;
      this.statusMask = statusMask;

      return ReturnCode.OK;
   }

   /**
    * Gets the attached listener
    * 
    * @return The <code>Listener</code> attached to this <code>Entity</code>.
    */
   protected Listener getBaseListener() {
      return listener;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been implemented or used.
    */
   public StatusCondition getStatusCondition() {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();
      return statusCondition;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been implemented or used.
    */
   public StatusKind[] getStatusChanges() {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();
      return statusChanges;
   }

   /**
    * Gets the enabled status.
    * 
    * @return Returns <b>true </b> if this <code>Entity</code> has been enabled, otherwise <b>false </b>.
    */
   public boolean isEnabled() {
      return enabled;
   }

   /**
    * Enables this entity. This method is idempotent. Note that the creating factory for this entity must be enabled.
    * 
    * @return {@link ReturnCode#OK}if successful, or if this was previously enabled. {@link ReturnCode#PRECONDITION_NOT_MET}if the creating factory is not
    *         enabled.
    */
   public ReturnCode enable() {

      // Check pre-conditions
      if (!parentFactory.isEnabled())
         return ReturnCode.PRECONDITION_NOT_MET;

      // If the entity is already enabled, then do nothing and return
      if (enabled)
         return ReturnCode.OK;

      enabled = true;
      return ReturnCode.OK;
   }
}
