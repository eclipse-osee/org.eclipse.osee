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
package org.eclipse.osee.define.rest.api.publisher.datarights;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * An array of title, header, and footer statements all associated with a unique integer. The statements are referenced
 * by their unique associated identifiers. Statements in the array are in the format indicated by the format-indicator.
 *
 * @author Md I. Khan
 */

public class StatementMap implements ToMessage {

   /**
    * A unique integer identifier for the associated statement.
    */

   private Integer identifier;

   /**
    * A title, header, or footer statement in the format indicated by the format-indicator of the
    * statement-for-response.
    */

   private StatementForResponse statement;

   /**
    * Creates a new empty {@link StatementMap} object for JSON deserialization
    */

   public StatementMap() {
      this.identifier = null;
      this.statement = null;
   }

   /**
    * Creates a new {@link StatementMap} object with data for JSON serialization
    *
    * @param identifier a unique identifier as an {@link Integer}
    * @param statement statement in the format indicated by the format-indicator as an object of
    * {@link StatementForResponse}
    * @throws NullPointerException when any of the parameters are null
    */

   public StatementMap(Integer identifier, StatementForResponse statement) {
      this.identifier =
         Objects.requireNonNull(identifier, "StatementMap::new, parameter \"idenfitier\" cannot be null.");
      this.statement = Objects.requireNonNull(statement, "StatementMap::new, parameter \"statement\" cannot be null.");
   }

   /**
    * Gets the identifier
    *
    * @return identifier of type {@link Integer}
    * @throws IllegalStateException when {@link #identifier} has not been set
    */

   public Integer getIdentifier() {
      if (Objects.isNull(this.identifier)) {
         throw new IllegalStateException("StatementMap::getIdentifier, member \"identifier\" has not been set.");
      }
      return this.identifier;
   }

   /**
    * Gets the statement
    *
    * @return statement of type {@link StatementForResponse}
    * @throws IllegalStateException when {@link #statement} has not been set
    */

   public StatementForResponse getStatement() {
      if (Objects.isNull(this.statement)) {
         throw new IllegalStateException("StatementMap::getStatement, member \"statement\" has not been set.");
      }
      return this.statement;
   }

   /**
    * Predicates to test the validity of {@link StatementMap} object
    *
    * @return <code>true</code> when all members are non-<code>null</code>; otherwise <code>false</code>
    */

   @JsonIgnore
   public boolean isValid() {
      //@formatter:off
      return
         Objects.nonNull(this.identifier) &&
         Objects.nonNull(this.statement) && this.statement.isValid();
      //@formatter:on
   }

   /**
    * Sets the identifier
    *
    * @param identifier a unique identifier as an {@link Integer}
    * @throws IllegalStateException when the member {@link #identifier} has already been set
    * @throws NullPointerException when the parameter <code>identifier</code> is <code>null</code>
    */

   public void setIdentifier(Integer identifier) {
      if (Objects.nonNull(this.identifier)) {
         throw new IllegalStateException("StatementMap::setIdentifier, member \"identifier\" has already been set.");
      }
      this.identifier =
         Objects.requireNonNull(identifier, "StatementMap::setIdentifier, parameter \"identifier\" cannot be null.");
   }

   /**
    * Sets the statement
    *
    * @param statement statement in the format indicated by the format-indicator as an object of
    * {@link StatementForResponse}
    * @throws IllegalStateException when the member {@link #statement} has already been set
    * @throws NullPointerException when the parameter <code>statement</code> is <code>null</code>
    */

   public void setStatement(StatementForResponse statement) {
      if (Objects.nonNull(this.statement)) {
         throw new IllegalStateException("StatementMap::setStatement, member \"statement\" has already been set.");
      }
      this.statement =
         Objects.requireNonNull(statement, "StatementMap::setStatement, parameter \"statement\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = (message != null) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Statement Map" )
         .indentInc()
         .segment( "Identifier",   this.identifier   )
         .segment( "Statement", this.statement )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, (Message) null).toString();
   }

}
/* EOF */