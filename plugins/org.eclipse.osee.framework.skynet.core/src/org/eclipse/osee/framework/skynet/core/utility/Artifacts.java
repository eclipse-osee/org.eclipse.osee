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

package org.eclipse.osee.framework.skynet.core.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * Utility methods for common tasks performed on Artifact's.
 *
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public final class Artifacts {

   private Artifacts() {
      // This constructor is private because there is no reason to instantiate this class
   }

   public static Collection<Integer> toIds(Collection<? extends ArtifactId> artifacts) {
      Set<Integer> toReturn = new HashSet<>(artifacts.size());
      for (ArtifactId artifact : artifacts) {
         toReturn.add(artifact.getId().intValue());
      }
      return toReturn;
   }

   public static Collection<Long> toIdsL(Collection<? extends ArtifactId> artifacts) {
      Set<Long> toReturn = new HashSet<>(artifacts.size());
      for (ArtifactId artId : artifacts) {
         toReturn.add(artId.getId());
      }
      return toReturn;
   }

   public static List<String> toGuids(Collection<? extends ArtifactToken> artifacts) {
      List<String> guids = new ArrayList<>(artifacts.size());
      for (ArtifactToken artifact : artifacts) {
         guids.add(artifact.getGuid());
      }
      return guids;
   }

   /**
    * getName() all artifacts, else toString()
    */
   public static String commaArts(Collection<? extends Object> objects) {
      return toTextList(objects, ", ");
   }

   /**
    * getName() all artifacts, else toString()
    */
   public static String semmicolonArts(Collection<? extends Object> objects) {
      return toTextList(objects, "; ");
   }

   /**
    * getName() all artifacts, else toString()
    */
   public static String toString(String separator, Collection<? extends Object> objects) {
      return toTextList(objects, separator);
   }

   /**
    * getName() all artifacts, else toString()
    */
   public static String toTextList(Collection<? extends Object> objects, String separator) {
      StringBuilder sb = new StringBuilder();
      for (Object obj : objects) {
         if (obj instanceof Artifact) {
            sb.append(((Artifact) obj).getName());
         } else {
            sb.append(obj.toString());
         }
         sb.append(separator);
      }
      if (sb.length() > separator.length()) {
         return sb.substring(0, sb.length() - separator.length());
      }
      return "";
   }

   public static Collection<String> getNames(Collection<? extends Named> artifacts) {
      ArrayList<String> names = new ArrayList<>();
      for (Named namedArtifact : artifacts) {
         names.add(namedArtifact.getName());
      }
      return names;
   }

   public static void persistInTransaction(String comment, final Collection<? extends Artifact> artifacts) {
      persistInTransaction(comment, artifacts.toArray(new Artifact[artifacts.size()]));
   }

   public static void persistInTransaction(String comment, Artifact... artifacts) {
      SkynetTransaction transaction = TransactionManager.createTransaction(artifacts[0].getBranch(), comment);
      for (Artifact art : artifacts) {
         art.persist(transaction);
      }
      transaction.execute();
   }

   /**
    * Recurses default hierarchy and collections children of parentArtifact that are of type class
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> void getChildrenOfType(Artifact parentArtifact, Collection<A> children, Class<A> clazz, boolean recurse) {
      for (Artifact child : parentArtifact.getChildren()) {
         if (child.getClass().equals(clazz)) {
            children.add((A) child);
            if (recurse) {
               getChildrenOfType(child, children, clazz, recurse);
            }
         }
      }
   }

   /**
    * @return Set of type class that includes parentArtifact and children and will recurse children if true
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> Set<A> getChildrenAndThisOfTypeSet(Artifact parentArtifact, Class<A> clazz, boolean recurse) {
      Set<A> thisAndChildren = new HashSet<>();
      if (parentArtifact.getClass().equals(clazz)) {
         thisAndChildren.add((A) parentArtifact);
      }
      getChildrenOfTypeSet(parentArtifact, clazz, recurse);
      return thisAndChildren;
   }

   @SuppressWarnings("unchecked")
   public static <A extends Artifact> Set<A> getChildrenOfTypeSet(Artifact parentArtifact, Class<A> clazz, boolean recurse) {
      Set<A> children = new HashSet<>();
      for (Artifact child : parentArtifact.getChildren()) {
         if (child.getClass().equals(clazz)) {
            children.add((A) child);
            if (recurse) {
               getChildrenOfType(child, children, clazz, recurse);
            }
         }
      }
      return children;
   }

   public static Map<String, String> getDetailsKeyValues(Artifact artifact) {
      Map<String, String> details = new HashMap<>();
      if (artifact != null) {
         details.put("Artifact Id", artifact.getIdString());
         details.put("GUID", String.valueOf(Xml.escape(artifact.getGuid())));
         details.put("Artifact Token", String.format("[%s]-[%d]", artifact.getSafeName(), artifact.getId()));
         details.put("Branch", String.valueOf(Xml.escape(artifact.getBranchToken().getName())));
         details.put("Branch Uuid", artifact.getBranch().getIdString());
         details.put("Artifact Type Name", String.valueOf(Xml.escape(artifact.getArtifactTypeName())));
         details.put("Artifact Type Id", String.valueOf(artifact.getArtTypeId()));
         details.put("Gamma Id", String.valueOf(artifact.getGammaId()));
         details.put("Historical", String.valueOf(artifact.isHistorical()));
         details.put("Deleted", String.valueOf(artifact.isDeleted()));
         details.put("Revision",
            String.valueOf(artifact.isInDb() ? String.valueOf(artifact.getTransaction()) : "Not In Db"));
         details.put("Read Only", String.valueOf(artifact.isReadOnly()));
         details.put("Last Modified", artifact.isInDb() ? String.valueOf(artifact.getLastModified()) : "Not In Db");
         try {
            details.put("Last Modified By",
               artifact.isInDb() ? String.valueOf(artifact.getLastModifiedBy()) : "Not In Db");
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            details.put("Last Modified By", "Exception " + ex.getLocalizedMessage());
         }
      } else {
         details.put("Artifact", "null");
      }
      return details;
   }

   public static String getDetailsFormText(Map<String, String> keyValues, String fontName, int fontSize) {
      String template = "<b>%s:</b> %s<br/>";
      StringBuilder sb = new StringBuilder();
      sb.append("<body style='overflow:hidden'>");
      sb.append("<div style=\"font-size : ");
      sb.append(fontSize);
      sb.append("pt; font-family : ");
      sb.append(fontName);
      sb.append("\"> ");
      if (keyValues != null) {
         String[] keys = keyValues.keySet().toArray(new String[keyValues.keySet().size()]);
         Arrays.sort(keys);
         for (String key : keys) {
            try {
               sb.append(String.format(template, key, Xml.escape(keyValues.get(key))));
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      }
      sb.append("</div></body>");
      return sb.toString();
   }

   public static HashCollection<BranchId, Artifact> getBranchArtifactMap(Collection<Artifact> artifacts) {
      HashCollection<BranchId, Artifact> branchMap = new HashCollection<>();
      for (Artifact artifact : artifacts) {
         branchMap.put(artifact.getBranch(), artifact);
      }
      return branchMap;
   }

   public static Collection<Artifact> getOfType(ArtifactTypeId artifactType, Collection<? extends Artifact> artifacts) {
      List<Artifact> results = new ArrayList<>();
      for (Artifact art : artifacts) {
         if (art.isOfType(artifactType)) {
            results.add(art);
         }
      }
      return results;
   }

   public static boolean isOfType(Object object, ArtifactTypeId artifactType) {
      if (object instanceof Artifact) {
         return ((Artifact) object).isOfType(artifactType);
      }
      return false;
   }

   public static String getDirtyReport(Artifact artifact) {
      StringBuilder strB = new StringBuilder();
      for (Attribute<?> attribute : artifact.internalGetAttributes()) {
         if (attribute.isDirty()) {
            strB.append("Attribute: ");
            strB.append(attribute.getNameValueDescription());
            strB.append("\n");
         }
      }

      strB.append(RelationManager.reportHasDirtyLinks(artifact));
      return strB.toString();
   }

   public static Collection<Long> toUuids(Set<Artifact> artifacts) {
      List<Long> uuids = new ArrayList<>(artifacts.size());
      for (ArtifactId artifact : artifacts) {
         uuids.add(artifact.getId());
      }
      return uuids;
   }
}