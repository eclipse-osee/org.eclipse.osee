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
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public final class RelationSetRule extends AbstractValidationRule {
   private final IArtifactType artifactType;
   private final Integer minimumRelations;
   private final RelationTypeSide relationEnum;
   private final Collection<IArtifactType> ignoreArtifactTypes;

   public RelationSetRule(IArtifactType artifactType, RelationTypeSide relationEnum, Integer minimumRelations, IArtifactType... ignoreArtifactTypes) {
      this.artifactType = artifactType;
      this.relationEnum = relationEnum;
      this.minimumRelations = minimumRelations;
      this.ignoreArtifactTypes =
         ignoreArtifactTypes.length == 0 ? new ArrayList<IArtifactType>() : Arrays.asList(ignoreArtifactTypes);
   }

   public boolean hasArtifactType(ArtifactType artType) {
      return artType.inheritsFrom(artifactType);
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor)  {
      Collection<String> errorMessages = new ArrayList<>();
      boolean validationPassed = true;
      ArtifactType type = artToValidate.getArtifactType();

      if (!isIgnoreType(type) && hasArtifactType(type)) {
         Collection<Artifact> arts = artToValidate.getRelatedArtifacts(relationEnum);
         if (arts.size() < minimumRelations) {
            errorMessages.add(ValidationReportOperation.getRequirementHyperlink(
               artToValidate) + " (" + artToValidate.getGammaId() + ") has less than minimum " + minimumRelations + " relation for type \"" + relationEnum.getName() + "\"");
            validationPassed = false;
         }
      }
      return new ValidationResult(errorMessages, validationPassed);
   }

   @Override
   public String getRuleDescription() {
      return "<b>Relations Check: </b>" + "For \"" + artifactType + "\", ensure at least " + minimumRelations + " relations(s) of type \"" + relationEnum + "\" exists";
   }

   @Override
   public String getRuleTitle() {
      return "Relations Check:";
   }

   private Collection<IArtifactType> getIgnoreArtifactTypes() {
      return ignoreArtifactTypes;
   }

   private boolean isIgnoreType(IArtifactType type) {
      return getIgnoreArtifactTypes().contains(type);
   }
}