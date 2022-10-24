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

package org.eclipse.osee.define.operations.synchronization.forest.denizens;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import org.eclipse.osee.define.operations.synchronization.RootList;
import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Class to represent the header material for a Synchronization Artifact. This class is also used as the native OSEE
 * thing in the Synchronization Artifact DOM Header {@link GroveThing}.
 *
 * @author Loren K. Ashley
 */

public final class NativeHeader implements Id, ToMessage {

   /**
    * Saves the identifier for the native header thing.
    */

   Long id;

   /**
    * Handle to the OSEE ORCS API used to obtain OSEE database information.
    */

   private final OrcsApi orcsApi;

   /**
    * The list of OSEE artifacts for Specifications in the Synchronization Artifact.
    */

   private final RootList rootList;

   /**
    * Creates a new {@link NativeHeader} object with an unique identifier.
    *
    * @param id the unique identifier for the {@link NativeHeader}.
    * @param orcsApi the OSEE ORCS API handle used to get database information.
    * @param rootList list of OSEE artifacts by branch and artifact identifiers.
    */

   public NativeHeader(Long id, OrcsApi orcsApi, RootList rootList) {

      this.id = Objects.requireNonNull(id);
      this.orcsApi = Objects.requireNonNull(orcsApi);
      this.rootList = Objects.requireNonNull(rootList);
   }

   /**
    * Returns the identifier of the {@link NativeHeader} instance.
    *
    * @return an identifier unique among {@link NativeHeader} things.
    */

   @Override
   public Long getId() {
      return this.id;
   }

   /**
    * Returns a list of the OSEE root artifacts by branch identifier and artifact identifier pairs.
    *
    * @return the header comment.
    */

   public String getComment() {
      return this.rootList.toText(null).toString();
   }

   /**
    * Get the OSEE database identifier.
    *
    * @return the OSEE database identifier.
    */

   public String getRepositoryId() {
      var jdbcService = this.orcsApi.getJdbcService();
      var jdbcClient = jdbcService.getClient();
      var databaseName = OseeInfo.getValue(jdbcClient, "osee.db");
      if (Objects.isNull(databaseName) || databaseName.isEmpty()) {
         databaseName = "(no-database-name)";
      }
      var databaseId = OseeInfo.getValue(jdbcClient, OseeInfo.DB_ID_KEY);

      return new StringBuilder().append(databaseName).append("( ").append(databaseId).append(" )").toString();
   }

   /**
    * Gets the OSEE server software version identifier.
    *
    * @return the OSEE server software version identifier.
    */

   public String getSourceToolId() {
      return OseeCodeVersion.getVersionId().toString();
   }

   /**
    * Gets the time that Synchronization Artifact was produced.
    *
    * @return the current time.
    */

   public GregorianCalendar getTime() {
      var calendar = new GregorianCalendar();
      calendar.setTime(new Date());

      return calendar;
   }

   /**
    * Gets the title for the Synchronization Artifact.
    *
    * @return the Synchronization Artifact title.
    */

   public String getTitle() {
      return "OSEE Synchronization Artifact";
   }

   /**
    * {@inheritDoc}<br>
    * <br>
    * The generated message is for debugging, there is no contract for the message contents or structure.
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      var name = this.getClass().getName();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( name )
         .indentInc()
         .segment( "ORCS API", this.orcsApi, ( v ) -> Objects.nonNull( v ) ? "(is set)" : "(is not set)" )
         .toMessage( this.rootList )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

}

/* EOF */