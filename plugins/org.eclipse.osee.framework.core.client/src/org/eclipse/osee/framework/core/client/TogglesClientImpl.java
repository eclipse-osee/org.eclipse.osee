/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.client;

import java.util.Objects;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.define.rest.api.toggles.TogglesEndpoint;
import org.eclipse.osee.framework.core.util.toggles.ToggleAccessor;
import org.eclipse.osee.framework.core.util.toggles.TogglesFactory;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;

public class TogglesClientImpl {

   /**
    * Save a reference to the single instance of the {@link TogglesClientImpl} class.
    */

   private static TogglesClientImpl togglesClientImpl = null;

   /**
    * Save a reference to the {@link TogglesEndpoint} for making REST API calls.
    */

   private final TogglesEndpoint togglesEndpoint;

   /**
    * Constructor is private to prevent multiple instantiations of the class.
    */

   private TogglesClientImpl(TogglesEndpoint togglesEndpoint) {
      this.togglesEndpoint = togglesEndpoint;
   }

   /**
    * Factory method to create the single {@link TogglesClientImpl} instance. This method is called by the
    * {@link TogglesFactory} when running on the client.
    *
    * @return the single {@link TogglesClientImpl} instance.
    */

   public synchronized static TogglesClientImpl create(TogglesEndpoint togglesEndpoint) {
      //@formatter:off
      return
         Objects.isNull( TogglesClientImpl.togglesClientImpl )
            ? ( TogglesClientImpl.togglesClientImpl = new TogglesClientImpl(Objects.requireNonNull(togglesEndpoint)) )
            : TogglesClientImpl.togglesClientImpl;
      //@formatter:on
   }

   /**
    * Gets the value for the toggle specified by <code>name</code>.
    *
    * @return the toggle's value as a {@link Boolean}.
    */

   public String getDataBaseToggle(String name) {
      try {
         return this.togglesEndpoint.getToggle(name);
      } catch (Exception e) {
         throw new OseeWebApplicationException(e, Status.INTERNAL_SERVER_ERROR, "Exception in \"getNoTags\" request.");
      }
   }

   public static ToggleAccessor getDataBaseToggleAccessor() {

      if (Objects.isNull(TogglesClientImpl.togglesClientImpl)) {
         TogglesClientImpl.create(null);
      }

      //@formatter:off
      return
         new ToggleAccessor() {

            @Override
            public String getToggle(String name) {
               return TogglesClientImpl.togglesClientImpl.getDataBaseToggle(name);
            }

            @Override
            public String toString() {
               return "Client dataBaseToggleAccessor";
            }

      };
      //@formatter:on
   }

}

/* EOF */
