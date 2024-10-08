/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.ui.skynet.Import;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.AttributeBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.DoorsBestFitArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.DropTargetAttributeBasedResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.GuidBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IRoughArtifactTranslator;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.NewArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.RoughArtifactTranslatorImpl;

/**
 * @author Roberto E. Escobar
 */
public final class ArtifactResolverFactory {

   public static enum ArtifactCreationStrategy {
      CREATE_NEW_ALWAYS,
      CREATE_ON_NEW_ART_GUID,
      CREATE_ON_DIFFERENT_ATTRIBUTES,
      CREATE_ON_DOORS_BEST_FIT
   }

   private ArtifactResolverFactory() {
      // Static Factory
   }

   public static IArtifactImportResolver createAlwaysNewArtifacts(ArtifactTypeToken primaryArtifactType) {
      ArtifactTypeToken secondaryArtifactType = CoreArtifactTypes.HeadingMsWord;
      if (primaryArtifactType.equals(CoreArtifactTypes.SubsystemRequirementHtml) || primaryArtifactType.equals(
         CoreArtifactTypes.SystemRequirementHtml)) {
         secondaryArtifactType = CoreArtifactTypes.HeadingHtml;
      }
      return createAlwaysNewArtifacts(primaryArtifactType, secondaryArtifactType);
   }

   public static IArtifactImportResolver createAlwaysNewArtifacts(ArtifactTypeToken primaryArtifactType,
      ArtifactTypeToken secondaryArtifactType) {
      IRoughArtifactTranslator translator = new RoughArtifactTranslatorImpl();
      return new NewArtifactImportResolver(translator, primaryArtifactType, secondaryArtifactType,
         CoreArtifactTypes.DocumentDescriptionMsWord, CoreArtifactTypes.DesignDescriptionMsWord);
   }

   public static IArtifactImportResolver createResolver(ArtifactCreationStrategy strategy,
      ArtifactTypeToken primaryArtifactType, Collection<AttributeTypeToken> nonChangingAttributes,
      boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      IArtifactImportResolver toReturn;
      switch (strategy) {
         case CREATE_ON_DIFFERENT_ATTRIBUTES:
            toReturn = new AttributeBasedArtifactResolver(new RoughArtifactTranslatorImpl(), primaryArtifactType,
               CoreArtifactTypes.HeadingMsWord, nonChangingAttributes, createNewIfNotExist, deleteUnmatchedArtifacts);
            break;
         case CREATE_ON_NEW_ART_GUID:
            toReturn = new GuidBasedArtifactResolver(new RoughArtifactTranslatorImpl(), primaryArtifactType,
               CoreArtifactTypes.HeadingMsWord, createNewIfNotExist, deleteUnmatchedArtifacts);
            break;
         case CREATE_ON_DOORS_BEST_FIT:
            toReturn = new DoorsBestFitArtifactResolver(new RoughArtifactTranslatorImpl(), primaryArtifactType,
               CoreArtifactTypes.HeadingHtml, createNewIfNotExist, deleteUnmatchedArtifacts);
            break;
         case CREATE_NEW_ALWAYS:
         default:
            toReturn = createAlwaysNewArtifacts(primaryArtifactType);
            break;
      }
      return toReturn;
   }

   public static IArtifactImportResolver createResolver(ArtifactCreationStrategy strategy,
      ArtifactTypeToken primaryArtifactType, Collection<AttributeTypeToken> nonChangingAttributes,
      boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts, Artifact dropTarget) {

      return new DropTargetAttributeBasedResolver(new RoughArtifactTranslatorImpl(), primaryArtifactType,
         CoreArtifactTypes.HeadingMsWord, nonChangingAttributes, createNewIfNotExist, deleteUnmatchedArtifacts,
         dropTarget);

   }
}
