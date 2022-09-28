/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.artifact.annotation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * Provides access to annotations stored as the "Annotation" attribute in the specified artifact or provided through
 * ArtifactAnnotationProvider extension point.
 *
 * @author Donald G. Dunne
 */
public class ArtifactAnnotationManager {

   private static Collection<Attribute<String>> getAttributes(Artifact artifact) {
      return artifact.getAttributes(CoreAttributeTypes.Annotation);
   }

   public static final Set<ArtifactAnnotation> getAnnotations(Artifact artifact) {
      ensureLoaded();
      Set<ArtifactAnnotation> annotations = new HashSet<>();
      for (ArtifactAnnotationProvider annotation : extensionDefinedObjects.getObjects()) {
         annotation.getAnnotations(artifact, annotations);
      }
      return annotations;
   }

   /**
    * Add an annotation to be stored in the "Annotation" attribute of this given artifact.
    */
   public static void addAnnotation(Artifact artifact, ArtifactAnnotation newAnnotation) {
      // Update attribute if it already exists
      for (Attribute<String> attr : getAttributes(artifact)) {
         ArtifactAnnotation annotation = new ArtifactAnnotation(attr.getValue());
         if (newAnnotation.equals(annotation)) {
            attr.setValue(newAnnotation.toXml());
            return;
         }
      }
      artifact.addAttribute(CoreAttributeTypes.Annotation, newAnnotation.toXml());
   }

   /**
    * Remove the annotation from the "Annotation" attribute of the given artifact.
    */
   public static void removeAnnotation(Artifact artifact, ArtifactAnnotation annotation) {
      // Update attribute if it already exists
      for (Attribute<String> attr : getAttributes(artifact)) {
         ArtifactAnnotation attrAnnotation = new ArtifactAnnotation(attr.getValue());
         if (annotation.equals(attrAnnotation)) {
            attr.delete();
            return;
         }
      }
   }

   private static ExtensionDefinedObjects<ArtifactAnnotationProvider> extensionDefinedObjects;

   private static void ensureLoaded() {
      if (extensionDefinedObjects == null) {
         extensionDefinedObjects =
            new ExtensionDefinedObjects<>("org.eclipse.osee.framework.skynet.core.ArtifactAnnotationProvider",
               "ArtifactAnnotationProvider", "classname");
      }
   }

}