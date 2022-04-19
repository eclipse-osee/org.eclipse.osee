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

package org.eclipse.osee.synchronization.rest.forest;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.synchronization.rest.IdentifierType;
import org.eclipse.osee.synchronization.rest.RootList;
import org.eclipse.osee.synchronization.rest.forest.morphology.AbstractGroveThing;
import org.eclipse.osee.synchronization.rest.forest.morphology.GroveThing;
import org.eclipse.osee.synchronization.util.IndentedString;
import org.eclipse.osee.synchronization.util.ParameterArray;

/**
 * A {@link GroveThing} to represent the header material for a Synchronization Artifact. This class is also used as the
 * native OSEE thing in the Synchronization Artifact DOM.
 *
 * @author Loren K. Ashley
 */

public final class HeaderGroveThing extends AbstractGroveThing implements Id {

   /**
    * Handle to the OSEE ORCS API used to obtain OSEE database information.
    */

   private OrcsApi orcsApi;

   /**
    * The list of OSEE artifacts for Specifications in the Synchronization Artifact.
    */

   private RootList rootList;

   /**
    * Creates a new {@link HeaderGroveThing} object with an unique identifier.
    */

   HeaderGroveThing(GroveThing parent) {
      super(IdentifierType.HEADER.createIdentifier(), 1);

      this.orcsApi = null;
   }

   /**
    * Sets the OSEE ORCS API handle used to get database information.
    *
    * @param orcsApi handle to the OSEE ORCS API
    */

   public void setOrcsApi(OrcsApi orcsApi) {
      assert Objects.nonNull(orcsApi) && Objects.isNull(this.orcsApi);
      this.orcsApi = orcsApi;
   }

   /**
    * Sets the list of OSEE artifacts that are roots for Synchronization Artifact Specifications.
    *
    * @param rootList list of OSEE artifacts by branch and artifact identifiers.
    */

   public void setRootListImpl(RootList rootList) {
      assert Objects.nonNull(rootList) && Objects.isNull(this.rootList);
      this.rootList = rootList;
   }

   /**
    * Returns the numeric portion of the {@link HeaderGroveThing} thing unique identifier as the native OSEE thing
    * identifier.
    *
    * @return an identifier unique among {@link HeaderGroveThing} things.
    */

   @Override
   public Long getId() {
      return this.groveThingKeys[this.groveThingRank - 1].getCount();
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
    * When assertions are enabled an assertion error will be thrown when the <code>nativeThing</code> is not an instance
    * of {@link HeaderGroveThing}.
    */

   @Override
   public boolean validateNativeThings(Object... nativeThings) {
      //@formatter:off
      return
            ParameterArray.validateNonNullAndSize(nativeThings, 1, 1)
         && (nativeThings[0] instanceof HeaderGroveThing);
      //@formatter:on
   }

   /**
    * {@inheritDoc}<br>
    * <br>
    * The generated message is for debugging, there is no contract for the message contents or structure.
    */

   @Override
   public StringBuilder toMessage(int indent, StringBuilder message) {
      var outMessage = (message != null) ? message : new StringBuilder(1 * 1024);
      var indent0 = IndentedString.indentString(indent + 0);
      var indent1 = IndentedString.indentString(indent + 1);

      var name = this.getClass().getName();

      //@formatter:off
      outMessage
         .append( indent0 ).append( name ).append( "\n" )
         .append( indent1 ).append( "ORCS API: ").append( Objects.nonNull( this.orcsApi ) ? "(is set)" : "(is not set)" ).append( "\n" )
         ;
      //@formatter:on

      if (Objects.isNull(this.rootList)) {
         //@formatter:off
         outMessage
            .append( indent1 ).append( "Root List: ").append( "(is not set)" ).append( "\n" )
            ;
         //@formatter:on
      } else {
         this.rootList.toMessage(indent + 1, outMessage);
      }

      return outMessage;
   }

}

/* EOF */