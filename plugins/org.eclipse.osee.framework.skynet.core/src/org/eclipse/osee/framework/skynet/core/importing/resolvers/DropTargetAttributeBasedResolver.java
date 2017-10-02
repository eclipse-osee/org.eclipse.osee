/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing.resolvers;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;

/**
 * @author Baily E. Roberts
 */
public class DropTargetAttributeBasedResolver extends AttributeBasedArtifactResolver {
   private final Artifact dropTarget;
   public static final Pattern oseeFilePattern = Pattern.compile("([^_]+)_[^\\d]+[^_]+_[^-]+.*");
   public static final Matcher oseeFileMatcher = oseeFilePattern.matcher("");

   public DropTargetAttributeBasedResolver(IRoughArtifactTranslator translator, IArtifactType primaryArtifactType, IArtifactType secondaryArtifactType, Collection<AttributeTypeToken> nonChangingAttributes, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts, Artifact dropTarget) {
      super(translator, primaryArtifactType, secondaryArtifactType, nonChangingAttributes, createNewIfNotExist,
         deleteUnmatchedArtifacts);
      this.dropTarget = dropTarget;
   }

   @Override
   public Artifact resolve(RoughArtifact roughArtifact, BranchId branch, Artifact realParent, Artifact root) {

      oseeFileMatcher.reset(roughArtifact.getName());
      if (oseeFileMatcher.find()) {
         roughArtifact.setName(oseeFileMatcher.group(1));
      }
      if (dropTarget != null && attributeValuesMatch(roughArtifact, dropTarget)) {
         getTranslator().translate(roughArtifact, dropTarget);
      }

      return dropTarget;
   }
}