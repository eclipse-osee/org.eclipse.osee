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

package org.eclipse.osee.framework.core.util.toggles;

import java.util.function.Function;

/**
 * This interface defines the method for obtaining a Toggle value. This interface is an extension of the
 * {@link Function} interface. The {@link TogglesFactory} creates implementations that are specific to the OSEE Client
 * and the OSEE Server. This interface may be used in both the client and server code.
 *
 * @author Loren K. Ashley
 */

@FunctionalInterface
public interface Toggles extends Function<String, Boolean> {
}

/* EOF */
