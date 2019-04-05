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
package org.eclipse.osee.define.rest.importing.resolvers;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author David W. Miller
 */
public final class ArtifactResolverFactory {

   public static enum ArtifactCreationStrategy {
      CREATE_NEW_ALWAYS,
      CREATE_ON_NEW_ART_GUID,
      CREATE_ON_DIFFERENT_ATTRIBUTES,
      CREATE_ON_DOORS_BEST_FIT,
      INSERT_OR_OVERLAY
   }

   private ArtifactResolverFactory() {
      // Static Factory
   }

   public static IArtifactImportResolver createAlwaysNewArtifacts(TransactionBuilder transaction, ArtifactTypeToken primaryArtifactType) {
      ArtifactTypeToken secondaryArtifactType = CoreArtifactTypes.HeadingMSWord;
      if (primaryArtifactType.equals(CoreArtifactTypes.SubsystemRequirementHTML) || primaryArtifactType.equals(
         CoreArtifactTypes.SystemRequirementHTML)) {
         secondaryArtifactType = CoreArtifactTypes.HeadingHTML;
      }
      return createAlwaysNewArtifacts(transaction, primaryArtifactType, secondaryArtifactType);
   }

   public static IArtifactImportResolver createAlwaysNewArtifacts(TransactionBuilder transaction, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType) {
      IRoughArtifactTranslator translator = new RoughArtifactTranslatorImpl();
      return new NewArtifactImportResolver(transaction, translator, primaryArtifactType, secondaryArtifactType,
         CoreArtifactTypes.DocumentDescriptionMSWord, CoreArtifactTypes.DesignDescriptionMSWord);
   }

   public static IArtifactImportResolver createResolver(TransactionBuilder transaction, ArtifactCreationStrategy strategy, ArtifactTypeToken primaryArtifactType, Collection<AttributeTypeToken> nonChangingAttributes, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      IArtifactImportResolver toReturn;
      switch (strategy) {
         case CREATE_ON_DIFFERENT_ATTRIBUTES:
            toReturn = new AttributeBasedArtifactResolver(transaction, new RoughArtifactTranslatorImpl(),
               primaryArtifactType, CoreArtifactTypes.HeadingMSWord, nonChangingAttributes, createNewIfNotExist,
               deleteUnmatchedArtifacts);
            break;
         case CREATE_ON_NEW_ART_GUID:
            toReturn = new GuidBasedArtifactResolver(transaction, new RoughArtifactTranslatorImpl(),
               primaryArtifactType, CoreArtifactTypes.HeadingMSWord, createNewIfNotExist, deleteUnmatchedArtifacts);
            break;
         case CREATE_ON_DOORS_BEST_FIT:
            toReturn = new DoorsBestFitArtifactResolver(transaction, new RoughArtifactTranslatorImpl(),
               primaryArtifactType, CoreArtifactTypes.HeadingHTML, createNewIfNotExist, deleteUnmatchedArtifacts);
            break;
         case INSERT_OR_OVERLAY:
            toReturn = new DoorsIdResolver(transaction, new RoughArtifactTranslatorImpl(), primaryArtifactType,
               CoreArtifactTypes.HeadingHTML, createNewIfNotExist, deleteUnmatchedArtifacts);
            break;
         case CREATE_NEW_ALWAYS:
         default:
            toReturn = createAlwaysNewArtifacts(transaction, primaryArtifactType);
            break;
      }
      return toReturn;
   }
}
