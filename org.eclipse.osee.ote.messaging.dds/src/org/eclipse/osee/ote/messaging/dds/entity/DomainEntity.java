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

import org.eclipse.osee.ote.messaging.dds.listener.Listener;


/**
 * The base class which all of the entity classes in the DDS system extend,
 * except for the <code>DomainParticipant</code>. This intermediate class
 * is in place simply to clarify that a <code>DomainParticipant</code> can
 * contain any other type of entity, except another <code>DomainParticipant</code>.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public abstract class DomainEntity extends Entity {

   /**
    * @param enabled
    * @param parentFactory
    */
   public DomainEntity(boolean enabled, Listener listener, EntityFactory parentFactory) {
      super(enabled, listener, parentFactory);
   }
}
