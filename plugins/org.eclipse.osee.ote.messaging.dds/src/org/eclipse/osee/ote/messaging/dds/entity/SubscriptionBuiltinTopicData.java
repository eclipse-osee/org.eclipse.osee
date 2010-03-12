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

import org.eclipse.osee.ote.messaging.dds.listener.TopicListener;
import org.eclipse.osee.ote.messaging.dds.service.TypeSignature;

//UNSURE This class has not been implemented, but is called out in the spec

/**
 * This class is here for future functionality that is described in the DDS specification but has not been implemented or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class SubscriptionBuiltinTopicData extends Topic {

   /**
    * @param participant
    * @param typeName
    * @param name
    * @param enabled
    * @param listener
    * @param parentFactory
    */
   SubscriptionBuiltinTopicData(DomainParticipant participant, TypeSignature typeName, String name, String namespace, boolean enabled, TopicListener listener, EntityFactory parentFactory) {
      super(participant, typeName, name, namespace, enabled, listener, parentFactory);
   }

}
