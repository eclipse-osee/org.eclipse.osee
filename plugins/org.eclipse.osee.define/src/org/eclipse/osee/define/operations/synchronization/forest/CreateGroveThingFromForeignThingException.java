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

package org.eclipse.osee.define.operations.synchronization.forest;

import org.eclipse.osee.define.operations.synchronization.ForeignThingFamily;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * {@link RuntimeException} which is thrown when a {@link GroveThing} cannot be created from a
 * {@link ForeignThingFamily}.
 *
 * @author Loren K. Ashley
 */

public class CreateGroveThingFromForeignThingException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the failure to create a {@link GroveThing} from a
    * {@link ForeignThingFamily}.
    *
    * @param reason description of the failure reason.
    * @param foreignThingFamily the {@link ForeignThingFamily} the {@link GroveThing} was being created for.
    */

   public CreateGroveThingFromForeignThingException(String reason, ForeignThingFamily foreignThingFamily) {
      super(CreateGroveThingFromForeignThingException.buildMessage(reason, foreignThingFamily));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the failure to create a {@link GroveThing} from a
    * {@link ForeignThingFamily}.
    *
    * @param reason description of the failure reason.
    * @param stringForeignKey the foreign key that caused the failure.
    * @param foreignThingFamily the {@link ForeignThingFamily} the {@link GroveThing} was being created for.
    */

   public CreateGroveThingFromForeignThingException(String reason, String stringForeignKey, ForeignThingFamily foreignThingFamily) {
      super(CreateGroveThingFromForeignThingException.buildMessage(reason, stringForeignKey, foreignThingFamily));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the failure to create a {@link GroveThing} from a
    * {@link ForeignThingFamily}.
    *
    * @param reason description of the failure reason.
    * @param foreignThingFamily the {@link ForeignThingFamily} the {@link GroveThing} was being created for.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public CreateGroveThingFromForeignThingException(String reason, ForeignThingFamily foreignThingFamily, Throwable cause) {
      this(reason, foreignThingFamily);

      this.initCause(cause);
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the failure to create a {@link GroveThing} from a
    * {@link ForeignThingFamily}.
    *
    * @param reason description of the failure reason.
    * @param stringForeignKey the foreign key that caused the failure.
    * @param foreignThingFamily the {@link ForeignThingFamily} the {@link GroveThing} was being created for.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public CreateGroveThingFromForeignThingException(String reason, String stringForeignKey, ForeignThingFamily foreignThingFamily, Throwable cause) {
      this(reason, stringForeignKey, foreignThingFamily);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param reason description of the failure reason.
    * @param foreignThingFamily the {@link ForeignThingFamily} the {@link GroveThing} was being created for.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(String reason, ForeignThingFamily foreignThingFamily) {
      //@formatter:off
      var message =
         new Message()
                .title( "Forest::createGroveThingFromForeignThing, " ).append( reason ).append( "." )
                .indentInc()
                .title( "Foreign Thing Record" )
                ;
      //@formatter:on

      foreignThingFamily.toMessage(2, message);

      return message.toString();
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param reason description of the failure reason.
    * @param stringForeignKey the foreign key that caused the failure.
    * @param foreignThingFamily the {@link ForeignThingFamily} the {@link GroveThing} was being created for.
    * @return {@link String} message describing the exception condition.
    */

   public static String buildMessage(String reason, String stringForeignKey, ForeignThingFamily foreignThingFamily) {
      //@formatter:off
      var message =
         new Message()
                .title( "Forest::createGroveThingFromForeignThing, " ).append( reason ).append( "." )
                .indentInc()
                .segment( "Foreign Identifier", stringForeignKey )
                .title( "Foreign Thing Record" )
                ;
      //@formatter:on

      foreignThingFamily.toMessage(2, message);

      return message.toString();
   }

}

/* EOF */
