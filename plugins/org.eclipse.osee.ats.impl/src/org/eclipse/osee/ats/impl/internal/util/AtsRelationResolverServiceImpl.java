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
package org.eclipse.osee.ats.impl.internal.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsRelationResolverServiceImpl implements IRelationResolver {

   private final IAtsServer atsServer;

   public AtsRelationResolverServiceImpl(IAtsServer atsServer) {
      this.atsServer = atsServer;
   }

   @Override
   public Collection<Object> getRelated(Object object, IRelationTypeSide relationType) {
      List<Object> results = new ArrayList<Object>();
      if (object instanceof ArtifactReadable) {
         for (ArtifactReadable art : ((ArtifactReadable) object).getRelated(relationType)) {
            results.add(art);
         }
      }
      return results;
   }

   @Override
   public boolean areRelated(Object object1, IRelationTypeSide relationType, Object object2) {
      boolean related = false;
      if ((object1 instanceof ArtifactReadable) && (object2 instanceof ArtifactReadable)) {
         related = ((ArtifactReadable) object1).areRelated(relationType, (ArtifactReadable) object2);
      }
      return related;
   }

   @Override
   public Object getRelatedOrNull(Object object, IRelationTypeSide relationType) {
      Object related = null;
      ArtifactReadable artifact = atsServer.getArtifact(object);
      if (artifact != null) {
         try {
            related = artifact.getRelated(relationType);
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
      }
      return related;
   }

}
