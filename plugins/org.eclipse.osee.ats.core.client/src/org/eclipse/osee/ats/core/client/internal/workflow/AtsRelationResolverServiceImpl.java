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
package org.eclipse.osee.ats.core.client.internal.workflow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class AtsRelationResolverServiceImpl implements IRelationResolver {

   @Override
   public Collection<Object> getRelated(Object object, IRelationTypeSide relationType) {
      List<Object> results = new ArrayList<Object>();
      Artifact useArt = getArtifact(object);
      if (useArt != null) {
         for (Artifact art : useArt.getRelatedArtifacts(relationType)) {
            results.add(art);
         }
      }
      return results;
   }

   private Artifact getArtifact(Object object) {
      Artifact useArt = null;
      if (object instanceof Artifact) {
         useArt = (Artifact) object;
      } else if (object instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) object;
         if (atsObject.getStoreObject() instanceof Artifact) {
            useArt = (Artifact) atsObject.getStoreObject();
         }
      }
      return useArt;
   }

   @Override
   public boolean areRelated(Object object1, IRelationTypeSide relationType, Object object2) {
      boolean related = false;
      Artifact useArt1 = getArtifact(object1);
      Artifact useArt2 = getArtifact(object2);
      if (useArt1 != null && useArt2 != null) {
         related = useArt1.isRelated(relationType, useArt2);
      }
      return related;
   }

}
