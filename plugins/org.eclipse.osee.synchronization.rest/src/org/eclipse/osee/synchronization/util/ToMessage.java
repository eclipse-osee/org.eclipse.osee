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

package org.eclipse.osee.synchronization.util;

public interface ToMessage {

   /**
    * Adds a message to a {@link StringBuilder} representing the contents of the object.
    *
    * @param indent the indent level for the message.
    * @param message when not <code>null</code> the message is appended to this {@link StringBuilder}.
    * @return the provided {@link StringBuilder} when not <code>null</code>; otherwise, a new {@link StringBuilder}.
    */

   StringBuilder toMessage(int indent, StringBuilder message);

}

/* EOF */
