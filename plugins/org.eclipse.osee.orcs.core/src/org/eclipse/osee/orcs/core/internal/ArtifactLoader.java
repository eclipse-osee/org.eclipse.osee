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
package org.eclipse.osee.orcs.core.internal;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Loader;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Andrew M. Finkbeiner
 */
public interface ArtifactLoader extends Loader {

   @Override
   ArtifactLoader includeDeleted();

   @Override
   ArtifactLoader includeDeleted(boolean enabled);

   @Override
   ArtifactLoader fromTransaction(int transactionId);

   @Override
   ArtifactLoader headTransaction();

   @Override
   ArtifactLoader setLoadLevel(LoadLevel loadLevel);

   @Override
   ArtifactLoader resetToDefaults();

   @Override
   ArtifactLoader loadAttributeType(IAttributeType... attributeType) throws OseeCoreException;

   @Override
   ArtifactLoader loadAttributeTypes(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException;

   @Override
   ArtifactLoader loadRelationType(IRelationType... relationType) throws OseeCoreException;

   @Override
   ArtifactLoader loadRelationTypes(Collection<? extends IRelationType> relationType) throws OseeCoreException;

   List<ArtifactReadable> load() throws OseeCoreException;

   List<ArtifactReadable> load(HasCancellation cancellation) throws OseeCoreException;

   ResultSet<ArtifactReadable> getResults() throws OseeCoreException;

   ResultSet<ArtifactReadable> getResults(HasCancellation cancellation) throws OseeCoreException;

}
