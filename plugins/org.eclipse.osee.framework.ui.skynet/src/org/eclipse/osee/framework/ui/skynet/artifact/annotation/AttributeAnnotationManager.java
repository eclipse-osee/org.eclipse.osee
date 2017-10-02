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
package org.eclipse.osee.framework.ui.skynet.artifact.annotation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;

/**
 * Provides access to annotations stored as the "Annotation" attribute in the specified artifact or provided through
 * IArtifactAnnotation extension point.
 * 
 * @author Donald G. Dunne
 */
public class AttributeAnnotationManager {
   private final Artifact artifact;
   private static Map<String, AttributeAnnotationManager> guidToManager =
      new HashMap<String, AttributeAnnotationManager>(50);

   public static AttributeAnnotationManager get(Artifact artifact) {
      if (!guidToManager.containsKey(artifact.getGuid())) {
         guidToManager.put(artifact.getGuid(), new AttributeAnnotationManager(artifact));
      }
      return guidToManager.get(artifact.getGuid());
   }

   private AttributeAnnotationManager(Artifact artifact) {
      this.artifact = artifact;
   }

   private Collection<Attribute<String>> getAttributes() {
      return artifact.getAttributes(CoreAttributeTypes.Annotation);
   }

   public static final Set<ArtifactAnnotation> getAnnotations(Artifact artifact) {
      if (!guidToManager.containsKey(artifact) && artifact.getAttributeCount(CoreAttributeTypes.Annotation) == 0) {
         return Collections.emptySet();
      }
      ensureLoaded();
      Set<ArtifactAnnotation> annotations = new HashSet<>();
      for (IArtifactAnnotation annotation : extensionDefinedObjects.getObjects()) {
         annotation.getAnnotations(artifact, annotations);
      }
      return annotations;
   }

   public static final boolean isAnnotationWarning(Artifact artifact) {
      for (ArtifactAnnotation notify : getAnnotations(artifact)) {
         if (notify.getType() == ArtifactAnnotation.Type.Warning || notify.getType() == ArtifactAnnotation.Type.Error) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return annotations stored in "Annotation" attribute of given artifact. NOTE: This is not a full list of
    * annotation for this artifact as annotations can be added via extension point.
    */
   public List<ArtifactAnnotation> getAnnotations() {
      List<ArtifactAnnotation> annotations = new ArrayList<>();
      for (String value : artifact.getAttributesToStringList(CoreAttributeTypes.Annotation)) {
         ArtifactAnnotation annotation = new ArtifactAnnotation(value);
         annotations.add(annotation);
      }
      return annotations;
   }

   /**
    * Add an annotation to be stored in the "Annotation" attribute of this given artifact.
    */
   public void addAnnotation(ArtifactAnnotation newAnnotation) {

      // Update attribute if it already exists
      for (Attribute<String> attr : getAttributes()) {
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
   public void removeAnnotation(ArtifactAnnotation annotation) {
      // Update attribute if it already exists
      for (Attribute<String> attr : getAttributes()) {
         ArtifactAnnotation attrAnnotation = new ArtifactAnnotation(attr.getValue());
         if (annotation.equals(attrAnnotation)) {
            attr.delete();
            return;
         }
      }
   }

   private static ExtensionDefinedObjects<IArtifactAnnotation> extensionDefinedObjects;

   private static void ensureLoaded() {
      if (extensionDefinedObjects == null) {
         extensionDefinedObjects = new ExtensionDefinedObjects<IArtifactAnnotation>(
            "org.eclipse.osee.framework.skynet.core.ArtifactAnnotation", "ArtifactAnnotation", "classname");
      }
   }

}