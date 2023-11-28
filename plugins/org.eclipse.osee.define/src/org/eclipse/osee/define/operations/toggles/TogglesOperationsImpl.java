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

package org.eclipse.osee.define.operations.toggles;

import java.util.Objects;
import org.eclipse.osee.define.operations.api.toggles.TogglesOperations;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.core.util.toggles.ToggleAccessor;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;

/**
 * Implementation of the {@link TogglesOperations} interface contains the methods that are invoked when a REST API call
 * has been made for a toggle value.
 *
 * @author Loren K. Ashley
 */

public class TogglesOperationsImpl implements TogglesOperations {

   /**
    * Save the single instance of the {@link TogglesOperationsImpl}.
    */

   private static TogglesOperationsImpl togglesOperationsImpl = null;

   /**
    * Saves the {@link JdbcClient} handle.
    */

   private final JdbcClient jdbcClient;

   /**
    * Creates an object to process toggle REST calls.
    *
    * @param jdbcService the {@link JdbcService} handle.
    */

   private TogglesOperationsImpl(JdbcService jdbcService) {
      this.jdbcClient = Objects.requireNonNull(jdbcService.getClient());
   }

   /**
    * Gets or creates the single instance of the {@link TogglesOperationsImpl} class.
    *
    * @param jdbcService a reference to the {@link JdbcClient}.
    * @return the single {@link TogglesOperationsImpl} object.
    * @throws NullPointerException when the parameter <code>jdbcService</code> is <code>null</code> and the single
    * instance of the {@link TogglesOperationsImpl} has not yet been created.
    */

   public synchronized static TogglesOperationsImpl create(JdbcService jdbcService) {

      //@formatter:off
      return
         Objects.isNull( TogglesOperationsImpl.togglesOperationsImpl )
            ? TogglesOperationsImpl.togglesOperationsImpl = new TogglesOperationsImpl( Objects.requireNonNull( jdbcService ) )
            : TogglesOperationsImpl.togglesOperationsImpl;
      //@formatter:on
   }

   public static ToggleAccessor getDataBaseToggleAccessor() {

      //@formatter:off
      return
         new ToggleAccessor() {

            @Override
            public String getToggle(String name) {
               return TogglesOperationsImpl.togglesOperationsImpl.getDataBaseToggle(name);
            }

            @Override
            public String toString() {
               return "Server dataBaseToggleAccessor";
            }

      };
      //@formatter:on

   }

   /**
    * Gets the value of a toggle.
    *
    * @param name the name of the toggle to get.
    * @return the value of the toggle as a {@link String}.
    */

   @Override
   public String getDataBaseToggle(String name) {
      return OseeInfo.getValue(jdbcClient, name);
   }

}

/* EOF */