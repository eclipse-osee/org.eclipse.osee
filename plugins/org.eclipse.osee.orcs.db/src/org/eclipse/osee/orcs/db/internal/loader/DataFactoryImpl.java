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
package org.eclipse.osee.orcs.db.internal.loader;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataFactory;
import org.eclipse.osee.orcs.core.ds.RelationData;

/**
 * @author Roberto E. Escobar
 */
public class DataFactoryImpl implements DataFactory {

   @Override
   public ArtifactData createArtifactData(IArtifactType artifactType, String guid, String hrid) throws OseeCoreException {
      ArtifactData artifactData = new ArtifactData();
      return artifactData;
   }

   @Override
   public AttributeData createAttributeData(IAttributeType attributeType) throws OseeCoreException {
      AttributeData data = new AttributeData();
      return data;
   }

   @Override
   public RelationData createRelationData(IRelationType relationType) throws OseeCoreException {
      RelationData data = new RelationData();
      return data;
   }

}
