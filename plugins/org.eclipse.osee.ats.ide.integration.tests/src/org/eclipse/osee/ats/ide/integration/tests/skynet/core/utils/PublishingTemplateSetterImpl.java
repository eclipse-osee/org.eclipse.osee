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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.publishing.table.RelationTableOptions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.LoadType;
import org.eclipse.osee.orcs.core.util.PublishingTemplateSetter;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;

/**
 * An implementation of {@link PublishingTemplateSetter} for use by JUnit tests.
 *
 * @author Loren K. Ashlye
 */

public class PublishingTemplateSetterImpl implements PublishingTemplateSetter {

   /**
    * Saves the {@RelationEndpoint} used to find the hierarchical children of an {@link Artifact}.
    */

   private final RelationEndpoint relationEndpoint;

   /**
    * Creates a {@PublishingTemplateSetter} implementation for JUnit tests and save the {@link RelationEndpoint} used to
    * find hierarchical children.
    *
    * @param relationEndpoint an {@link RelationEndpoint} implementation.
    */

   public PublishingTemplateSetterImpl(RelationEndpoint relationEndpoint) {
      this.relationEndpoint = relationEndpoint;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String set(ArtifactToken parent, String name, String content, String rendererOptions,
      List<Map.Entry<String, String>> publishingTemplateContentMapEntries, List<String> matchCriteria,
      RelationTableOptions relationTableOptions) {
      //@formatter:off
      var templateArtifactToken =
         TestUtil
            .getOrCreateChildArtifactTokenByName
               (
                  this.relationEndpoint,
                  CoreBranches.COMMON,
                  CoreArtifactTokens.DocumentTemplates,
                  ArtifactId.SENTINEL,
                  CoreArtifactTypes.RendererTemplateWholeWord,
                  name
               );

      var templateArtifact =
         ArtifactLoader.loadArtifacts
            (
               List.of( templateArtifactToken ),
               CoreBranches.COMMON,
               LoadLevel.ALL,
               LoadType.RELOAD_CACHE,
               DeletionFlag.EXCLUDE_DELETED
            ).get(0);

      if( Objects.nonNull(rendererOptions)) {
         var valueList = new ArrayList<Object>();
         valueList.add(rendererOptions);
         TestUtil.setAttributeValues
            (
               templateArtifact,
               CoreAttributeTypes.RendererOptions,
               valueList,
               AttributeSetters.stringAttributeSetter
            );
      }

      if(Objects.nonNull(content)) {
         var valueList = new ArrayList<Object>();
         valueList.add( content );
         TestUtil.setAttributeValues
            (
               templateArtifact,
               CoreAttributeTypes.WholeWordContent,
               valueList,
               AttributeSetters.stringAttributeSetter
            );
      }

      if(Objects.nonNull(publishingTemplateContentMapEntries) && !publishingTemplateContentMapEntries.isEmpty()  ) {
         @SuppressWarnings("unchecked")
         var valueList = (List<Object>) (Object) publishingTemplateContentMapEntries;
         TestUtil.setAttributeValues
            (
               templateArtifact,
               CoreAttributeTypes.PublishingTemplateContentByFormatMapEntry,
               valueList,
               AttributeSetters.mapEntryAttributeSetter
            );
      }

      if(Objects.nonNull(matchCriteria) && !matchCriteria.isEmpty()) {
         @SuppressWarnings("unchecked")
         var valueList = (List<Object>) (Object) matchCriteria;
         TestUtil.setAttributeValues
            (
               templateArtifact,
               CoreAttributeTypes.TemplateMatchCriteria,
               valueList,
               AttributeSetters.stringAttributeSetter
            );
      }
      
      if (Objects.nonNull(relationTableOptions)) {
         @SuppressWarnings("unchecked")
         var artTypes = (List<Object>) (Object) relationTableOptions.getRelationTableArtifactTypeNamesAndOrIds();

         @SuppressWarnings("unchecked")
         var cols = (List<Object>) (Object) relationTableOptions.getRelationTableColumns();

         @SuppressWarnings("unchecked")
         var relTypeSides = (List<Object>) (Object) relationTableOptions.getRelationTableRelationTypeSides();
         
         if (Objects.nonNull(artTypes) && !artTypes.isEmpty()) {
            TestUtil.setAttributeValues
            (
               templateArtifact,
               CoreAttributeTypes.PublishingRelationTableArtifactTypeNameOrId,
               artTypes,
               AttributeSetters.stringAttributeSetter
            );
         }
         if (Objects.nonNull(cols) && !cols.isEmpty()) {
            TestUtil.setAttributeValues
            (
               templateArtifact,
               CoreAttributeTypes.PublishingRelationTableColumn,
               cols,
               AttributeSetters.stringAttributeSetter
            );
         }
         if (Objects.nonNull(relTypeSides) && !relTypeSides.isEmpty()) {
            TestUtil.setAttributeValues
            (
               templateArtifact,
               CoreAttributeTypes.PublishingRelationTableRelationTypeSide,
               relTypeSides,
               AttributeSetters.stringAttributeSetter
            );
         }
      }

      if (templateArtifact.isDirty()) {
         templateArtifact.persist("Three Blind Mice");
      }

      return "AT-".concat( templateArtifactToken.getIdString() );

   }

}

/* EOF */
