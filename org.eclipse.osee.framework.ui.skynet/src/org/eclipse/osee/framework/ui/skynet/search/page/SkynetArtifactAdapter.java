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
package org.eclipse.osee.framework.ui.skynet.search.page;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.DynamicRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.LinkDescriptorPersistenceMemo;
import org.eclipse.osee.framework.skynet.core.sql.SkynetRevisionControl;
import org.eclipse.osee.framework.skynet.core.sql.SkynetSql;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.RsetProcessor;
import org.eclipse.osee.framework.ui.skynet.search.page.data.ArtifactTypeNode;
import org.eclipse.osee.framework.ui.skynet.search.page.data.AttributeTypeNode;
import org.eclipse.osee.framework.ui.skynet.search.page.data.RelationTypeNode;

public class SkynetArtifactAdapter {
   private static SkynetArtifactAdapter instance = null;
   private static SkynetSql skynetSql = SkynetSql.getInstance();
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();

   private SkynetArtifactAdapter() {
   }

   public static SkynetArtifactAdapter getInstance() {
      if (instance == null) {
         instance = new SkynetArtifactAdapter();
      }
      return instance;
   }

   public Map<String, Map<String, Integer>> getBranchRevisions() {
      Map<String, Map<String, Integer>> map = new HashMap<String, Map<String, Integer>>();
      try {
         ConnectionHandlerStatement statement =
               ConnectionHandler.runPreparedQuery(SkynetRevisionControl.SELECT_REVISION);
         ResultSet rSet = statement.getRset();
         while (rSet.next()) {
            int branchId = rSet.getInt("branchId");
            int min = rSet.getInt("minTX");
            int max = rSet.getInt("maxTX");
            String branch = rSet.getString("branchName");
            if (branch != null && !branch.equals("")) {
               Map<String, Integer> valuesMap = new HashMap<String, Integer>();
               valuesMap.put("branchId", branchId);
               valuesMap.put("minTX", min);
               valuesMap.put("maxTX", max);
               map.put(branch, valuesMap);
            }
         }
         rSet.close();
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      return map;
   }

   private Collection<IRelationLinkDescriptor> getIRelationLinkDescriptorsFromArtifactTypeId(int artTypeid, int branchId, final int revision) throws SQLException {
      Collection<IRelationLinkDescriptor> relationsTypes = new LinkedList<IRelationLinkDescriptor>();
      String sql =
            skynetSql.getMetaDataSql().getRelationTypeBy("art_type_id", Integer.toString(artTypeid), branchId, revision);
      try {
         Query.acquireCollection(relationsTypes, sql, new RsetProcessor<IRelationLinkDescriptor>() {
            public IRelationLinkDescriptor process(ResultSet set) throws SQLException {
               IRelationLinkDescriptor descriptor = null;
               try {

                  TransactionId transactionId =
                        transactionIdManager.getEditableTransactionId(branchManager.getDefaultBranch());

                  descriptor =
                        new DynamicRelationLinkDescriptor(set.getString("type_name"), set.getString("a_name"),
                              set.getString("b_name"), set.getString("ab_phrasing"), set.getString("ba_phrasing"),
                              set.getString("short_name"), transactionId);
                  descriptor.setPersistenceMemo(new LinkDescriptorPersistenceMemo(set.getInt("rel_link_type_id")));

               } catch (Exception ex) {
                  ex.printStackTrace();
               }

               return descriptor;
            }

            public boolean validate(IRelationLinkDescriptor item) {
               return item != null;
            }
         });
      } catch (SQLException ex) {
         ex.printStackTrace();
      }

      return relationsTypes;
   }

   private Collection<ArtifactSubtypeDescriptor> getArtifactTypeDescriptorsFromRelationLinkId(int relationLinkId, int branchId, int revision) throws SQLException {
      Collection<ArtifactSubtypeDescriptor> descriptors = new LinkedList<ArtifactSubtypeDescriptor>();
      // TODO this method should just ask the ConfigurationPersistenceManager and return the result
      return descriptors;
   }

   public ArtifactTypeNode createArtifactTypeNode(ArtifactSubtypeDescriptor artifactType, int branchId, int revision) {
      return createArtifactTypeNode(artifactType, "", branchId, revision); // Accept all relations
   }

   // Filter parent relations unless "" or null is passed in.
   public ArtifactTypeNode createArtifactTypeNode(ArtifactSubtypeDescriptor artifactType, String parentRelationName, int branchId, int revision) {
      ArtifactTypeNode artifactTypeNode = new ArtifactTypeNode(artifactType);
      Collection<DynamicAttributeDescriptor> attributeTypes = null;
      Collection<IRelationLinkDescriptor> relationsTypes = null;
      int artTypeid = artifactType.getArtTypeId();
      if (parentRelationName == null) {
         parentRelationName = "";
      }
      try {
    	 Branch branch = BranchPersistenceManager.getInstance().getBranch(branchId);
         attributeTypes = ConfigurationPersistenceManager.getInstance().getAttributeTypesFromArtifactType(artifactType, branch);
         relationsTypes = getIRelationLinkDescriptorsFromArtifactTypeId(artTypeid, branchId, revision);
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      for (DynamicAttributeDescriptor descriptor : attributeTypes) {
         artifactTypeNode.addChild(new AttributeTypeNode(descriptor));
      }
      for (IRelationLinkDescriptor relationType : relationsTypes) {
         if (!relationType.getName().equals(parentRelationName)) {
            artifactTypeNode.addChild(new RelationTypeNode(relationType, artifactTypeNode, branchId, revision));
         }
      }
      return artifactTypeNode;
   }

   public Collection<ArtifactSubtypeDescriptor> getValidArtifactTypesForRelationLink(IRelationLinkDescriptor relationLinkDescriptor, int branchId, int revision) {
      Collection<ArtifactSubtypeDescriptor> descriptors = null;
      try {
         if (relationLinkDescriptor != null && relationLinkDescriptor.getPersistenceMemo() != null) {
            int relationLinkId = relationLinkDescriptor.getPersistenceMemo().getLinkTypeId();
            descriptors = getArtifactTypeDescriptorsFromRelationLinkId(relationLinkId, branchId, revision);
         }
      } catch (SQLException ex) {
         ex.printStackTrace();
      }
      return descriptors;
   }
}