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

package org.eclipse.osee.define.operations.api.publisher.dataaccess;

import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * The {@link RuntimeException} class created for errors by the {@link DataAccessOperations} class methods.
 *
 * @author Loren K. Ashley
 */

public class DataAccessException extends RuntimeException {

   /**
    * Default serialization identifier.
    */

   private static final long serialVersionUID = 1L;

   /**
    * The {@link DataAccessOperations.Cause} of the error.
    */

   private final Cause dataAccessCause;

   /**
    * Creates a new {@link DataAccessException} with a {@link DataAccessOperations.Cause} and the causing
    * {@link Throwable}.
    *
    * @param title a title description of the error.
    * @param DataAccessCause the {@link Cause} of the error.
    * @param throwable the {@link Throwable} causing the error.
    * @throws NullPointerException when <code>DataAccessCause</code> or <code>throwable</code> are <code>null</code>.
    */

   public DataAccessException(CharSequence title, Cause DataAccessCause, Throwable throwable) {
      super(DataAccessException.buildMessage(title, DataAccessCause, throwable));
      this.dataAccessCause = Objects.requireNonNull(DataAccessCause);
      this.initCause(Objects.requireNonNull(throwable));
   }

   /**
    * Creates a new {@link DataAccessException} with a {@link DataAccessOperations.Cause}.
    *
    * @param title a title description of the error.
    * @param DataAccessCause the {@link Cause} of the error.
    * @throws NullPointerException when <code>DataAccessCause</code> is <code>null</code>.
    */

   public DataAccessException(CharSequence title, Cause DataAccessCause) {
      super(DataAccessException.buildMessage(title, DataAccessCause, null));
      this.dataAccessCause = Objects.requireNonNull(DataAccessCause);
   }

   /**
    * Creates a new {@link DataAccessException} with a {@link DataAccessOperations.Cause} of
    * {@link DataAccessOperations.Cause#ERROR} and the causing {@link Throwable}.
    *
    * @param title a title description of the error.
    * @param throwable the {@link Throwable} causing the error.
    * @throws NullPointerException when <code>throwable</code> is <code>null</code>.
    */

   public DataAccessException(CharSequence title, Throwable throwable) {
      super(DataAccessException.buildMessage(title, Cause.ERROR, throwable));
      this.dataAccessCause = Cause.ERROR;
      this.initCause(Objects.requireNonNull(throwable));
   }

   /**
    * Gets the {@link Cause} for the exception.
    *
    * @return the {@link Cause}.
    */

   public Cause getPublishingUtilCause() {
      return this.dataAccessCause;
   }

   public boolean isError() {
      return this.dataAccessCause.equals(Cause.ERROR);
   }

   public boolean isMoreThanOne() {
      return this.dataAccessCause.equals(Cause.MORE_THAN_ONE);
   }

   public boolean isNotFound() {
      return this.dataAccessCause.equals(Cause.NOT_FOUND);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param title a title description of the error.
    * @param DataAccessCause the {@link Cause} for the exception.
    * @param throwable the exception that caused the Publishing Utils error. This parameter may be <code>null</code>.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(CharSequence title, Cause dataAccessCause, Throwable throwable) {
      //@formatter:off
      return
         new Message()
                .title( title )
                .indentInc()
                .segment( "Publishing Utils Cause", dataAccessCause )
                .reasonFollowsIfNonNull( throwable )
                .toString();
      //@formatter:on
   }
}

/* EOF */
