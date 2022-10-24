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

package org.eclipse.osee.framework.jdk.core.util;

/**
 * Objects implementing this interface provide a method for producing a formatted string representation for debugging.
 *
 * @author Loren K. Ashley
 */

public interface ToMessage {

   /**
    * Adds a message to a {@link Message} representing the contents of the object.
    *
    * @param indent the indent level for the message.
    * @param message when not <code>null</code> the message is appended to the {@link Message} provided by the
    * <code>message</code> parameter.
    * @return the provided {@link Message} when not <code>null</code>; otherwise, a new {@link Message}.
    */

   Message toMessage(int indent, Message message);
}

/* EOF */
