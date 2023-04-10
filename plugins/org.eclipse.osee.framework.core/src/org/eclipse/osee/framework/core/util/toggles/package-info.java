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

/**
 * This package contains a factory and class for obtaining the value of configuration toggles stored in the database.
 * The factory loads either an OSEE Server or an OSEE Client implementation for the {@link Toggles} interface. This
 * allows for an interface that can be used in the shared client and server code.
 */

package org.eclipse.osee.framework.core.util.toggles;

/* EOF */
