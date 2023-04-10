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

package org.eclipse.osee.framework.skynet.core.httpRequests;

import java.util.Objects;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.define.api.toggles.TogglesEndpoint;
import org.eclipse.osee.framework.core.util.toggles.Toggles;
import org.eclipse.osee.framework.core.util.toggles.TogglesFactory;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;

public class TogglesClientImpl implements Toggles {

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

   private TogglesClientImpl() {
      this.togglesEndpoint = ServiceUtil.getOseeClient().getTogglesEndpoint();
   }

   /**
    * Factory method to create the single {@link TogglesClientImpl} instance. This method is called by the
    * {@link TogglesFactory} when running on the client.
    *
    * @return the single {@link TogglesClientImpl} instance.
    */

   public synchronized static TogglesClientImpl create() {
      //@formatter:off
      return
         Objects.isNull( TogglesClientImpl.togglesClientImpl )
            ? ( TogglesClientImpl.togglesClientImpl = new TogglesClientImpl() )
            : TogglesClientImpl.togglesClientImpl;
      //@formatter:on
   }

   /**
    * Gets the value for the toggle specified by <code>name</code>.
    *
    * @return the toggle's value as a {@link Boolean}.
    */

   @Override
   public Boolean apply(String name) {
      try {
         return this.togglesEndpoint.apply(name);
      } catch (Exception e) {
         throw new OseeWebApplicationException(e, Status.INTERNAL_SERVER_ERROR, "Exception in \"getNoTags\" request.");
      }
   }

}

/* EOF */
