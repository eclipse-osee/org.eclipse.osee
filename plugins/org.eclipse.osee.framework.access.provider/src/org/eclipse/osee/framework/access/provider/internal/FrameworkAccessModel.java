/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.access.provider.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.access.AccessData;
import org.eclipse.osee.framework.core.access.AccessDetail;
import org.eclipse.osee.framework.core.access.Scope;
import org.eclipse.osee.framework.core.data.IAccessContextId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.dsl.integration.AccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslAccessModel;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author John Misinco
 */
public class FrameworkAccessModel extends OseeDslAccessModel {

   private final OrcsTokenService tokenService;

   public FrameworkAccessModel(AccessModelInterpreter interpreter, OseeDslProvider dslProvider, OrcsTokenService tokenService) {
      super(interpreter, dslProvider);
      this.tokenService = tokenService;
   }

   @Override
   public void computeAccess(IAccessContextId contextId, Collection<Object> objectsToCheck, AccessData accessData) {
      if (contextId.equals(DefaultFrameworkAccessConstants.INVALID_ASSOC_ART_ID)) {
         for (Object obj : objectsToCheck) {
            AccessDetail<Object> data = new AccessDetail<>(obj, PermissionEnum.READ, Scope.createLegacyScope(),
               "Invalid artifact Id associated with branch");
            accessData.add(obj, data);
            addRelationAccess(obj, accessData);
         }
      } else {
         super.computeAccess(contextId, objectsToCheck, accessData);
      }
   }

   private void addRelationAccess(Object object, AccessData accessData) {
      if (object instanceof Artifact) {
         for (RelationTypeToken relationType : tokenService.getValidRelationTypes(
            ((Artifact) object).getArtifactType())) {
            for (RelationSide relationSide : RelationSide.values()) {
               accessData.add(object, new AccessDetail<>(new RelationTypeSide(relationType, relationSide),
                  PermissionEnum.READ, Scope.createLegacyScope()));
            }
         }
      }
   }
}