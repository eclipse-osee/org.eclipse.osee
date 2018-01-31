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
package org.eclipse.osee.orcs.db.internal;

import org.eclipse.osee.orcs.db.internal.loader.data.ArtifactObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.AttributeObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.RelationObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.TransactionObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.data.TupleObjectFactory;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsObjectFactory extends TransactionObjectFactory, ArtifactObjectFactory, AttributeObjectFactory, RelationObjectFactory, TupleObjectFactory {
   //
}
