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

package org.eclipse.osee.define.operations.publisher.publishing;

import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.jdk.core.util.IndentedString;

/**
 * {@link RuntimeException} which is thrown when a user does not have the required permissions for access to a
 * publishing REST API.
 *
 * @author Loren K. Ashley
 */

public class UserNotAuthorizedForPublishingException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Exception cause flag for a user without any login identifiers.
    */

   public static int NOT_LOGIN_USER = 0x1;

   /**
    * Exception cause flag for a user that is not active.
    */

   public static int NOT_ACTIVE_USER = 0x2;

   /**
    * Exception cause flag for a user that is not in the publishing group.
    */

   public static int NOT_IN_PUBLISHING_GROUP = 0x4;

   /**
    * Creates a new exception with a detailed permissions error message.
    *
    * @param causeFlags integer exception cause flags ORed together for the reasons of the permission denial.
    * @param user the user that was denied permission.
    */

   public UserNotAuthorizedForPublishingException(int causeFlags, UserToken user) {
      super(UserNotAuthorizedForPublishingException.buildMessage(causeFlags, user));
   }

   /**
    * Creates a detailed permissions error message for the denied user.
    *
    * @param causeFlags integer exception cause flags ORed together for the reasons of the permission denial.
    * @param user the user that was denied permission.
    */

   public static String buildMessage(int causeFlags, UserToken user) {

      var indent1 = IndentedString.indentString(1);
      var indent2 = IndentedString.indentString(2);

      //@formatter:off
      var message = new StringBuilder( 1024 )
                           .append( "\n" )
                           .append( "User not authorized for access to publishing REST APIs." ).append( "\n" )
                           .append( indent1 ).append( "Reasons follow:" ).append( "\n" );
      //@formatter:on

      if ((UserNotAuthorizedForPublishingException.NOT_LOGIN_USER & causeFlags) > 0) {
         message.append(indent2).append("User is not a login user.").append("\n");
      }

      if ((UserNotAuthorizedForPublishingException.NOT_ACTIVE_USER & causeFlags) > 0) {
         message.append(indent2).append("User is not an active user.").append("\n");
      }

      if ((UserNotAuthorizedForPublishingException.NOT_IN_PUBLISHING_GROUP & causeFlags) > 0) {
         message.append(indent2).append("User is not in the publishing group.").append("\n");
      }

      //@formatter:off
      message
         .append( indent2 ).append( "User:    " ).append( user.getName() ).append( "\n" )
         .append( indent2 ).append( "User Id: " ).append( user.getUserId() ).append( "\n" )
         .append( "\n" );
      //@formatter:on

      return message.toString();
   }
}

/* EOF */
