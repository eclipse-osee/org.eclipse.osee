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
package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;

/**
 * @author Roberto E. Escobar
 */
public interface AttributeDataFactory<T> {

   AttributeData<T> create(ArtifactData parent, AttributeTypeId attributeType);

   AttributeData<T> copy(BranchId destination, AttributeData<T> orcsData);

   AttributeData<T> clone(AttributeData<T> source);

   AttributeData<T> introduce(BranchId destination, AttributeData<T> source);

}
