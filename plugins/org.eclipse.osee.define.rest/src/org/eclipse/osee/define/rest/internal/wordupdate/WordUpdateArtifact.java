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
package org.eclipse.osee.define.rest.internal.wordupdate;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordOleData;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import com.google.common.collect.Lists;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.define.api.WordArtifactChange;
import org.eclipse.osee.define.api.WordUpdateChange;
import org.eclipse.osee.define.api.WordUpdateData;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public class WordUpdateArtifact {
   private final OrcsApi orcsApi;
   private final QueryFactory queryFactory;
   private final Log logger;
   private final EventAdmin eventAdmin;

   public WordUpdateArtifact(Log logger, OrcsApi orcsApi, EventAdmin eventAdmin) {
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.logger = logger;
      this.eventAdmin = eventAdmin;
   }

   public WordUpdateChange updateArtifacts(WordUpdateData data) {
      Collection<WordExtractorData> extractorDatas;
      Element oleDataElement;
      try {
         IElementExtractor elementExtractor;
         Document document = extractJaxpDocument(data);
         if (data.isThreeWayMerge()) {
            String guid = orcsApi.getQueryFactory().fromBranch(data.getBranch()).andUuid(
               data.getArtifacts().iterator().next()).getResults().getExactlyOne().getGuid();
            elementExtractor = new MergeEditArtifactElementExtractor(guid, document);
         } else {
            elementExtractor = new WordImageArtifactElementExtractor(document);
         }
         extractorDatas = elementExtractor.extractElements();
         oleDataElement = elementExtractor.getOleDataElement();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
      return wordArtifactUpdate(data, extractorDatas, oleDataElement);
   }

   public ArtifactReadable getArtifact(BranchId branch, String guid) {
      return queryFactory.fromBranch(branch).andGuid(guid).getResults().getExactlyOne();
   }

   private Document extractJaxpDocument(WordUpdateData data) throws ParserConfigurationException, SAXException, IOException {
      Document document;
      InputStream stream = new ByteArrayInputStream(data.getWordData());
      try {
         document = Jaxp.nonDeferredReadXmlDocument(stream, "UTF-8");
      } finally {
         stream.close();
      }
      return document;
   }

   private WordUpdateChange wordArtifactUpdate(WordUpdateData data, Collection<WordExtractorData> extractorDatas, Element oleDataElement) {
      List<String> deletedArtifacts = Lists.newArrayList();
      WordUpdateChange updateChange = new WordUpdateChange();
      try {
         TransactionFactory txFactory = orcsApi.getTransactionFactory();
         boolean singleArtifact = extractorDatas.size() == 1;
         boolean containsOleData = false;
         boolean containsWordData = false;
         ArtifactId account = ArtifactId.valueOf(data.getUserArtId());
         TransactionBuilder txBuilder = txFactory.createTransaction(data.getBranch(), account, data.getComment());
         for (WordExtractorData extractorData : extractorDatas) {
            ArtifactReadable artifact = getArtifact(data.getBranch(), extractorData.getGuid());
            WordArtifactChange artChange = new WordArtifactChange();
            artChange.setArtId(artifact.getId());
            if (artifact.isDeleted()) {
               deletedArtifacts.add(artifact.getName());

            } else {
               containsOleData = artifact.getAttributeCount(CoreAttributeTypes.WordOleData) > 0;
               containsWordData = artifact.getAttributeCount(CoreAttributeTypes.WordTemplateContent) > 0;

               if (oleDataElement == null && containsOleData) {
                  txBuilder.setSoleAttributeFromString(artifact, CoreAttributeTypes.WordOleData, "");
                  artChange.setChanged(true);
                  artChange.addChangedAttributeType(WordOleData);
               } else if (oleDataElement != null && singleArtifact) {
                  txBuilder.setSoleAttributeFromStream(artifact, CoreAttributeTypes.WordOleData,
                     new ByteArrayInputStream(WordUtilities.getFormattedContent(oleDataElement)));
                  artChange.setChanged(true);
                  if (!containsOleData) {
                     artChange.setCreated(true);
                  }
                  artChange.addChangedAttributeType(WordOleData);
               }
               String content = Lib.inputStreamToString(
                  new ByteArrayInputStream(WordUtilities.getFormattedContent(extractorData.getParentEelement())));

               boolean hasTrackedChanges = WordCoreUtil.containsWordAnnotations(content);
               QueryFactory query = orcsApi.getQueryFactory();
               BranchId plBranch = WordMLApplicabilityHandler.getProductLineBranch(query, data.getBranch());
               HashCollection<String, String> validFeatureValues = getValidFeatureValuesForBranch(query, plBranch);
               HashSet<String> validConfigurations = WordMLApplicabilityHandler.getValidConfigurations(query, plBranch);

               // If artifact has InvalidApplicabilityTags, do not block the save
               boolean hasInvalidApplicabilityTags =
                  WordCoreUtil.areApplicabilityTagsInvalid(content, plBranch, validFeatureValues, validConfigurations);

               /**
                * Only update if: a. editing a single artifact or b. in multi-edit mode only update if the artifact has
                * at least one textual change (if the MUTI_EDIT_SAVE_ALL_CHANGES preference is not set).
                */
               boolean multiSave = data.isMultiEdit() || hasChangedContent(artifact, content);

               if (singleArtifact || multiSave) {
                  if (!hasTrackedChanges) {
                     if (extractorData.getParentEelement().getNodeName().endsWith("body")) {
                        /*
                         * This code pulls out all of the stuff after the inserted listnum reordering stuff. This needs
                         * to be here so that we remove unwanted template information from single editing
                         */
                        content = content.replace(WordUtilities.LISTNUM_FIELD_HEAD, "");
                     }
                     LinkType linkType = LinkType.OSEE_SERVER_LINK;
                     content = WordMlLinkHandler.unlink(queryFactory, linkType, artifact, content);
                     txBuilder.setSoleAttributeValue(artifact, CoreAttributeTypes.WordTemplateContent, content);
                     artChange.setChanged(true);
                     if (!containsWordData) {
                        artChange.setCreated(true);
                     }
                     artChange.addChangedAttributeType(WordTemplateContent);
                     if (hasInvalidApplicabilityTags) {
                        updateChange.setInvalidApplicabilityTagArts(artifact.getId(), artifact.getName());
                     }
                  } else {
                     if (hasTrackedChanges) {
                        updateChange.setTrackedChangeArts(artifact.getId(), artifact.getName());
                     }
                     if (hasInvalidApplicabilityTags) {
                        updateChange.setInvalidApplicabilityTagArts(artifact.getId(), artifact.getName());
                     }
                  }
               }
               if (artChange.isChanged()) {
                  artChange.setSafetyRelated(checkIfSafetyRelated(artifact,
                     CoreAttributeTypes.LegacyDAL) || checkIfSafetyRelated(artifact, CoreAttributeTypes.ItemDAL));
                  updateChange.addChangedArt(artChange);
                  artChange.addChangedAttributeType(WordTemplateContent);
               }
            }
         }
         TransactionToken tx = txBuilder.commit();
         if (tx.isValid()) {
            postProcessChange(tx, updateChange, account);
         }

      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      } finally {
         if (!deletedArtifacts.isEmpty()) {
            throw new OseeStateException("The following deleted artifacts could not be saved [%s]",
               Collections.toString(",", deletedArtifacts));
         }
      }
      return updateChange;
   }

   private void postProcessChange(TransactionToken tx, WordUpdateChange updateChange, ArtifactId account) {
      updateChange.setTx(tx);
      updateChange.setBranch(tx.getBranch());
      if (updateChange.hasSafetyRelatedArtifactChange()) {
         try {
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(SafetyWorkflowEventHandler.SAFETY_EVENT_BRANCH_ID, tx.getBranch());
            properties.put(SafetyWorkflowEventHandler.SAFETY_EVENT_USER_ART, account);
            Event event = new Event(SafetyWorkflowEventHandler.SAFETY_EVENT_TOPIC, properties);
            eventAdmin.postEvent(event);
         } catch (Exception ex) {
            logger.error(ex, "Could not create safety workflow");
         }
      }
   }

   private boolean hasChangedContent(ArtifactReadable artifact, String content) {
      String originalContent = artifact.getSoleAttributeAsString(CoreAttributeTypes.WordTemplateContent, "");

      return !WordUtilities.textOnly(originalContent).equals(
         WordUtilities.textOnly(content)) || !WordUtilities.referencesOnly(originalContent).equals(
            WordUtilities.referencesOnly(content));
   }

   private boolean checkIfSafetyRelated(ArtifactReadable artifact, AttributeTypeId dalAttrType) {
      String dal = artifact.getSoleAttributeAsString(dalAttrType, "");
      return "A".equals(dal) || "B".equals(dal) || "C".equals(dal);
   }

   private HashCollection<String, String> getValidFeatureValuesForBranch(QueryFactory query, BranchId branch) {
      List<FeatureDefinition> featureDefinitionData = query.applicabilityQuery().getFeatureDefinitionData(branch);

      HashCollection<String, String> validFeatureValues = new HashCollection<>();
      for (FeatureDefinition feat : featureDefinitionData) {
         validFeatureValues.put(feat.getName().toUpperCase(), feat.getValues());
      }

      return validFeatureValues;
   }
}