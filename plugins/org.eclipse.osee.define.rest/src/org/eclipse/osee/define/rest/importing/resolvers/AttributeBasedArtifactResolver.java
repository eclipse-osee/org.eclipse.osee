/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.importing.resolvers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.define.api.importing.RoughArtifact;
import org.eclipse.define.api.importing.RoughAttributeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Robert A. Fisher
 */
public class AttributeBasedArtifactResolver extends NewArtifactImportResolver {

   private final Collection<AttributeTypeToken> nonChangingAttributes;
   private final boolean createNewIfNotExist;

   public AttributeBasedArtifactResolver(TransactionBuilder transaction, IRoughArtifactTranslator translator, ArtifactTypeToken primaryArtifactType, ArtifactTypeToken secondaryArtifactType, Collection<AttributeTypeToken> nonChangingAttributes, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      super(transaction, translator, primaryArtifactType, secondaryArtifactType);
      this.nonChangingAttributes = nonChangingAttributes;
      this.createNewIfNotExist = createNewIfNotExist;
   }

   private boolean attributeValuesMatch(RoughArtifact roughArtifact, ArtifactReadable artifact) {
      RoughAttributeSet roughAttributeSet = roughArtifact.getAttributes();
      for (AttributeTypeToken attributeType : nonChangingAttributes) {
         Collection<String> attributeValues = artifact.getAttributeValues(attributeType);
         Collection<String> roughAttributes =
            roughAttributeSet.getAttributeValueList(attributeType, new ArrayList<String>());

         if (attributeValues.size() == roughAttributes.size()) {
            for (String attributeValue : attributeValues) {
               Iterator<String> iter = roughAttributes.iterator();

               String normalizedAttributeValue = normalizeAttributeValue(attributeValue);
               while (iter.hasNext()) {
                  String otherAttribute = iter.next();

                  if (normalizedAttributeValue.equals(normalizeAttributeValue(otherAttribute))) {
                     return true;
                  }
               }
            }
         }
      }
      return false;
   }

   private String normalizeAttributeValue(String value) {
      return value.trim().replaceAll("\\.$", "").toLowerCase();
   }

   @Override
   public ArtifactId resolve(RoughArtifact roughArtifact, BranchId branch, ArtifactId realParentId, ArtifactId rootId) {
      ArtifactReadable realArtifact = null;

      RoughArtifact roughParent = roughArtifact.getRoughParent();

      if (roughParent != null) {
         ArtifactReadable root =
            roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andId(rootId).getArtifact();
         List<ArtifactReadable> descendants = root.getDescendants();
         Collection<ArtifactReadable> candidates = new LinkedList<>();

         System.out.println(
            String.format("Resolved using: %s", !descendants.isEmpty() ? "root node." : "realParent descendants."));
         ArtifactReadable realParent =
            roughArtifact.getOrcsApi().getQueryFactory().fromBranch(branch).andId(realParentId).getArtifact();
         for (ArtifactReadable artifact : !descendants.isEmpty() ? descendants : realParent.getDescendants()) {
            if (attributeValuesMatch(roughArtifact, artifact)) {
               candidates.add(artifact);
            }
         }

         if (candidates.size() == 1) {
            realArtifact = candidates.iterator().next();
            getTranslator().translate(transaction, roughArtifact, realArtifact);
         } else {
            roughArtifact.getActivityLog().getDebugLogger().info("Found %s candidates during reuse import for \"%s\"",
               candidates.size(), roughArtifact.getName());
            if (createNewIfNotExist) {
               return super.resolve(roughArtifact, branch, null, root);
            }
         }
      }

      return realArtifact;
   }
}