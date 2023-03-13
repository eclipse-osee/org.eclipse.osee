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

package org.eclipse.osee.define.operations.synchronization.publishingdom;

import java.util.Objects;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * {@link RuntimeException} thrown when a trying to use a {@link Node} with a DOM it was not created for.
 *
 * @author Loren K. Ashley
 */

public class NodeNotMemberOfDomException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message explaining the {@link Node} is not a member of the DOM it
    * was attempted to be used with.
    *
    * @param node the {@link Node}.
    * @param documentMap the root {@link Node} of the DOM.
    * @param message additional context for the exception message. This parameter maybe <code>null</code>.
    */

   public NodeNotMemberOfDomException(Node node, Node documentMap, String message) {
      super(NodeNotMemberOfDomException.buildMessage(node, documentMap, message));
   }

   /**
    * Creates a new {@link RuntimeException} with a message explaining the {@link Node} is not a member of the DOM it
    * was attempted to be used with.
    *
    * @param node the {@link Node}.
    * @param documentMap the root {@link Node} of the DOM.
    * @param message additional context for the exception message. This parameter maybe <code>null</code>.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public NodeNotMemberOfDomException(Node node, Node documentMap, String message, Throwable cause) {
      this(node, documentMap, message);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param node the {@link Node}.
    * @param documentMap the root {@link Node} of the DOM.
    * @param message additional context for the exception message. This parameter maybe <code>null</code>.
    */

   public static String buildMessage(Node node, Node documentMap, String message) {
      //@formatter:off
      var exceptionMessage =
         new Message()
                .title( "Node is not a member of the DOM." )
                ;

      if( Objects.nonNull( message ) ) {
         exceptionMessage.title( message );
      }

      exceptionMessage
         .indentInc()
         .segment( "Node", node.getIdentifier()        )
         .segment( "DOM",  documentMap.getIdentifier() )
         ;
      //@formatter:on

      return exceptionMessage.toString();
   }
}

/* EOF */
