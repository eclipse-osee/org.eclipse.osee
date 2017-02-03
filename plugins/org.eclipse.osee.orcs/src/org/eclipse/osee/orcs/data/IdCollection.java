/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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