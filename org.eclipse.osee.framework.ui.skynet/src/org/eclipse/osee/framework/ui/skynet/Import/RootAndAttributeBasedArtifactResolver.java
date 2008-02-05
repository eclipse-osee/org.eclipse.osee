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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.ui.skynet.Import.RoughArtifact.NameAndVal;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Robert A. Fisher
 */
public class RootAndAttributeBasedArtifactResolver extends NewArtifactImportResolver {
   private final LinkedList<DynamicAttributeDescriptor> identifyingAttributeDescriptors;
   private final Collection<String> EMPTY = new ArrayList<String>(0);
   private final boolean createNewIfNotExist;

   /**
    * @param identifyingAttributeDescriptors
    */
   public RootAndAttributeBasedArtifactResolver(Collection<DynamicAttributeDescriptor> identifyingAttributeDescriptors, boolean createNewIfNotExist) {
      if (identifyingAttributeDescriptors == null) throw new IllegalArgumentException(
            "identifyingAttributeDescriptors can not be null");
      if (identifyingAttributeDescriptors.isEmpty()) throw new IllegalArgumentException(
            "identifyingAttributeDescriptors can not be empty");

      this.identifyingAttributeDescriptors =
            new LinkedList<DynamicAttributeDescriptor>(identifyingAttributeDescriptors);
      this.createNewIfNotExist = createNewIfNotExist;
   }

   private boolean attributeValuesMatch(RoughArtifact roughArtifact, Artifact artifact) throws SQLException {

      Collection<NameAndVal> roughAttributeCollection = roughArtifact.getAttributes();
      HashCollection<String, String> roughAttributeMap = new HashCollection<String, String>();
      for (NameAndVal roughAttribute : roughAttributeCollection) {
         roughAttributeMap.put(roughAttribute.getName(), roughAttribute.getValue());
      }

      for (DynamicAttributeDescriptor descriptor : identifyingAttributeDescriptors) {
         Collection<Attribute> attributes = artifact.getAttributeManager(descriptor.getName()).getAttributes();
         Collection<String> roughAttributes = roughAttributeMap.getValues(descriptor.getName());

         if (roughAttributes == null) {
            roughAttributes = EMPTY;
         }

         if (attributes.size() == roughAttributes.size()) {
            for (Attribute attribute : attributes) {
               boolean attributeEqual = false;
               Iterator<String> iter = roughAttributes.iterator();

               String normalizedAttributeValue = normalizeAttributeValue(attribute.getStringData());
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

   public Artifact resolve(RoughArtifact roughArtifact) throws SQLException, FileNotFoundException {
      Set<Artifact> siblings = roughArtifact.getRoughParent().getAssociatedArtifact().getChildren();
      Collection<Artifact> candidates = new LinkedList<Artifact>();

      for (Artifact artifact : siblings) {
         if (attributeValuesMatch(roughArtifact, artifact)) {
            candidates.add(artifact);
         }
      }

      Artifact realArtifact = null;
      if (candidates.size() == 1) {
         realArtifact = candidates.iterator().next();
         roughArtifact.updateValues(realArtifact);
      } else {
         OSEELog.logInfo(getClass(),
               "Found " + candidates.size() + " candidates during reuse import for " + roughArtifact.getName(), false);
         if (createNewIfNotExist) {
            realArtifact = super.resolve(roughArtifact);
         }
      }

      return realArtifact;
   }
}