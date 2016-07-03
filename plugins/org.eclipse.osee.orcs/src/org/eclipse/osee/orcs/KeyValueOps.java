/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs;

/**
 * @author Angel Avila
 */
public interface KeyValueOps {

   Long putIfAbsent(String value);

   String getByKey(Long key);

   Long getByValue(String value);

   boolean putByKey(Long key, String value);

}