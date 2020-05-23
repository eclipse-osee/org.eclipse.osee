/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs;

import java.io.OutputStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.EnumTypes;

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

   AttributeTypes getAttributeTypes();

   EnumTypes getEnumTypes();

   void loadTypes(IResource resource);

   void loadTypes(String model);

   Callable<Void> writeTypes(OutputStream outputStream);

   Callable<Void> purgeArtifactsByArtifactType(Collection<? extends ArtifactTypeToken> artifactTypes);

   Callable<Void> purgeAttributesByAttributeType(Collection<? extends AttributeTypeId> attributeTypes);

   Callable<Void> purgeRelationsByRelationType(Collection<? extends RelationTypeToken> relationTypes);

   void invalidateAll();

}
