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
package org.eclipse.osee.framework.skynet.core.artifact.annotation;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;

/**
 * Provides access to annotations stored as the "Annotation" attribute in the specified artifact. NOTE: Annotations can
 * also be provided through IArtifactAnnotation extension point.
 * 
 * @author Donald G. Dunne
 */
public class AttributeAnnotationManager {

   public static final String ANNOTATION_ATTRIBUTE = "Annotation";
   private final org.eclipse.osee.framework.skynet.core.artifact.Artifact artifact;

   public AttributeAnnotationManager(Artifact artifact) {
      this.artifact = artifact;
   }

   private Collection<Attribute<String>> getAttributes() throws SQLException {
      return artifact.getAttributes(ANNOTATION_ATTRIBUTE);
   }

   /**
    * @return annotations stored in "Annotation" attribute of given artifact. NOTE: This is not a full list of
    *         annotation for this artifact as annotations can be added via extension point.
    * @throws SQLException
    */
   public List<ArtifactAnnotation> getAnnotations() throws SQLException {
      List<ArtifactAnnotation> annotations = new ArrayList<ArtifactAnnotation>();
      for (String value : artifact.getAttributesToStringList(ANNOTATION_ATTRIBUTE)) {
         ArtifactAnnotation annotation = new ArtifactAnnotation(value);
         annotations.add(annotation);
      }
      return annotations;
   }

   /**
    * Add an annotation to be stored in the "Annotation" attribute of this given artifact.
    * 
    * @param newAnnotation
    * @throws SQLException
    * @throws OseeCoreException
    */
   public void addAnnotation(ArtifactAnnotation newAnnotation) throws OseeCoreException, SQLException {

      // Update attribute if it already exists
      for (Attribute<String> attr : getAttributes()) {
         ArtifactAnnotation annotation = new ArtifactAnnotation(attr.getValue());
         if (newAnnotation.equals(annotation)) {
            attr.setValue(newAnnotation.toXml());
            return;
         }
      }
      artifact.addAttribute(ANNOTATION_ATTRIBUTE, newAnnotation.toXml());
   }

   /**
    * Remove the annotation from the "Annotation" attribute of the given artifact.
    * 
    * @param annotation
    * @throws SQLException
    * @throws OseeCoreException
    */
   public void removeAnnotation(ArtifactAnnotation annotation) throws SQLException, OseeCoreException {
      // Update attribute if it already exists
      for (Attribute<String> attr : getAttributes()) {
         ArtifactAnnotation attrAnnotation = new ArtifactAnnotation(attr.getValue());
         if (annotation.equals(attrAnnotation)) {
            attr.delete();
            return;
         }
      }
   }

}
