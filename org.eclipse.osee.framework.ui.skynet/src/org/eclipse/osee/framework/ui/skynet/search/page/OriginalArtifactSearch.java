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

import static org.eclipse.osee.framework.skynet.core.artifact.search.Operator.EQUAL;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.collection.tree.Tree;
import org.eclipse.osee.framework.jdk.core.collection.tree.TreeNode;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeIdSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.artifact.search.RelationTypeIdSearch;
import org.eclipse.osee.framework.ui.skynet.search.page.data.ArtifactTypeNode;
import org.eclipse.osee.framework.ui.skynet.search.page.data.RelationTypeNode;

public class OriginalArtifactSearch {

   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

   // public static void dummySearch() {
   // String name, version, pass, fail, status, executionDate;
   //
   // // ResultSetStructure resultSet = new ResultSetStructure();
   //
   // // resultSet.addColumn("name", 1);
   // // resultSet.addColumn("version", 2);
   // // resultSet.addColumn("pass", 3);
   // // resultSet.addColumn("fail", 4);
   // // resultSet.addColumn("status", 5);
   // // resultSet.addColumn("execution date", 6);
   //
   // try {
   // // GET ALL OF THE TEST_SCRIPTS
   // List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
   // criteria.add(new ArtifactTypeSearch(TestScript.ARTIFACT_NAME
   // .toString(), EQUAL));
   // Collection<Artifact> artifacts = ArtifactPersistenceManager
   // .getInstance().getArtifacts(criteria, true,
   // Branch.getDefaultBranch());
   // Iterator<Artifact> artIt = artifacts.iterator();
   // while (artIt.hasNext()) {
   // Artifact artifact = artIt.next();
   //
   // name = artifact.getAttribute(TestAttributes.NAME.toString())
   // .getSoleAttributeValue();
   //
   // Set<Artifact> configurations = artifact
   // .getArtifacts(RelationSide.TestConfigurationRelation_TestConfiguration);
   // Iterator<Artifact> configIt = configurations.iterator();
   // while (configIt.hasNext()) {
   // Artifact configuration = configIt.next();
   //
   // version = configuration.getAttribute(
   // TestAttributes.VERSION.toString())
   // .getSoleAttributeValue();
   //
   // Set<Artifact> runs = configuration
   // .getArtifacts(RelationSide.TestRunConfigRelation_TestRun);
   // Iterator<Artifact> runIt = runs.iterator();
   // while (runIt.hasNext()) {
   // Artifact run = runIt.next();
   //
   // Set<Artifact> users = run
   // .getArtifacts(RelationSide.RunByRelation_User);
   // Iterator<Artifact> userIt = users.iterator();
   // while (userIt.hasNext()) {
   // // Artifact user = userIt.next();
   //
   // // // User Run
   // // Attributes[] attributes =
   // // User.Attributes.values();
   // // for (Attributes attribute : attributes) {
   // // (attribute.name().toLowerCase(),
   // // user.getAttribute(attribute.name()).getSoleAttributeValue());
   // // }
   //
   // pass = run.getAttribute(
   // TestAttributes.TEST_PASS.toString())
   // .getSoleAttributeValue();
   // fail = run.getAttribute(
   // TestAttributes.TEST_FAIL.toString())
   // .getSoleAttributeValue();
   // status = run.getAttribute(
   // TestAttributes.TEST_STATUS.toString())
   // .getSoleAttributeValue();
   // executionDate = run.getAttribute(
   // TestAttributes.EXECUTION_DATE.toString())
   // .getSoleAttributeValue();
   //
   // // userName =
   // // run.getAttribute(TestAttributes.EXECUTION_DATE.toString()).getSoleAttributeValue();
   //
   // // resultSet.addRow(name, version, pass, fail, status,
   // // executionDate);
   //
   // }
   //
   // }
   // }
   // }
   // } catch (SQLException e) {
   // 
   // e.printStackTrace();
   //
   // }
   //
   // }

   public void recursiveArtifactSearch(TreeNode<Artifact> node) {

      List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
      try {
         Collection<Artifact> artifacts =
               artifactManager.getArtifacts(criteria, true, branchManager.getDefaultBranch());
         Iterator<Artifact> artifact = artifacts.iterator();
         while (artifact.hasNext()) {

         }
      } catch (SQLException e) {

         e.printStackTrace();
      }
   }

   // private Collection<Artifact> getArtifacts(int ArtifactTypeId, int RelationTypeId) {
   // // ArtifactTypeIdSearch
   // return null;
   // }

   // private List<ISearchPrimitive> buildSearch(ArtifactTypeNode parent, Artifact artifact){
   // List<ISearchPrimitive> search = new ArrayList<ISearchPrimitive>();
   //		
   // TreeObject[] objs = parent.getChildren();
   // for( TreeObject obj : objs ){
   // if(obj instanceof RelationTypeNode){
   //				
   // }
   // }
   // return search;
   // }

   public Tree<Artifact> getArtifactSearch2(TreeParent parent) throws SQLException {
      Tree<Artifact> artifactTree = new Tree<Artifact>();
      if (parent instanceof ArtifactTypeNode) {
         ArtifactTypeNode artNode = ((ArtifactTypeNode) parent);
         TreeObject[] treeObjects = artNode.getChildren();
         List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
         ISearchPrimitive search = getSearchPrimative(parent);
         if (search != null) {
            criteria.add(search);
         }
         for (TreeObject treeObject : treeObjects) {
            if (treeObject.isChecked()) {
               search = getSearchPrimative(treeObject);
               System.out.println(search.getArtIdColName());
               System.out.println(search.getArtIdColName() + "1");
               if (search != null) {
                  criteria.add(search);
               }
            }
         }
         System.out.println("hello 5");
         Collection<Artifact> artifacts =
               artifactManager.getArtifacts(criteria, true, branchManager.getDefaultBranch());
         System.out.println("hello 6");
         artifactTree.getRoot().addChildren(artifacts);

      }
      return artifactTree;
   }

   public Tree<Artifact> getArtifactSearch(TreeParent parent) throws SQLException {
      Tree<Artifact> artifactTree = new Tree<Artifact>();
      if (parent instanceof ArtifactTypeNode) {
         List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
         ISearchPrimitive search = getSearchPrimative(parent);
         if (search != null) {
            criteria.add(search);
         }
         Collection<Artifact> artifacts =
               artifactManager.getArtifacts(criteria, true, branchManager.getDefaultBranch());
         artifactTree.getRoot().addChildren(artifacts);

      }
      return artifactTree;
   }

   private ISearchPrimitive getSearchPrimative(TreeObject treeObject) {
      if (treeObject instanceof ArtifactTypeNode) {
         return new ArtifactTypeIdSearch(((ArtifactTypeNode) treeObject).getSubTypeDescriptor().getArtTypeId(), EQUAL);
      } else if (treeObject instanceof RelationTypeNode) {
         return new RelationTypeIdSearch(
               ((RelationTypeNode) treeObject).getRelationLinkDescriptor().getPersistenceMemo().getLinkTypeId(), EQUAL);
      }
      return null;
   }

}
