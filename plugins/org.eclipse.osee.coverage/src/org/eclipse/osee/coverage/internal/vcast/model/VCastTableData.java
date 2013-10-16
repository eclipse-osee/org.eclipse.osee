/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.internal.vcast.model;

import java.util.Collection;
import org.eclipse.osee.coverage.internal.vcast.VCastDataStore;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface VCastTableData<T> {

   String getName();

   String[] getColumns();

   Collection<T> getRows(VCastDataStore dataStore) throws OseeCoreException;

   Object[] toRow(T data);

}