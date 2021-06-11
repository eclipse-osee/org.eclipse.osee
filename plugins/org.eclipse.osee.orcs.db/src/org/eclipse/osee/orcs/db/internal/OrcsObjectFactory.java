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

package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.orcs.db.internal.loader.data.ArtifactObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.AttributeObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.BranchCategoryObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.RelationObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.TupleObjectFactory;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsObjectFactory extends ArtifactObjectFactory, AttributeObjectFactory, RelationObjectFactory, TupleObjectFactory, BranchCategoryObjectFactory {
   //
}
