/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs;

import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.EnumTypes;
import org.eclipse.osee.orcs.data.RelationTypes;

/**
 * @author Roberto E. Escobar
 */
public interface OrcsTypes {

   /**
    * e2 stores
    */
   public static final String LOAD_OSEE_TYPE_DEF_URIS =
      "select uri, attr.attr_id from osee_tuple2 t2, osee_txs txs1, osee_attribute attr, osee_txs txs2 where tuple_type = ? and " //
         + "t2.gamma_id = txs1.gamma_id and txs1.branch_id = ? and txs1.tx_current = ? and e1 = ? and e2 = attr.attr_id and " //
         + "attr.gamma_id = txs2.gamma_id and txs2.branch_id = txs1.branch_id and txs2.tx_current = ?";
   public static final String LOAD_OSEE_TYPE_VERSIONS = "select distinct e1 from osee_tuple2 where tuple_type = ?";

   ArtifactTypes getArtifactTypes();

   AttributeTypes getAttributeTypes();

   RelationTypes getRelationTypes();

   EnumTypes getEnumTypes();

   void loadTypes(IResource resource);

   void loadTypes(String model);

   Callable<Void> writeTypes(OutputStream outputStream);

   Callable<Void> purgeArtifactsByArtifactType(Collection<? extends IArtifactType> artifactTypes);

   Callable<Void> purgeAttributesByAttributeType(Collection<? extends AttributeTypeId> attributeTypes);

   Callable<Void> purgeRelationsByRelationType(Collection<? extends IRelationType> relationTypes);

   void invalidateAll();

}
