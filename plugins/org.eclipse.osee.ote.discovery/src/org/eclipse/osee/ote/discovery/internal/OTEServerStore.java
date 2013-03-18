package org.eclipse.osee.ote.discovery.internal;

import java.util.Collection;

import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

public interface OTEServerStore {

   void add(ServiceHealth serviceHealth);
   void remove(ServiceHealth serviceHealth);
   public Collection<ServiceHealth> getAll();
}
