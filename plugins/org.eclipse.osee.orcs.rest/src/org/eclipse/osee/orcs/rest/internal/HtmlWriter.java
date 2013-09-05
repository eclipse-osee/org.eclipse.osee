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
package org.eclipse.osee.orcs.rest.internal;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.GraphReadable;

/**
 * @author Roberto E. Escobar
 */
public class HtmlWriter {

   private final UriInfo uriInfo;

   public HtmlWriter(UriInfo uriInfo) {
      this.uriInfo = uriInfo;
   }

   public String toHtml(Iterable<? extends Object> objects) throws OseeCoreException {
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
         } else if (object instanceof TransactionRecord) {
            TransactionRecord tx = (TransactionRecord) object;
            addTable(builder, toData(tx));
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

   private Map<String, Object> toData(AttributeReadable<?> attribute) throws OseeCoreException {
      Map<String, Object> data = new LinkedHashMap<String, Object>();
      data.put("Name", attribute.getAttributeType().getName());

      int attrId = attribute.getLocalId();
      URI uri = uriInfo.getAbsolutePathBuilder().path("{attributeId}").build(attrId);
      data.put("AttributeId", asLink(uri.toASCIIString(), String.valueOf(attrId)));
      return data;
   }

   private Map<String, Object> toData(ArtifactReadable artifact) throws OseeCoreException {
      Map<String, Object> data = new LinkedHashMap<String, Object>();
      data.put("Name", artifact.getName());
      data.put("Uuid", artifact.getGuid());
      data.put("Tx Id", artifact.getTransaction());
      IOseeBranch branch = artifact.getBranch();

      URI uri;
      if (isAtEndOfPath(uriInfo.getPath(), "artifact")) {
         uri = uriInfo.getAbsolutePathBuilder().path("../../{uuid}").build(branch.getGuid());
      } else {
         uri = uriInfo.getAbsolutePathBuilder().path("../../../{uuid}").build(branch.getGuid());
      }
      data.put("Branch", asLink(uri.toASCIIString(), branch.getName()));

      Collection<? extends IAttributeType> types = artifact.getExistingAttributeTypes();
      for (IAttributeType type : types) {
         for (AttributeReadable<?> attr : artifact.getAttributes(type)) {
            URI attrUri = uriInfo.getAbsolutePathBuilder().path("/attribute/{attributeId}").build(attr.getLocalId());
            String label = asLink(attrUri.toASCIIString(), type.getName());
            String value = attr.getDisplayableString();
            data.put(label, value == null ? "<NULL>" : value);
         }
      }

      int count = 0;
      GraphReadable graph = OrcsApplication.getOrcsApi().getGraph(null);
      for (ArtifactReadable art : graph.getChildren(artifact)) {
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

   public Map<String, Object> toData(TransactionRecord txRecord) throws OseeCoreException {
      Map<String, Object> data = new LinkedHashMap<String, Object>();
      data.put("TxId", txRecord.getId());
      data.put("TxType", txRecord.getTxType());
      data.put("Date", txRecord.getTimeStamp());
      data.put("Comment", txRecord.getComment());
      data.put("Author", txRecord.getAuthor());
      IOseeBranch parent = txRecord.getBranch();
      URI uri;
      if (isAtEndOfPath(uriInfo.getPath(), "branch")) {
         uri = uriInfo.getAbsolutePathBuilder().path("{uuid}").build(parent.getGuid());
      } else {
         uri = uriInfo.getAbsolutePathBuilder().path("../{uuid}").build(parent.getGuid());
      }
      data.put("Branch", asLink(uri.toASCIIString(), parent.getName()));
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
