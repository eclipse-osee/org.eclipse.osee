/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.search;

import java.util.List;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface ResultSet<T> {

   int getCount() throws OseeCoreException;

   T getOneOrNull(LoadLevel level) throws OseeCoreException;

   T getExactlyOne(LoadLevel level) throws OseeCoreException;

   List<T> getList(LoadLevel level) throws OseeCoreException;

   Iterable<T> getIterable(LoadLevel level, int fetchSize) throws OseeCoreException;
}
