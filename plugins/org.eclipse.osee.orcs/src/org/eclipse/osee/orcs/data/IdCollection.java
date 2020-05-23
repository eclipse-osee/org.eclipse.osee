/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.data;

import java.util.Collection;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Roberto E. Escobar
 */
public interface IdCollection<T extends Id> {

   Collection<T> getAll();

   T get(Id id);

   T get(Long id);

   boolean exists(Id id);

   boolean isEmpty();

   int size();

}