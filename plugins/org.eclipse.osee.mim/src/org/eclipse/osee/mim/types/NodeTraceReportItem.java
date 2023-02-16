/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.mim.types;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.RelationTypeSide;

public class NodeTraceReportItem {

   ArtifactId id;
   String name;
   String artifactType;
   List<NodeTraceReportItem> relatedItems;

   public NodeTraceReportItem(ArtifactReadable art) {
      this(art, RelationTypeSide.SENTINEL);
   }

   public NodeTraceReportItem(ArtifactReadable art, RelationTypeSide relType) {
      this.id = ArtifactId.valueOf(art.getId());
      this.name = art.getName();
      this.artifactType = art.getArtifactType().getName();
      if (relType.isValid()) {
         this.relatedItems =
            art.getRelated(relType).getList().stream().distinct().map(a -> new NodeTraceReportItem(a)).collect(
               Collectors.toList());
      } else {
         this.relatedItems = new LinkedList<>();
      }
   }

   public ArtifactId getId() {
      return id;
   }

   public void setId(ArtifactId id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(String artifactType) {
      this.artifactType = artifactType;
   }

   public List<NodeTraceReportItem> getRelatedItems() {
      return relatedItems;
   }

   public void setRelatedItems(List<NodeTraceReportItem> relatedItems) {
      this.relatedItems = relatedItems;
   }

}
