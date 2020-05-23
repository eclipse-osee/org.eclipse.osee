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

package org.eclipse.osee.framework.jdk.core.type;

import java.util.Comparator;
import java.util.List;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface ResultSet<T> extends Iterable<T> {

   T getOneOrDefault(T defaultValue);

   T getAtMostOneOrDefault(T defaultValue);

   T getOneOrNull();

   T getExactlyOne();

   T getAtMostOneOrNull();

   int size();

   boolean isEmpty();

   ResultSet<T> sort(Comparator<T> comparator);

   List<T> getList();

}
