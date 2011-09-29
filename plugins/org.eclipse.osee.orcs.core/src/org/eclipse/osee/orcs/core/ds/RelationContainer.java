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
package org.eclipse.osee.orcs.core.ds;

import java.util.Collection;
import java.util.List;
import javax.management.relation.Relation;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

public interface RelationContainer<PARENT> {

   PARENT getContainer();

   void add(IRelationType type, Relation relation);

   int getCount(IRelationType type);

   Collection<IRelationType> getRelationTypes() throws OseeCoreException;

   List<Relation> getRelations(IRelationType relationType) throws OseeCoreException;

}
