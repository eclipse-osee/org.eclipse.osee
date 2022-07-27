/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.PLGenericDBObject;

public interface QueryCapableMIMAPI<T extends PLGenericDBObject> {

   Collection<T> query(BranchId branch, MimAttributeQuery query);

   Collection<T> queryExact(BranchId branch, MimAttributeQuery query);

   Collection<T> query(BranchId branch, MimAttributeQuery query, boolean isExact);

}
