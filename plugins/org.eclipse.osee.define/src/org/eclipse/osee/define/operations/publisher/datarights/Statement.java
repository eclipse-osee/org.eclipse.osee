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

package org.eclipse.osee.define.operations.publisher.datarights;

import java.util.Arrays;
import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * This class contains a statement in multiple formats for a Required Indicator.
 *
 * @author Loren K. Ashley
 */

public class Statement {

   /**
    * The statement contents in various formats. Any entry in text format is required.
    */

   StatementBody[] statementBody;

   /**
    * The name of the Required Indicator Statement.
    */

   String statementIndicator;

   /**
    * Creates a new empty {@link Statement} for JSON deserialization.
    */

   public Statement() {
      this.statementIndicator = null;
      this.statementBody = null;
   }

   /**
    * @param statementIndicator
    * @param statementBody
    */

   public Statement(String statementIndicator, StatementBody[] statementBody) {
      this.statementIndicator = statementIndicator;
      this.statementBody = statementBody;
   }

   /**
    * Gets the {@link #statementBody}.
    *
    * @return the statementBody.
    * @throws IllegalStateException when the member {@link #statementBody} has already been set.
    */

   public StatementBody[] getStatementBody() {
      if (Objects.isNull(this.statementBody)) {
         throw new IllegalStateException(
            "Statement::getStatementBody, the member \"this.statementBody\" has not yet been set.");
      }

      return this.statementBody;
   }

   /**
    * Gets the {@link #statementIndicator}.
    *
    * @return the statementIndicator.
    * @throws IllegalStateException when the member {@link #statementIndicator} has already been set.
    */

   public String getStatementIndicator() {
      if (Objects.isNull(this.statementIndicator)) {
         throw new IllegalStateException(
            "Statement::getStatementIndicator, the member \"this.statementIndicator\" has not yet been set.");
      }

      return this.statementIndicator;
   }

   /**
    * Predicate to determine the validity of the {@link Statement}. The following checks are done:
    * <ul>
    * <li>The member {@link #statementIndicator} is non-<code>null</code> and non-blank.</li>
    * <li>The member {@link #statementBody} is non-<code>null</code>, non-empty, and all {@link Statement} objects are
    * valid.</li>
    * <li>The member {@link #statementBody} contains one {@link Statement} with a format type of "text".</li>
    * </ul>
    *
    * @return <code>true</code>, when the validity check passes; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      boolean[] gotText = {false};
      //@formatter:off
      return
            Strings.isValidAndNonBlank( this.statementIndicator )
         && (
                  Objects.nonNull( this.statementBody )
               && ( this.statementBody.length > 0 )
               && Arrays.stream( this.statementBody )
                     .peek( ( statementBody ) -> gotText[0] |= statementBody.getFormatIndicator().equals( "text" ) )
                     .allMatch( StatementBody::isValid )
               && gotText[0]
            );
      //@formatter:on
   }

   /**
    * Sets the member {@link #statementBody}.
    *
    * @param statementBody the statementBody to set.
    * @throws NullPointerException when the parameter <code>statementBody</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #statementBody} has already been set.
    */

   public void setStatementBody(StatementBody[] statementBody) {
      if (Objects.nonNull(this.statementBody)) {
         throw new IllegalStateException(
            "Statement::setStatementBody, member \"this.statementBody\" has already been set.");
      }

      this.statementBody = Objects.requireNonNull(statementBody,
         "Statement::setStatementBody, parameter \"statementBody\" cannot be null.");
   }

   /**
    * Sets the member {@link #statementIndicator}.
    *
    * @param statementIndicator the statementIndicator to set.
    * @throws NullPointerException when the parameter <code>statementIndicator</code> is <code>null</code>.
    * @throws IllegalStateException when the member {@link #statementIndicator} has already been set.
    */

   public void setStatementIndicator(String statementIndicator) {
      if (Objects.nonNull(this.statementIndicator)) {
         throw new IllegalStateException(
            "Statement::setStatementIndicator, member \"this.statementIndicator\" has already been set.");
      }

      this.statementIndicator = Objects.requireNonNull(statementIndicator,
         "Statement::setStatementIndicator, parameter \"statementIndicator\" cannot be null.");
   }

}

/* EOF */
