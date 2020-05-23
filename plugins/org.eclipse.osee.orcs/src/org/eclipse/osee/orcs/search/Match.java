/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.search;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;

/**
 * @author Roberto E. Escobar
 */
public interface Match<T, K> {

   public boolean hasLocationData();

   public T getItem();

   public Collection<K> getElements();

   public List<MatchLocation> getLocation(K element);
}
