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

package org.eclipse.osee.define.operations.api.toggles;

/**
 * This interface defines the method for obtaining a Toggle value. This interface is an extension of the
 * {@link Function} interface with a {@link String} input for the toggle name and a {@link Boolean} output for the
 * toggle value.
 *
 * @author Loren K. Ashley
 */

public interface TogglesOperations {

   /**
    * Gets the value of a toggle.
    *
    * @param name the name of the toggle to get.
    * @return the value of the toggle as a {@link String}.
    */

   String getDataBaseToggle(String name);

}

/* EOF */
