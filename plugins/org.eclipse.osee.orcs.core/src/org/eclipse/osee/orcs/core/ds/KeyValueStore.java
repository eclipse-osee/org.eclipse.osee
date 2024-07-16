/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.orcs.core.ds;

/**
 * @author Angel Avila
 */
public interface KeyValueStore {

   Long putIfAbsent(String value);

   boolean putWithKeyIfAbsent(Long key, String value);

   Long getByValue(String value);

   String getByKey(Long key);

   boolean putByKey(Long key, String value);

   boolean updateByKey(Long key, String value);
}