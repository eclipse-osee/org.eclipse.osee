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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.listener.DomainParticipantListener;
import org.eclipse.osee.ote.messaging.dds.service.DomainId;

/**
 * This is the entry point of the DDS system. This is a singleton factory class used by the application to create <code>DomainParticipant</code> 's.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class DomainParticipantFactory implements EntityFactory {
   private static DomainParticipantFactory factory = null;
   private Map<DomainId, Collection<DomainParticipant>> domainMap;

   private DomainParticipantFactory() {
      // This map is very important since data must be sent to all
      domainMap = Collections.synchronizedMap(new HashMap<DomainId, Collection<DomainParticipant>>(10, .75f));
   }

   /**
    * This is a singleton class so this will always return the same DomainParticipantFactory.
    * 
    * @return The instance of the <code>DomainParticipantFactory</code>
    */
   public static DomainParticipantFactory getInstance() {
      if (factory == null)
         factory = new DomainParticipantFactory();

      return factory;
   }

   /**
    * Creates a <code>DomainParticipant</code> that belongs to this <code>DomainParticipantFactory</code>
    * 
    * @param domainId The ID of the domain that the <code>DomainParticipant</code> should belong to. All participants will receive publications from any
    *           publisher in the domain.
    * @param listener The listener to attach to the <code>DomainParticipant</code>.
    * @param threadedPublishing <b>true </b> if the participant should use a separate thread for processing publications.
    * @return A DomainParticipant in the domainId with provided listener attached.
    */
   public DomainParticipant createParticipant(DomainId domainId, DomainParticipantListener listener, boolean threadedPublishing) {

      Collection<DomainParticipant> domain = domainMap.get(domainId);

      // If there is not a collection for this ID yet, then make one and add it to the hash
      if (domain == null) {
         domain = Collections.synchronizedList(new ArrayList<DomainParticipant>(1));
         domainMap.put(domainId, domain);
      }

      // Get a new DomainParticipant
      DomainParticipant participant = new DomainParticipant(domainId, domain, this.isEnabled(), listener, this, threadedPublishing);

      // Add the participant to that domain collection
      domain.add(participant);

      return participant;
   }

   /**
    * Removes a <code>DomainParticipant</code> from the <code>DomainParticipantFactory</code>. If the participant contains entities the method will fail.
    * This can be satisfied by calling {@link DomainParticipant#deleteContainedEntities()}on the participant.
    * 
    * @param participant The participant to be removed.
    * @return {@link ReturnCode#OK}if successful, otherwise {@link ReturnCode#PRECONDITION_NOT_MET}.
    */
   public ReturnCode deleteParticipant(DomainParticipant participant) {
      // Check the pre-conditions
      // NOTE: This can be satisfied by calling deleteContainedEntities() on the participant
      if (participant.hasEntities())
         return ReturnCode.PRECONDITION_NOT_MET;

      // Remove the participant and return OK status
      Collection<DomainParticipant> domain = domainMap.get(participant.getDomainId());

      // If the domain doesn't exist, return ALREADY_DELETED
      if (domain == null)
         return ReturnCode.ALREADY_DELETED;

      // Remove the participant from the domain
      domain.remove(participant);

      // If the domain is empty now, then remove it from the map
      if (domain.isEmpty())
         domainMap.remove(participant.getDomainId());

      return ReturnCode.OK;
   }

   /**
    * Finds a <code>DomainParticipant</code> that belongs to the domain passed. If multiple <code>DomainPariticpant</code>'s are in the domain, any one of
    * them may be returned.
    * 
    * @param domainId The domain to find a <code>DomainParticipant</code> in.
    * @return A <code>DomainParticipant</code> whose domainId matches the id provided, or NULL if the factory does not contain a participant in the provided
    *         domain.
    */
   public DomainParticipant lookupParticipant(DomainId domainId) {

      // Attempt to find a matching participant
      Collection<DomainParticipant> domain = domainMap.get(domainId);

      // If the domain exists, then get the first participant from the domain
      if (domain != null)
         return (DomainParticipant) domain.iterator().next();
      // Otherwise, no such participant exists
      else
         return null;
   }

   public boolean isEnabled() {
      // This is always true because DomainParticipantFactory is never disabled nor disableable.
      return true;
   }
   
   public static void dispose() {
      for (Collection<DomainParticipant> collection : factory.domainMap.values()) {
         for (DomainParticipant p : collection) {
            p.dispose();
         }
      }
      factory.domainMap.clear();
   }
}
