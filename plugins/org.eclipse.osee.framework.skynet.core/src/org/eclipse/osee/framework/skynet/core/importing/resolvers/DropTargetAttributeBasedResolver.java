/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.framework.skynet.core.importing.resolvers;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;

/**
 * @author Baily E. Roberts
 */
public class DropTargetAttributeBasedResolver extends AttributeBasedArtifactResolver {
   private final Artifact dropTarget;
   public static final Pattern oseeFilePattern = Pattern.compile("([^_]+)_[^\\d]+[^_]+_[^-]+.*");
   public static final Matcher oseeFileMatcher = oseeFilePattern.matcher("");

   public DropTargetAttributeBasedResolver(IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType, Collection<AttributeTypeToken> nonChangingAttributes, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts, Artifact dropTarget) {
      super(translator, primaryArtifactType, secondaryArtifactType, nonChangingAttributes, createNewIfNotExist,
         deleteUnmatchedArtifacts);
      this.dropTarget = dropTarget;
   }

   @Override
   public Artifact resolve(RoughArtifact roughArtifact, BranchToken branch, Artifact realParent, Artifact root) {

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