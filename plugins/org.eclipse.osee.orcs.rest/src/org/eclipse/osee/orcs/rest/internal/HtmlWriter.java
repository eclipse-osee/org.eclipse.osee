/*
 * Created on Oct 17, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.orcs.rest.internal;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.QueryFactory;

public class HtmlWriter {

   private final UriInfo uriInfo;
   private final QueryFactory factory;

   public HtmlWriter(UriInfo uriInfo) {
      this(uriInfo, null);
   }

   public HtmlWriter(UriInfo uriInfo, QueryFactory factory) {
      this.uriInfo = uriInfo;
      this.factory = factory;
   }

   public String toHtml(Collection<? extends Object> objects) throws OseeCoreException {
      StringBuilder builder = new StringBuilder();
      builder.append("<html><body>");
      for (Object object : objects) {
         if (object instanceof Branch) {
            Branch branch = (Branch) object;
            addTable(builder, toData(branch));
         } else if (object instanceof ReadableArtifact) {
            ReadableArtifact artifact = (ReadableArtifact) object;
            addTable(builder, toData(artifact));
         } else {
            Map<String, Object> unhandled = new LinkedHashMap<String, Object>();
            unhandled.put("Class", object.getClass().getSimpleName());
            unhandled.put("Data", object.toString());
            addTable(builder, unhandled);
         }
         builder.append("<br/>");
      }
      builder.append("</body></html>");
      return builder.toString();
   }

   private Map<String, Object> toData(ReadableArtifact artifact) throws OseeCoreException {
      Map<String, Object> data = new LinkedHashMap<String, Object>();
      data.put("Name", artifact.getName());
      data.put("Uuid", artifact.getGuid());
      data.put("Local Id", artifact.getId());
      data.put("Tx Id", artifact.getTransactionId());
      IOseeBranch branch = artifact.getBranch();

      URI uri;
      if (isAtEndOfPath(uriInfo.getPath(), "artifact")) {
         uri = uriInfo.getAbsolutePathBuilder().path("../../{uuid}").build(branch.getGuid());
      } else {
         uri = uriInfo.getAbsolutePathBuilder().path("../../../{uuid}").build(branch.getGuid());
      }
      data.put("Branch", asLink(uri.toASCIIString(), branch.getName()));

      Collection<IAttributeType> types = artifact.getAttributeTypes();
      for (IAttributeType type : types) {
         for (ReadableAttribute<?> attr : artifact.getAttributes(type)) {
            String value = attr.getDisplayableString();
            data.put(type.getName(), value == null ? "<NULL>" : value);
         }
      }

      List<ReadableArtifact> arts =
         artifact.getRelatedArtifacts(CoreRelationTypes.Default_Hierarchical__Child, factory);
      int count = 0;
      for (ReadableArtifact art : arts) {
         URI uri1;
         if (isAtEndOfPath(uriInfo.getPath(), "artifact")) {
            uri1 = uriInfo.getAbsolutePathBuilder().path("{uuid}").build(art.getGuid());
         } else {
            uri1 = uriInfo.getAbsolutePathBuilder().path("../{uuid}").build(art.getGuid());
         }
         String value = art.getName();
         data.put("Child " + ++count, asLink(uri1.toASCIIString(), value == null ? "<NULL>" : value));
      }
      return data;
   }

   public Map<String, Object> toData(Branch branch) throws OseeCoreException {
      Map<String, Object> data = new LinkedHashMap<String, Object>();
      data.put("Name", branch.getName());
      data.put("Uuid", branch.getGuid());
      data.put("Local Id", branch.getId());
      data.put("State", branch.getBranchState());
      data.put("Type", branch.getBranchType());
      data.put("Archived", branch.getArchiveState());
      data.put("Short Name", branch.getShortName());
      data.put("Storage", branch.getStorageState());
      if (branch.hasParentBranch()) {
         try {
            IOseeBranch parent = branch.getParentBranch();
            URI uri;
            if (isAtEndOfPath(uriInfo.getPath(), "branch")) {
               uri = uriInfo.getAbsolutePathBuilder().path("{uuid}").build(parent.getGuid());
            } else {
               uri = uriInfo.getAbsolutePathBuilder().path("../{uuid}").build(parent.getGuid());
            }
            data.put("Parent", asLink(uri.toASCIIString(), parent.getName()));
         } catch (OseeCoreException ex) {
            data.put("Parent", "Root");
         }

         URI uri;
         if (isAtEndOfPath(uriInfo.getPath(), "branch")) {
            uri = uriInfo.getAbsolutePathBuilder().path("{uuid}/artifact").build(branch.getGuid());
         } else {
            uri = uriInfo.getAbsolutePathBuilder().path("../{uuid}/artifact").build(branch.getGuid());
         }
         data.put("Artifacts", asLink(uri.toASCIIString(), "Hierarchy"));
      }
      return data;
   }

   public String asLink(String url, String text) {
      return String.format("<a href=\"%s\">%s</a>", url, text);
   }

   public void addTable(StringBuilder builder, Map<String, Object> data) {
      builder.append("<table>");
      for (Entry<String, Object> entry : data.entrySet()) {
         builder.append("<tr><td><b>");
         builder.append(entry.getKey());
         builder.append(": <b/></td><td>");
         builder.append(entry.getValue().toString());
         builder.append("</td></tr>");
      }
      builder.append("</table>");
   }

   private boolean isAtEndOfPath(String path, String value) {
      String toCheck = path;
      if (toCheck.endsWith("/")) {
         toCheck = toCheck.substring(0, toCheck.length() - 1);
      }
      return toCheck.endsWith(value);
   }
}
