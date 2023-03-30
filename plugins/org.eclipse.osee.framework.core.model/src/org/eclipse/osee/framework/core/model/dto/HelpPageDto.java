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

package org.eclipse.osee.framework.core.model.dto;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

/**
 * @author Ryan T. Baldwin
 */
public class HelpPageDto {

   private ArtifactId id = ArtifactId.SENTINEL;
   private String name = "";
   private String markdownContent = "";
   private String appName = "";
   private boolean isHeader = false;
   private boolean isTraining = false;
   private List<HelpPageDto> children = new LinkedList<>();

   public HelpPageDto() {
   }

   public HelpPageDto(ArtifactReadable art) {
      if (art.isValid()) {
         setId(ArtifactId.valueOf(art.getId()));
         setName(art.getName());
         setMarkdownContent(art.getSoleAttributeAsString(CoreAttributeTypes.MarkdownContent, ""));
         setAppName(art.getSoleAttributeAsString(CoreAttributeTypes.ApplicationName, ""));
         setHeader(art.getSoleAttributeValue(CoreAttributeTypes.IsHelpPageHeader, false));
         setTraining(art.getSoleAttributeValue(CoreAttributeTypes.IsTrainingPage, false));

         if (isHeader()) {
            setChildren(art.getRelated(CoreRelationTypes.HelpToHelp_Child).getList().stream().map(
               a -> new HelpPageDto(a)).collect(Collectors.toList()));
         }
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

   public String getMarkdownContent() {
      return markdownContent;
   }

   public void setMarkdownContent(String markdownContent) {
      this.markdownContent = markdownContent;
   }

   public String getAppName() {
      return appName;
   }

   public void setAppName(String appName) {
      this.appName = appName;
   }

   public boolean isHeader() {
      return isHeader;
   }

   public void setHeader(boolean isHeader) {
      this.isHeader = isHeader;
   }

   public boolean isTraining() {
      return isTraining;
   }

   public void setTraining(boolean isTraining) {
      this.isTraining = isTraining;
   }

   public List<HelpPageDto> getChildren() {
      return children;
   }

   public void setChildren(List<HelpPageDto> children) {
      this.children = children;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof HelpPageDto) {
         return this.getId().equals(((HelpPageDto) obj).getId());
      }
      return false;
   }

}