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
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public final class RelationSetRule extends AbstractValidationRule {
   private final IArtifactType artifactType;
   private final Integer minimumRelations;
   private final IRelationTypeSide relationEnum;

   public RelationSetRule(IArtifactType artifactType, IRelationTypeSide relationEnum, Integer minimumRelations) {
      this.artifactType = artifactType;
      this.relationEnum = relationEnum;
      this.minimumRelations = minimumRelations;
   }

   public Integer getMinimumRelations() {
      return minimumRelations;
   }

   public boolean hasArtifactType(ArtifactType artType) {
      return artType.inheritsFrom(artifactType);
   }

   @Override
   public String toString() {
      return "For \"" + artifactType + "\", ensure at least " + minimumRelations + " relations(s) of type \"" + relationEnum + "\" exists";
   }

   public IRelationTypeSide getRelationEnum() {
      return relationEnum;
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) throws OseeCoreException {
      Collection<String> errorMessages = new ArrayList<String>();
      boolean validationPassed = true;
      if (hasArtifactType(artToValidate.getArtifactType())) {
         // validate that artifact has one "Requirement Trace" relation to a Subsystem Requirement
         Collection<Artifact> arts = artToValidate.getRelatedArtifacts(relationEnum);
         if (arts.size() < minimumRelations) {
            errorMessages.add(ValidateReqChangeReport.getRequirementHyperlink(artToValidate) + " (" + artToValidate.getGammaId() + ") has less than minimum " + minimumRelations + " relation for type \"" + relationEnum + "\"");
            validationPassed = false;
         }
      }
      return new ValidationResult(errorMessages, validationPassed);
   }
}