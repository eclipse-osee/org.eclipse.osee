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
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactAnnotation;
import org.eclipse.osee.framework.core.data.ArtifactAnnotation.AnnotationType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

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
      getDefaultAnnotations(artifact, annotations);
      for (ArtifactAnnotationProvider annotation : extensionDefinedObjects.getObjects()) {
         annotation.getAnnotations(artifact, annotations);
      }
      return annotations;
   }

   public static void getDefaultAnnotations(Artifact artifact, Set<ArtifactAnnotation> annotations) {
      try {
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.Annotation)) {
            for (String value : artifact.getAttributesToStringList(CoreAttributeTypes.Annotation)) {
               try {
                  // Format: ::<AnnotationType>::<annotation id>::<message>
                  if (value.startsWith("::")) {
                     String[] split = value.split("::");
                     ArtifactAnnotation annotation =
                        new ArtifactAnnotation(AnnotationType.valueOf(split[1]), split[2], split[3]);
                     annotations.add(annotation);
                  } else if (value.startsWith("<type>")) {
                     ArtifactAnnotation annotation = new ArtifactAnnotation(value);
                     annotations.add(annotation);
                  }
               } catch (Exception ex) {
                  // do nothing
               }
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
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