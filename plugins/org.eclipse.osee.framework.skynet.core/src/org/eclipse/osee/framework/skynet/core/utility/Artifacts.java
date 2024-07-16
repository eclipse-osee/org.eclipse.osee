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

package org.eclipse.osee.framework.skynet.core.utility;

import com.google.common.html.HtmlEscapers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;

/**
 * Utility methods for common tasks performed on Artifacts.
 *
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public final class Artifacts {

   private Artifacts() {
      // This constructor is private because there is no reason to instantiate this class
   }

   public static List<String> toGuids(Collection<? extends ArtifactToken> artifacts) {
      List<String> guids = new ArrayList<>(artifacts.size());
      for (ArtifactToken artifact : artifacts) {
         guids.add(artifact.getGuid());
      }
      return guids;
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
      var htmlEscaper = HtmlEscapers.htmlEscaper();
      Map<String, String> details = new HashMap<>();
      if (artifact != null) {
         details.put("Artifact Id", artifact.getIdString());
         details.put("GUID", String.valueOf(htmlEscaper.escape(artifact.getGuid())));
         details.put("Artifact Token", String.format("[%s]-[%d]", artifact.getSafeName(), artifact.getId()));
         details.put("Branch", String.valueOf(htmlEscaper.escape(artifact.getBranchToken().getName())));
         details.put("Branch Uuid", artifact.getBranch().getIdString());
         details.put("Artifact Type Name", String.valueOf(htmlEscaper.escape(artifact.getArtifactTypeName())));
         int x = 1;
         for (ArtifactTypeToken artType : artifact.getArtifactType().getSuperTypes()) {
            details.put("Artifact Type Super Type Name " + x++, String.valueOf(htmlEscaper.escape(artType.getName())));
         }
         details.put("Artifact Type Id", artifact.getArtifactType().getIdString());
         details.put("Gamma Id", String.valueOf(artifact.getGammaId()));
         details.put("Historical", String.valueOf(artifact.isHistorical()));
         details.put("Deleted", String.valueOf(artifact.isDeleted()));
         details.put("Revision",
            String.valueOf(artifact.isInDb() ? String.valueOf(artifact.getTransaction()) : "Not In Db"));
         details.put("Read Only", String.valueOf(artifact.isReadOnly()));
         details.put("Last Modified", artifact.isInDb() ? String.valueOf(artifact.getLastModified()) : "Not In Db");
         try {
            details.put("Last Modified By", artifact.isInDb() ? artifact.getLastModifiedBy().getName() : "Not In Db");
            TransactionRecord trans = (TransactionRecord) artifact.getTransaction();
            details.put("Last Modified Comment", artifact.isInDb() ? trans.getComment() : "Not In Db");
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
      var htmlEscaper = HtmlEscapers.htmlEscaper();
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
               sb.append(String.format(template, key, htmlEscaper.escape(keyValues.get(key))));
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

   public static boolean isOfType(Object object, ArtifactTypeToken artifactType) {
      if (object instanceof ArtifactToken) {
         return ((ArtifactToken) object).isOfType(artifactType);
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

   public static Object toStringWithIds(Collection<? extends ArtifactToken> artifacts) {
      StringBuilder sb = new StringBuilder();
      for (ArtifactToken art : artifacts) {
         sb.append(art.toStringWithId());
         sb.append(", ");
      }
      return sb.toString().replaceFirst(", $", "");
   }
}