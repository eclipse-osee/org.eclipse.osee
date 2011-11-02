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
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Andrew M. Finkbeiner
 */
public interface DataLoader {

   void loadArtifacts(HasCancellation cancellation, ArtifactRowHandler handler, int branchId, Collection<Integer> ids, LoadOptions loadOptions, RelationRowHandlerFactory relationRowHandlerFactory, AttributeRowHandlerFactory attributeRowHandlerFactory) throws OseeCoreException;

   void loadArtifacts(HasCancellation cancellation, ArtifactRowHandler handler, QueryContext queryContext, LoadOptions loadOptions, RelationRowHandlerFactory relationRowHandlerFactory, AttributeRowHandlerFactory attributeRowHandlerFactory) throws OseeCoreException;

   int countArtifacts(HasCancellation cancellation, QueryContext queryContext) throws OseeCoreException;

}
