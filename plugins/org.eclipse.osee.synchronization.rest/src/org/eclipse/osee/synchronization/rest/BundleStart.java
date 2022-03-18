/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.synchronization.rest;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.synchronization.api.SynchronizationEndpoint;

/**
 * This class is both an OSGI service component and an {@link javax.ws.rs.core.Application}. The OSGI service component
 * performs the OSGI bundle activation which creates a single instance of the {@link SynchronizationEndpoint} used to
 * process the HTTP requests for the "synchronization" end point. The class also implement the
 * {@link javax.ws.rs.core.Application} interface which provides the {@link SynchronizationEndpoint} implementation as a
 * resource to the JAX-RS server.
 *
 * @author Loren K. Ashley
 */

@ApplicationPath("synchronization")
public class BundleStart extends Application {

   /**
    * Saves a reference to the Logger service for the bundle.
    */

   private Log logger;

   /**
    * Saves a reference to the Object Revisions Control System service for the bundle.
    */

   private OrcsApi orcsApi;

   /**
    * To implement the {@link Application} interface a {@link Set} of the resources used to process HTTP requests for
    * the "synchronization" end point is returned by the {#getSingletons} method. A single set is created and returned
    * for each call to {@link #getSingletons} to reduce garbage production.
    */

   private Set<Object> singletons;

   /**
    * Saves an implementation of the {@link SynchronizationEndpoint} interface used to process the HTTP requests for the
    * Synchronization end point.
    */

   private SynchronizationEndpoint synchronizationEndpoint;

   /**
    * Creates a new combination OSGI service component object and {@link javax.ws.rs.core.Application} object.
    */

   public BundleStart() {
      this.singletons = null;
      this.synchronizationEndpoint = null;
      this.orcsApi = null;
      this.logger = null;
   }

   /**
    * OSGI service binding method, this method is called before activation with the Object Revision Control System
    * service object.
    *
    * @param orcsApi the {@link OrcsApi} service object.
    */

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * OSGI service binding method, this method is called before activation with the logger service object.
    *
    * @param logger the {@link Log} service object.
    */

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   /**
    * The OSGI Component <code>activate</code> method, this method is called as the last step in the component
    * activation. This method creates an instance of the {@link SynchronizationEndpointImpl} that will be called to
    * process the REST API calls for the Synchronization end point.
    */

   public void start() {
      assert Objects.nonNull(this.orcsApi) && Objects.nonNull(this.logger);

      this.synchronizationEndpoint = new SynchronizationEndpointImpl(this.orcsApi);

      this.singletons = new AbstractSet<Object>() {

         @Override
         public boolean add(Object object) {
            throw new UnsupportedOperationException();
         }

         @Override
         public Iterator<Object> iterator() {

            return new Iterator<Object>() {

               private boolean first = true;

               @Override
               public boolean hasNext() {
                  return this.first;
               }

               @Override
               public Object next() {
                  if (this.first) {
                     this.first = false;
                     return BundleStart.this.synchronizationEndpoint;

                  } else {
                     throw new NoSuchElementException();
                  }

               }

            };
         }

         @Override
         public int size() {
            return 1;
         }

      };

      logger.warn("Synchronization Artifact Application Started - %s", System.getProperty("OseeApplicationServer"));
   }

   /**
    * Returns a {@link Set} containing the resource to process HTTP requests for the "synchronization" end point.
    *
    * @return a {@link Set} containing an instance of the {@link SynchronizationEndpointImpl} class.
    */

   @Override
   public Set<Object> getSingletons() {
      return this.singletons;
   }

}

/* EOF */
