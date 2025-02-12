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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class ArtifactAnnotation {

   private AnnotationType type;
   private String namespace;
   private String content;
   public static enum AnnotationType {
      None,
      Info,
      Warning,
      Error,
      Hidden;
      private static AnnotationType[] orderedTypes = new AnnotationType[] {Error, Warning, Info, Hidden, None};

      public static AnnotationType[] getOrderedTypes() {
         return orderedTypes;
      }
   };

   public ArtifactAnnotation(AnnotationType type, String namespace, String message) {
      this.type = type;
      this.content = message;
      this.namespace = namespace;
   }

   public ArtifactAnnotation(String xml) {
      fromXml(xml);
   }

   public static ArtifactAnnotation getError(String namespace, String message) {
      return new ArtifactAnnotation(AnnotationType.Error, namespace, message);
   }

   public static ArtifactAnnotation getInfo(String namespace, String message) {
      return new ArtifactAnnotation(AnnotationType.Info, namespace, message);
   }

   public static ArtifactAnnotation getHidden(String namespace, String message) {
      return new ArtifactAnnotation(AnnotationType.Hidden, namespace, message);
   }

   public static ArtifactAnnotation getWarning(String namespace, String message) {
      return new ArtifactAnnotation(AnnotationType.Warning, namespace, message);
   }

   private static String TYPE_TAG = "type";
   private static String NAMESPACE_TAG = "NAMESPACE";
   private static String CONTENT_TAG = "content";

   public String toXml() {
      return AXml.addTagData(TYPE_TAG, type.name()) + AXml.addTagData(NAMESPACE_TAG,
         namespace) + AXml.addTagData(CONTENT_TAG, content);
   }

   public void fromXml(String xml) {
      String typeStr = AXml.getTagData(xml, TYPE_TAG);
      if (!Strings.isValid(typeStr)) {
         type = AnnotationType.None;
      } else {
         type = AnnotationType.valueOf(typeStr);
      }
      namespace = AXml.getTagData(xml, NAMESPACE_TAG);
      content = AXml.getTagData(xml, CONTENT_TAG);
   }

   public AnnotationType getType() {
      return type;
   }

   public String getContent() {
      return content;
   }

   public String getNamespace() {
      return namespace;
   }
}
