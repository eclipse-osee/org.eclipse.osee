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
package org.eclipse.osee.framework.access.provider.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.AccessDetail;
import org.eclipse.osee.framework.core.model.access.Scope;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author John Misinco
 */
public class FrameworkAccessModel extends OseeDslAccessModel {

   public FrameworkAccessModel(AccessModelInterpreter interpreter, OseeDslProvider dslProvider) {
      super(interpreter, dslProvider);
   }

   @Override
   public void computeAccess(IAccessContextId contextId, Collection<Object> objectsToCheck, AccessData accessData)  {
      if (contextId.equals(DefaultFrameworkAccessConstants.INVALID_ASSOC_ART_ID)) {
         for (Object obj : objectsToCheck) {
            AccessDetail<Object> data = new AccessDetail<Object>(obj, PermissionEnum.READ, Scope.createLegacyScope(),
               "Invalid artifact Id associated with branch");
            accessData.add(obj, data);
            addRelationAccess(obj, accessData);
         }
      } else {
         super.computeAccess(contextId, objectsToCheck, accessData);
      }
   }

   private void addRelationAccess(Object object, AccessData accessData)  {
      if (object instanceof Artifact) {
         for (RelationType relationType : ((Artifact) object).getValidRelationTypes()) {
            for (RelationSide relationSide : RelationSide.values()) {
               accessData.add(object, new AccessDetail<RelationTypeSide>(
                  new RelationTypeSide(relationType, relationSide), PermissionEnum.READ, Scope.createLegacyScope()));
            }
         }

      }
   }

}
