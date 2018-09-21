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
package org.eclipse.osee.framework.skynet.core.importing.resolvers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughAttributeSet;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Robert A. Fisher
 */
public class AttributeBasedArtifactResolver extends NewArtifactImportResolver {

   protected final Collection<AttributeTypeToken> nonChangingAttributes;
   protected final boolean createNewIfNotExist;

   public AttributeBasedArtifactResolver(IRoughArtifactTranslator translator, IArtifactType primaryArtifactType, IArtifactType secondaryArtifactType, Collection<AttributeTypeToken> nonChangingAttributes, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) {
      super(translator, primaryArtifactType, secondaryArtifactType);
      this.nonChangingAttributes = nonChangingAttributes;
      this.createNewIfNotExist = createNewIfNotExist;
   }

   protected boolean attributeValuesMatch(RoughArtifact roughArtifact, Artifact artifact) throws OseeCoreException {
      RoughAttributeSet roughAttributeSet = roughArtifact.getAttributes();

      for (AttributeTypeToken attributeType : nonChangingAttributes) {
         Collection<String> attributeValues = artifact.getAttributesToStringList(attributeType);
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

   protected String normalizeAttributeValue(String value) {
      return value.trim().replaceAll("\\.$", "").toLowerCase();
   }

   @Override
   public Artifact resolve(RoughArtifact roughArtifact, BranchId branch, Artifact realParent, Artifact root) throws OseeCoreException {
      Artifact realArtifact = null;
      RoughArtifact roughParent = roughArtifact.getRoughParent();

      if (roughParent != null) {
         List<Artifact> descendants = root.getDescendants();
         Collection<Artifact> candidates = new LinkedList<>();

         System.out.println(
            String.format("Resolved using: %s", !descendants.isEmpty() ? "root node." : "realParent descendants."));

         for (Artifact artifact : !descendants.isEmpty() ? descendants : realParent.getDescendants()) {
            if (attributeValuesMatch(roughArtifact, artifact)) {
               candidates.add(artifact);
            }
         }

         if (candidates.size() == 1) {
            realArtifact = candidates.iterator().next();
            getTranslator().translate(roughArtifact, realArtifact);
         } else {
            String output = String.format("Found %s candidates during reuse import for \"%s\"", candidates.size(),
               roughArtifact.getName());
            OseeLog.log(Activator.class, Level.INFO, output);
            if (createNewIfNotExist) {
               realArtifact = super.resolve(roughArtifact, branch, null, root);
            }
         }
      }

      return realArtifact;
   }
}