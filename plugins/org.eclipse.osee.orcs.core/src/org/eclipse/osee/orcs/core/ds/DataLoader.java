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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.LoadLevel;

/**
 * @author Andrew M. Finkbeiner
 */
public interface DataLoader extends Loader {

   ////////// OPTIONS
   @Override
   DataLoader setOptions(Options sourceOptions);

   DataLoader fromTransaction(TransactionId transactionId);

   DataLoader fromBranchView(ArtifactId viewId);

   DataLoader fromHeadTransaction();

   DataLoader withLoadLevel(LoadLevel loadLevel);

   LoadLevel getLoadLevel();

   boolean isHeadTransaction();

   //////////////// Deletion Options
   DataLoader includeDeletedArtifacts();

   DataLoader includeDeletedAttributes();

   DataLoader includeDeletedRelations();

   DataLoader includeDeletedArtifacts(boolean enabled);

   DataLoader includeDeletedAttributes(boolean enabled);

   DataLoader includeDeletedRelations(boolean enabled);

   boolean areDeletedArtifactsIncluded();

   boolean areDeletedAttributesIncluded();

   boolean areDeletedRelationsIncluded();

   //////////// IDS

   DataLoader withAttributeIds(int... attributeIds) ;

   DataLoader withAttributeIds(Collection<Integer> attributeIds) ;

   DataLoader withRelationIds(int... relationIds) ;

   DataLoader withRelationIds(Collection<Integer> relationIds) ;

   ///////// TYPES

   DataLoader withAttributeTypes(AttributeTypeId... attributeType) ;

   DataLoader withAttributeTypes(Collection<? extends AttributeTypeId> attributeTypes) ;

   DataLoader withRelationTypes(IRelationType... relationType) ;

   DataLoader withRelationTypes(Collection<? extends IRelationType> relationType) ;

}
