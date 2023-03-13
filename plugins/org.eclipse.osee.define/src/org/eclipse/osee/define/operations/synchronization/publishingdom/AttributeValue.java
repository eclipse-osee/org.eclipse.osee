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

/**
 * The interface for DOM nodes that represent an attribute definition (Synchronization Artifact Attribute Value).
 *
 * @author Loren K. Ashley
 */

public interface AttributeValue extends Node {

   /**
    * Gets the string representation of the attribute value.
    *
    * @return the attribute value.
    */

   public String getValue();

   /**
    * Sets the string representation of the attribute value.
    *
    * @param value the attribute value.
    */

   public void setValue(String value);

}

/* EOF */
