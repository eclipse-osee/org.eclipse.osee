/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.rule.validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public final class RelationSetRule extends AbstractValidationRule {
   private final ArtifactTypeToken artifactType;
   private final Integer minimumRelations;
   private final RelationTypeSide relationEnum;
   private final Collection<ArtifactTypeToken> ignoreArtifactTypes;

   public RelationSetRule(ArtifactTypeToken artifactType, RelationTypeSide relationEnum, Integer minimumRelations, AtsApi atsApi, ArtifactTypeToken... ignoreArtifactTypes) {
      super(atsApi);
      this.artifactType = artifactType;
      this.relationEnum = relationEnum;
      this.minimumRelations = minimumRelations;
      this.ignoreArtifactTypes =
         ignoreArtifactTypes.length == 0 ? new ArrayList<>() : Arrays.asList(ignoreArtifactTypes);
   }

   public boolean hasArtifactType(ArtifactTypeToken artifactType) {
      return artifactType.inheritsFrom(artifactType);
   }

   @Override
   public void validate(ArtifactToken artifact, XResultData results) {
      ArtifactTypeToken type = atsApi.getStoreService().getArtifactType(artifact);

      if (!isIgnoreType(type) && hasArtifactType(type)) {
         Collection<ArtifactToken> arts = atsApi.getRelationResolver().getRelatedArtifacts(artifact, relationEnum);
         if (arts.size() < minimumRelations) {
            String errStr =
               "has less than minimum " + minimumRelations + " relation for type \"" + relationEnum.getName() + "\"";
            logError(artifact, errStr, results);
         }
      }
   }

   @Override
   public String getRuleDescription() {
      return "For \"" + artifactType + "\", ensure at least " + minimumRelations + " relations(s) of type \"" + relationEnum + "\" exists";
   }

   @Override
   public String getRuleTitle() {
      return "Relations Check:";
   }

   private Collection<ArtifactTypeToken> getIgnoreArtifactTypes() {
      return ignoreArtifactTypes;
   }

   private boolean isIgnoreType(ArtifactTypeToken type) {
      return getIgnoreArtifactTypes().contains(type);
   }
}