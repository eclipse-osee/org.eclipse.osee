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

package org.eclipse.osee.orcs.rest.internal;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Roberto E. Escobar
 */
public class HtmlWriter {

   private final UriInfo uriInfo;
   private final OrcsApi orcsApi;

   public HtmlWriter(UriInfo uriInfo, OrcsApi orcsApi) {
      this.uriInfo = uriInfo;
      this.orcsApi = orcsApi;
   }

   public String toHtml(Iterable<? extends Object> objects) {
      StringBuilder builder = new StringBuilder();
      builder.append("<html><body>");
      for (Object object : objects) {
         if (object instanceof Branch) {
            Branch branch = (Branch) object;
            addTable(builder, toData(branch));
         } else if (object instanceof ArtifactReadable) {
            ArtifactReadable artifact = (ArtifactReadable) object;
            addTable(builder, toData(artifact));
         } else if (object instanceof AttributeReadable) {
            AttributeReadable<?> attribute = (AttributeReadable<?>) object;
            addTable(builder, toData(attribute));
         } else if (object instanceof TransactionReadable) {
            TransactionReadable tx = (TransactionReadable) object;
            addTable(builder, toData(tx));
         } else {
            Map<String, Object> unhandled = new LinkedHashMap<>();
            unhandled.put("Class", object.getClass().getSimpleName());
            unhandled.put("Data", object.toString());
            addTable(builder, unhandled);
         }
         builder.append("<br/>");
      }
      builder.append("</body></html>");
      return builder.toString();
   }

   private Map<String, Object> toData(AttributeReadable<?> attribute) {
      Map<String, Object> data = new LinkedHashMap<>();
      data.put("Name", attribute.getAttributeType().getName());

      Long attrId = attribute.getId();
      URI uri = uriInfo.getAbsolutePathBuilder().path("{attributeId}").build(attrId);
      data.put("AttributeId", asLink(uri.getPath(), String.valueOf(attrId)));
      return data;
   }

   private Map<String, Object> toData(ArtifactReadable artifact) {
      Map<String, Object> data = new LinkedHashMap<>();
      data.put("Name", artifact.getName());
      data.put("Artifact Id", artifact.getId());
      data.put("Tx Id", artifact.getTransaction());
      String branchId = artifact.getBranch().getIdString();
      String branchName = getBranchFromId(artifact.getBranch()).getName();

      URI uri;
      if (isAtEndOfPath(uriInfo.getPath(), "artifact")) {
         uri = uriInfo.getAbsolutePathBuilder().path("../../{uuid}").build(branchId);
      } else {
         uri = uriInfo.getAbsolutePathBuilder().path("../../../{uuid}").build(branchId);
      }
      data.put("Branch Id", asLink(uri.toASCIIString(), branchId));
      data.put("Branch Name", asLink(uri.toASCIIString(), branchName));

      Collection<AttributeTypeToken> types = artifact.getExistingAttributeTypes();
      for (AttributeTypeToken type : types) {
         for (AttributeReadable<?> attr : artifact.getAttributes(type)) {
            URI attrUri = uriInfo.getAbsolutePathBuilder().path("/attribute/{attributeId}").build(attr.getId());
            String label = asLink(attrUri.getPath(), type.getName());
            String value = attr.getDisplayableString();
            data.put(label, value == null ? "<NULL>" : value);
         }
      }

      int count = 0;
      for (ArtifactReadable art : artifact.getChildren()) {
         URI uri1;
         if (isAtEndOfPath(uriInfo.getPath(), "artifact")) {
            uri1 = uriInfo.getAbsolutePathBuilder().path("{uuid}").build(art.getIdString());
         } else {
            uri1 = uriInfo.getAbsolutePathBuilder().path("../{uuid}").build(art.getIdString());
         }
         String value = art.getName();
         data.put("Child " + ++count, asLink(uri1.getPath(), value == null ? "<NULL>" : value));
      }
      return data;
   }

   private Map<String, Object> toData(Branch branch) {
      Map<String, Object> data = new LinkedHashMap<>();
      data.put("Name", branch.getName());
      data.put("Branch Id", branch.getId());
      data.put("State", branch.getBranchState());
      data.put("Type", branch.getBranchType());
      data.put("Archived", branch.isArchived());
      if (branch.hasParentBranch()) {
         try {
            BranchToken parent = getBranchFromId(branch.getParentBranch());

            URI uri;
            String parentId = parent.getIdString();
            if (isAtEndOfPath(uriInfo.getPath(), "branch")) {
               uri = uriInfo.getAbsolutePathBuilder().path("{uuid}").build(parentId);
            } else {
               uri = uriInfo.getAbsolutePathBuilder().path("../{uuid}").build(parentId);
            }
            data.put("Parent", asLink(uri.getPath(), parent.getName()));
         } catch (OseeCoreException ex) {
            data.put("Parent", "Root");
         }

         URI uri;
         if (isAtEndOfPath(uriInfo.getPath(), "branch")) {
            uri = uriInfo.getAbsolutePathBuilder().path("{uuid}/artifact").build(branch.getIdString());
         } else {
            uri = uriInfo.getAbsolutePathBuilder().path("../{uuid}/artifact").build(branch.getIdString());
         }
         data.put("Artifacts", asLink(uri.getPath(), "Hierarchy"));
      }
      return data;
   }

   private BranchToken getBranchFromId(BranchId branch) {
      return orcsApi.getQueryFactory().branchQuery().andId(branch).getResultsAsId().getExactlyOne();
   }

   public Map<String, Object> toData(TransactionReadable txRecord) {
      Map<String, Object> data = new LinkedHashMap<>();
      data.put("TxId", txRecord);
      data.put("TxType", txRecord.getTxType());
      data.put("Date", txRecord.getDate());
      data.put("Comment", txRecord.getComment());
      data.put("Author", txRecord.getAuthor());
      BranchToken branch = getBranchFromId(txRecord.getBranch());

      URI uri;
      if (isAtEndOfPath(uriInfo.getPath(), "branch")) {
         uri = uriInfo.getAbsolutePathBuilder().path("{uuid}").build(branch.getIdString());
      } else {
         uri = uriInfo.getAbsolutePathBuilder().path("../{uuid}").build(branch.getIdString());
      }
      data.put("Branch", asLink(uri.getPath(), branch.getName()));
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
