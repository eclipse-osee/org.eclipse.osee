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

import org.eclipse.osee.define.operations.synchronization.identifier.Identifier;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * {@link RuntimeException} which is thrown a {@link Node} is not of the expected class.
 *
 * @author Loren K. Ashley
 */

public class NodeWrongTypeException extends RuntimeException {

   /**
    * Serialization version identifier
    */

   private static final long serialVersionUID = 1L;

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Node} with an unexpected class.
    *
    * @param nodeIdentifier the {@link Node} {@link Identifier}.
    * @param node the {@link Node}.
    * @param expectedNodeClass the expected class for the {@link Node}.
    */

   public NodeWrongTypeException(Identifier nodeIdentifier, Node node, Class<?> expectedNodeClass) {
      super(NodeWrongTypeException.buildMessage(nodeIdentifier, node, expectedNodeClass));
   }

   /**
    * Creates a new {@link RuntimeException} with a message describing the {@link Node} with an unexpected class.
    *
    * @param nodeIdentifier the {@link Node} {@link Identifier}.
    * @param node the {@link Node}.
    * @param expectedNodeClass the expected class for the {@link Node}.
    * @param cause the {@link Throwable} which led to this exception being thrown. This parameter maybe
    * <code>null</code>.
    */

   public NodeWrongTypeException(Identifier nodeIdentifier, Node node, Class<?> expectedNodeClass, Throwable cause) {
      this(nodeIdentifier, node, expectedNodeClass);

      this.initCause(cause);
   }

   /**
    * Builds an error message {@link String} describing the exception.
    *
    * @param nodeIdentifier the {@link Node} {@link Identifier}.
    * @param node the {@link Node}.
    * @param expectedNodeClass the expected class for the {@link Node}.
    * @return a {@link String} message describing the exception condition.
    */

   public static String buildMessage(Identifier nodeIdentifier, Node node, Class<?> expectedNodeClass) {
      //@formatter:off
      return
         new Message()
                .title( "Node is not of the expected type." )
                .indentInc()
                .segment( "Expected Node Class", expectedNodeClass.getName() )
                .segment( "Node Identifier",     nodeIdentifier              )
                .segment( "Node Class",          node.getClass().getName()   )
                .toString()
                ;
      //@formatter:on
   }
}

/* EOF */
