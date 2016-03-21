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
package org.eclipse.osee.define.report.internal.wordupdate;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON_ID;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionAdapter;
import org.eclipse.osee.ats.core.users.AtsCoreUsers;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.define.report.api.WordArtifactChange;
import org.eclipse.osee.define.report.api.WordUpdateChange;
import org.eclipse.osee.define.report.api.WordUpdateData;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import com.google.common.collect.Lists;

/**
 * @author Ryan D. Brooks
 * @author David W. Miller
 */
public class WordUpdateArtifact {
   private final OrcsApi orcsApi;
   private final QueryFactory queryFactory;
   private IAtsServer atsServer;
   private final Log logger;

   public WordUpdateArtifact(Log logger, OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.logger = logger;
   }

   public WordUpdateChange updateArtifacts(WordUpdateData data, IAtsServer atsServer) {
      this.atsServer = atsServer;
      Collection<WordExtractorData> extractorDatas;
      Element oleDataElement;
      try {
         IElementExtractor elementExtractor;
         Document document = extractJaxpDocument(data);
         if (data.isThreeWayMerge()) {
            String guid = orcsApi.getQueryFactory().fromBranch(data.getBranch()).andUuid(
               data.getArtifacts().iterator().next()).getResults().getAtMostOneOrNull().getGuid();
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

   public ArtifactReadable getArtifact(Long branchUuid, long uuid) {
      ArtifactReadable toReturn = queryFactory.fromBranch(branchUuid).andUuid(uuid).getResults().getExactlyOne();
      return toReturn;
   }

   public ArtifactReadable getArtifact(Long branchUuid, String guid) {
      ArtifactReadable toReturn = queryFactory.fromBranch(branchUuid).andGuid(guid).getResults().getExactlyOne();
      return toReturn;
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
         ArtifactReadable userArtifact = getArtifact(COMMON_ID, data.getUserArtId());
         TransactionBuilder txBuilder = txFactory.createTransaction(data.getBranch(), userArtifact, data.getComment());
         for (WordExtractorData extractorData : extractorDatas) {
            ArtifactReadable artifact = getArtifact(data.getBranch(), extractorData.getGuid());
            WordArtifactChange artChange = new WordArtifactChange();
            artChange.setArtId(artifact.getLocalId());
            if (artifact.isDeleted()) {
               deletedArtifacts.add(artifact.getName());

            } else {
               containsOleData = artifact.getAttributeCount(CoreAttributeTypes.WordOleData) > 0;
               containsWordData = artifact.getAttributeCount(CoreAttributeTypes.WordTemplateContent) > 0;

               if (oleDataElement == null && containsOleData) {
                  txBuilder.setSoleAttributeFromString(artifact, CoreAttributeTypes.WordOleData, "");
                  artChange.setChanged(true);
                  artChange.addChangedAttrType(CoreAttributeTypes.WordOleData.getGuid());
               } else if (oleDataElement != null && singleArtifact) {
                  txBuilder.setSoleAttributeFromStream(artifact, CoreAttributeTypes.WordOleData,
                     new ByteArrayInputStream(WordUtilities.getFormattedContent(oleDataElement)));
                  artChange.setChanged(true);
                  if (!containsOleData) {
                     artChange.setCreated(true);
                  }
                  artChange.addChangedAttrType(CoreAttributeTypes.WordOleData.getGuid());
               }
               String content = Lib.inputStreamToString(
                  new ByteArrayInputStream(WordUtilities.getFormattedContent(extractorData.getParentEelement())));

               /**
                * Only update if: a. editing a single artifact or b. in multi-edit mode only update if the artifact has
                * at least one textual change (if the MUTI_EDIT_SAVE_ALL_CHANGES preference is not set).
                */

               boolean multiSave = data.isMultiEdit() || hasChangedContent(artifact, content);
               if (singleArtifact || multiSave) {
                  if (extractorData.getParentEelement().getNodeName().endsWith("body")) {
                     /*
                      * This code pulls out all of the stuff after the inserted listnum reordering stuff. This needs to
                      * be here so that we remove unwanted template information from single editing
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
                  artChange.addChangedAttrType(CoreAttributeTypes.WordTemplateContent.getGuid());
               }
               if (artChange.isChanged()) {
                  artChange.setSafetyRelated(checkIfSafetyRelated(artifact));
                  updateChange.addChangedArt(artChange);
                  artChange.addChangedAttrType(CoreAttributeTypes.WordTemplateContent.getGuid());
               }
            }
         }
         TransactionReadable tx = txBuilder.commit();
         if (tx != null) {
            postProcessChange(tx, updateChange, userArtifact);
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

   private void postProcessChange(TransactionReadable tx, WordUpdateChange updateChange, ArtifactReadable userId) {
      updateChange.setTx(tx.getLocalId());
      updateChange.setBranch(tx.getBranch());
      if (updateChange.hasSafetyRelatedArtifactChange()) {
         try {
            ArtifactReadable assocArt = getAssociatedWorkflowArt(tx.getBranch());
            IAtsTeamWorkflow safetyWf = getSafetyWorkflow(assocArt);
            if (safetyWf == null) {
               IAtsTeamWorkflow teamWf = atsServer.getWorkItemFactory().getTeamWf(assocArt);
               safetyWf = createSafetyAction(tx, updateChange, teamWf, userId);
            }
         } catch (Exception ex) {
            logger.error(ex, "Could not create safety workflow");
         }
      }
   }

   private ArtifactReadable getAssociatedWorkflowArt(BranchId branchId) {
      ArtifactReadable toReturn = null;
      BranchReadable branch = queryFactory.branchQuery().andIds(branchId).getResults().getExactlyOne();
      long workflowUuid = branch.getAssociatedArtifactId();
      try {
         toReturn = atsServer.getQuery().andUuid(workflowUuid).andIsOfType(
            AtsArtifactTypes.TeamWorkflow).getResults().getExactlyOne();
      } catch (Exception ex) {
         throw new OseeCoreException(ex, "Exception in getAssociatedWorkflowArt: %s", workflowUuid);
      }
      return toReturn;
   }

   private IAtsTeamWorkflow getSafetyWorkflow(ArtifactReadable workflowArt) {
      Conditions.checkNotNull(workflowArt, "work flow artifact");
      IAtsTeamWorkflow safetyWorkflow = null;
      ArtifactReadable safetyActionableItemArt = atsServer.getArtifact(AtsArtifactToken.SafetyActionableItem.getUuid());
      IAtsTeamWorkflow teamWf = atsServer.getWorkItemFactory().getTeamWf(workflowArt);
      IAtsActionableItem actionableItem = atsServer.getConfigItemFactory().getActionableItem(safetyActionableItemArt);
      for (IAtsTeamWorkflow sibling : atsServer.getActionFactory().getSiblingTeamWorkflows(teamWf)) {
         if (sibling.getActionableItems().contains(actionableItem)) {
            safetyWorkflow = sibling;
            break;
         }
      }
      return safetyWorkflow;
   }

   private IAtsTeamWorkflow createSafetyAction(TransactionReadable tx, WordUpdateChange updateChange, IAtsTeamWorkflow teamWf, ArtifactReadable userArt) {
      IAtsTeamWorkflow teamWorkflow = null;
      try {
         IAtsActionableItem ai = atsServer.getConfig().getSoleByUuid(AtsArtifactToken.SafetyActionableItem.getUuid(),
            IAtsActionableItem.class);
         if (ai == null) {
            throw new OseeCoreException("Safety Actionable Item not configured");
         }
         IAtsTeamDefinition teamDef = atsServer.getConfig().getSoleByUuid(
            AtsArtifactToken.SafetyTeamDefinition.getUuid(), IAtsTeamDefinition.class);
         if (teamDef == null) {
            throw new OseeCoreException("Safety Team Definition not configured");
         }
         IAtsUser createdBy = AtsCoreUsers.SYSTEM_USER;
         IAtsChangeSet changes = atsServer.getStoreService().createAtsChangeSet("Create System Safety Workflow",
            atsServer.getUserService().getUserById(userArt.getSoleAttributeAsString(CoreAttributeTypes.UserId)));
         IAtsAction action = atsServer.getActionFactory().getAction(teamWf);
         teamWorkflow = atsServer.getActionFactory().createTeamWorkflow(action, teamDef,
            java.util.Collections.singleton(ai), null, changes, new Date(), createdBy, new NewActionAdapter() {

               @Override
               public void teamCreated(IAtsAction action, IAtsTeamWorkflow teamWf, IAtsChangeSet changes) throws OseeCoreException {
                  changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.Description,
                     "Review System Safety Changes for the associated RPCR to Complete the Workflow");
               }

            });
         changes.setSoleAttributeValue(teamWorkflow, CoreAttributeTypes.Name,
            "Safety Workflow for " + teamWf.getAtsId());
         changes.execute();
      } catch (Exception ex) {
         logger.error(ex, "WordUpdateData Safety Action creation");
      }
      return teamWorkflow;
   }

   private boolean hasChangedContent(ArtifactReadable artifact, String content) {
      String originalContent = artifact.getSoleAttributeAsString(CoreAttributeTypes.WordTemplateContent, "");

      return !WordUtilities.textOnly(originalContent).equals(
         WordUtilities.textOnly(content)) || !WordUtilities.referencesOnly(originalContent).equals(
            WordUtilities.referencesOnly(content));
   }

   private boolean checkIfSafetyRelated(ArtifactReadable artifact) {
      String dal = artifact.getSoleAttributeAsString(CoreAttributeTypes.LegacyDAL, "");
      return "A".equals(dal) || "B".equals(dal) || "C".equals(dal);
   }

}
