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
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Andrew M. Finkbeiner
 */
public interface DataLoader extends Loader {

   @Override
   DataLoader includeDeleted();

   @Override
   DataLoader includeDeleted(boolean enabled);

   @Override
   DataLoader fromTransaction(int transactionId);

   @Override
   DataLoader headTransaction();

   @Override
   DataLoader setLoadLevel(LoadLevel loadLevel);

   @Override
   DataLoader resetToDefaults();

   @Override
   DataLoader loadAttributeType(IAttributeType... attributeType) throws OseeCoreException;

   @Override
   DataLoader loadAttributeTypes(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException;

   @Override
   DataLoader loadRelationType(IRelationType... relationType) throws OseeCoreException;

   @Override
   DataLoader loadRelationTypes(Collection<? extends IRelationType> relationType) throws OseeCoreException;

   DataLoader loadAttributeLocalId(int... attributeIds) throws OseeCoreException;

   DataLoader loadAttributeLocalIds(Collection<Integer> attributeIds) throws OseeCoreException;

   DataLoader loadRelationLocalId(int... relationIds) throws OseeCoreException;

   DataLoader loadRelationLocalIds(Collection<Integer> relationIds) throws OseeCoreException;

   void load(HasCancellation cancellation, LoadDataHandler handler) throws OseeCoreException;

}
