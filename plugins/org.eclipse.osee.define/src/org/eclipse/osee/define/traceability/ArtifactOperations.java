/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import org.eclipse.osee.define.internal.Activator;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactOperations {
   private final Artifact artifact;
   private List<String> partitions;
   private String qualificationMethod;
   private String qualificationFacility;

   enum QualificationFacility {
      UNKNOWN,
      STE,
      AIL,
      SEE;
   }

   enum QualificationMethod {
      Test("Test", "T", QualificationFacility.STE),
      Demo("Demonstration", "D", QualificationFacility.AIL),
      Inspection("Inspection", "I", QualificationFacility.SEE),
      SpecialQualification("Special Qualification", "S", QualificationFacility.AIL),
      Analysis("Analysis", "A", QualificationFacility.SEE),
      Unspecified("Unspecified", "U", QualificationFacility.UNKNOWN);

      String attributeString;
      String shortName;
      QualificationFacility facility;

      QualificationMethod(String attributeString, String shortName, QualificationFacility facility) {
         this.attributeString = attributeString;
         this.shortName = shortName;
         this.facility = facility;
      }

      public String getAttributeString() {
         return attributeString;
      }

      public String getShortName() {
         return shortName;
      }

      public QualificationFacility getFacility() {
         return facility;
      }

      public static QualificationMethod getMethodFromAttribute(String attribute) {
         QualificationMethod toReturn = QualificationMethod.Unspecified;
         for (QualificationMethod method : QualificationMethod.values()) {
            if (method.getAttributeString().equalsIgnoreCase(attribute)) {
               toReturn = method;
               break;
            }
         }
         return toReturn;
      }
   }

   public ArtifactOperations(Artifact artifact) {
      this.artifact = artifact;
   }

   public String getName() {
      return artifact.getName();
   }

   public String getParagraphNumber()  {
      return artifact.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, "");
   }

   public List<String> getPartitions() {
      if (partitions == null) {
         partitions = new ArrayList<>();
         try {
            partitions.addAll(artifact.getAttributesToStringList(CoreAttributeTypes.Partition));
            if (partitions.isEmpty()) {
               partitions.add("unspecified");
            }
            Collections.sort(partitions);
         } catch (OseeCoreException ex) {
            OseeLog.logf(Activator.class, Level.WARNING, ex, "Error obtaining partition info for [%s]", getName());
            partitions.add("Error");
         }
      }
      return partitions;
   }

   public String getQualificationMethod() {
      if (qualificationMethod == null) {
         processQualificationMethod();
      }
      return qualificationMethod;
   }

   public String getQualificationFacility() {
      if (qualificationFacility == null) {
         processQualificationMethod();
      }
      return qualificationFacility;
   }

   private void processQualificationMethod() {
      StringBuilder qualMethodBuilder = new StringBuilder();
      StringBuilder qualFacilityBuilder = new StringBuilder();
      try {
         List<String> methods =
            new ArrayList<String>(artifact.getAttributesToStringList(CoreAttributeTypes.QualificationMethod));
         Collections.sort(methods);
         for (int index = 0; index < methods.size(); index++) {
            String qualMethod = methods.get(index);
            QualificationMethod method = QualificationMethod.getMethodFromAttribute(qualMethod);
            qualMethodBuilder.append(method.getShortName());
            qualFacilityBuilder.append(method.getFacility());
            if (index + 1 < methods.size()) {
               qualMethodBuilder.append(", ");
               qualFacilityBuilder.append(", ");
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.logf(Activator.class, Level.WARNING, ex, "Error obtaining qualification info [%s]", getName());
      }
      qualificationMethod = qualMethodBuilder.toString();
      qualificationFacility = qualFacilityBuilder.toString();
   }

   public static HashCollection<String, Artifact> sortByPartition(Collection<Artifact> source) {
      HashCollection<String, Artifact> partitionMap = new HashCollection<>(false, TreeSet.class);
      for (Artifact artifact : source) {
         ArtifactOperations operator = new ArtifactOperations(artifact);
         List<String> partitions = operator.getPartitions();
         for (String key : partitions) {
            partitionMap.put(key, artifact);
         }
      }
      return partitionMap;
   }

   public static List<Artifact> sortByParagraphNumbers(Collection<Artifact> source) {
      List<Artifact> toReturn = new ArrayList<>(source);
      Collections.sort(toReturn, new Comparator<Artifact>() {
         @Override
         public int compare(Artifact art1, Artifact art2) {
            try {
               int toReturn = 0;
               String paragraph1 = new ArtifactOperations(art1).getParagraphNumber();
               String paragraph2 = new ArtifactOperations(art2).getParagraphNumber();
               Integer[] set1 = getParagraphIndices(paragraph1);
               Integer[] set2 = getParagraphIndices(paragraph2);
               int length1 = set1.length;
               int length2 = set2.length;

               int size = length1 < length2 ? length1 : length2;
               if (size == 0 && length1 != length2) {
                  toReturn = length1 < length2 ? -1 : 1;
               } else {
                  for (int index = 0; index < size; index++) {
                     toReturn = set1[index].compareTo(set2[index]);
                     if (toReturn != 0) {
                        break;
                     }
                  }
               }
               return toReturn;
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
            return 1;
         }

      });
      return toReturn;
   }

   private static Integer[] getParagraphIndices(String paragraph) {
      List<Integer> paragraphs = new ArrayList<>();
      if (Strings.isValid(paragraph)) {
         String[] values = paragraph.split("\\.");
         for (int index = 0; index < values.length; index++) {
            try {
               paragraphs.add(new Integer(values[index]));
            } catch (Exception ex) {
               // Do nothing;
            }
         }
      }
      return paragraphs.toArray(new Integer[paragraphs.size()]);
   }

   public String getPriority() {
      String toReturn = null;
      try {
         toReturn = this.artifact.getSoleAttributeValue(CoreAttributeTypes.Category, "");
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         toReturn = "";
      }
      return toReturn;
   }
}
