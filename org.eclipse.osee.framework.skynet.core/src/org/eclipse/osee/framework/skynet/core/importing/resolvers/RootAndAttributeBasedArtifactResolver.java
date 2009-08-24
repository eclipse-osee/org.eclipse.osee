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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Robert A. Fisher
 */
public class RootAndAttributeBasedArtifactResolver extends NewArtifactImportResolver {
   private final Collection<AttributeType> noneChangingAttributes;
   private final boolean createNewIfNotExist;

   public RootAndAttributeBasedArtifactResolver(ArtifactType primaryArtifactType, ArtifactType secondaryArtifactType, Collection<AttributeType> noneChangingAttributes, boolean createNewIfNotExist) {
      super(primaryArtifactType, secondaryArtifactType);
      this.noneChangingAttributes = noneChangingAttributes;
      this.createNewIfNotExist = createNewIfNotExist;
   }

   private boolean attributeValuesMatch(RoughArtifact roughArtifact, Artifact artifact) throws OseeCoreException {
      Map<String, String> roughAttributeCollection = roughArtifact.getAttributes();
      HashCollection<String, String> roughAttributeMap = new HashCollection<String, String>();
      for (Entry<String, String> roughAttribute : roughAttributeCollection.entrySet()) {
         roughAttributeMap.put(roughAttribute.getKey(), roughAttribute.getValue());
      }

      for (AttributeType attributeType : noneChangingAttributes) {
         Collection<String> attributeValues = artifact.getAttributesToStringList(attributeType.getName());
         Collection<String> roughAttributes = roughAttributeMap.getValues(attributeType.getName());

         if (roughAttributes == null) {
            roughAttributes = Collections.emptyList();
         }

         if (attributeValues.size() == roughAttributes.size()) {
            for (String attributeValue : attributeValues) {
               boolean attributeEqual = false;
               Iterator<String> iter = roughAttributes.iterator();

               String normalizedAttributeValue = normalizeAttributeValue(attributeValue);
               while (iter.hasNext()) {
                  String otherAttribute = iter.next();

                  if (normalizedAttributeValue.equals(normalizeAttributeValue(otherAttribute))) {
                     // Make sure we don't count this attribute more than once for equality
                     iter.remove();
                     attributeEqual = true;
                     break;
                  }
               }

               if (!attributeEqual) {
                  return false;
               }
            }
         }
      }
      return true;
   }

   private String normalizeAttributeValue(String value) {
      return value.trim().replaceAll("\\.$", "").toLowerCase();
   }

   @Override
   public Artifact resolve(RoughArtifact roughArtifact, Branch branch) throws OseeCoreException {
      if (noneChangingAttributes == null || noneChangingAttributes.isEmpty()) {
         throw new OseeArgumentException("noneChangingAttributes cannot be null or empty");
      }
      Artifact realArtifact = null;
      RoughArtifact roughParent = roughArtifact.getRoughParent();

      if (roughParent != null) {
         Artifact real = null;
         //            toRealArtifact(roughParent);

         List<Artifact> siblings = real.getChildren();
         Collection<Artifact> candidates = new LinkedList<Artifact>();

         for (Artifact artifact : siblings) {
            if (attributeValuesMatch(roughArtifact, artifact)) {
               candidates.add(artifact);
            }
         }

         if (candidates.size() == 1) {
            realArtifact = candidates.iterator().next();
            translateAttributes(roughArtifact, realArtifact);
         } else {
            OseeLog.log(Activator.class, Level.INFO,
                  "Found " + candidates.size() + " candidates during reuse import for " + roughArtifact.getName());
            if (createNewIfNotExist) {
               realArtifact = super.resolve(roughArtifact, branch);
            }
         }
      }

      return realArtifact;
   }
}