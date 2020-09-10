/*********************************************************************
 * Copyright (c) 2012 Boeing
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

import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeDataFactory<T> {

   AttributeData<T> create(ArtifactData parent, AttributeTypeGeneric attributeType);

   AttributeData<T> copy(BranchId destination, AttributeData<T> orcsData);

   AttributeData<T> clone(AttributeData<T> source);

   AttributeData<T> introduce(BranchId destination, AttributeData<T> source);

}
