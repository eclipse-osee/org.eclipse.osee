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
package org.eclipse.osee.framework.ui.skynet.render.word;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.collection.tree.Tree;
import org.eclipse.osee.framework.jdk.core.collection.tree.TreeNode;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Jeff C. Phillips
 */
public class SrsProducer implements IWordMlProducer {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

   // Some filtering for testing
   private int dbg_count = 0;
   private final int DBG_MAX = Integer.MAX_VALUE;

   public SrsProducer() throws SQLException {
   }

   public BlamVariableMap process(BlamVariableMap variableMap) throws SQLException {
      if (variableMap == null) throw new IllegalArgumentException("variableMap must not be null");

      String name = variableMap.getString("Name");
      Branch branch = variableMap.getBranch("Branch");
      Artifact root = artifactManager.getDefaultHierarchyRootArtifact(branch);
      Artifact softwareRequirement = root.getChild("Software Requirements");
      Artifact crewInterface = softwareRequirement.getChild("Crew Interface");
      Artifact subsystemManagement = softwareRequirement.getChild("Subsystem Management");
      Artifact appendices = softwareRequirement.getChild("SRS Appendices");

      Tree<Object> objects = new Tree<Object>();

      try {
         process(crewInterface, objects, name);
         process(subsystemManagement, objects, name);
         process(appendices, objects, name);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

      variableMap.setValue("useTree", Boolean.TRUE);
      variableMap.setValue("srsProducer.objects", objects);
      return variableMap;
   }

   private void process(Artifact parent, Tree<Object> artifacts, String name) throws IllegalStateException, IOException, SQLException {
      for (Artifact child : parent.getChildren()) {
         if (child.getDescriptiveName().contains(name)) {
            TreeNode<Object> parentNode = artifacts.getRoot().addChild(parent);
            processChildren(parentNode, child, artifacts);
         }
      }
   }

   private void processChildren(TreeNode<Object> parentNode, Artifact artifact, Tree<Object> artifacts) throws IllegalStateException, IOException {
      if (dbg_count > DBG_MAX) return;

      TreeNode<Object> artifactNode = null;
      dbg_count++;

      artifactNode = parentNode.addChild(artifact);

      try {
         for (Artifact child : artifact.getChildren()) {
            processChildren(artifactNode, child, artifacts);
         }
      } catch (SQLException ex) {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   // private void process(String partition, String subsystem, Artifact parent, Tree<Object>
   // artifacts) throws IllegalStateException, IOException {
   // TreeNode parentNode = artifacts.getRoot().addChild(subsystem + " Subsystem");
   //
   // try {
   // for (Artifact child : parent.getChildren()) {
   // if (child.getSoleAttributeValue("Subsystem").equals(subsystem)) {
   // processChildren(parentNode, child, partition, subsystem, artifacts);
   // }
   // }
   //
   // if (parentNode.getChildren().isEmpty()) {
   // artifacts.getRoot().getChildren().remove(parentNode);
   // }
   // }
   // catch (SQLException ex) {
   // SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
   // }
   // }

   // private void processChildren(TreeNode<Object> parentNode, Artifact artifact, String partition,
   // String subsystem,
   // Tree<Object> artifacts) throws IllegalStateException, IOException {
   // boolean goodToPublish = artifactOnlyInPartition(artifact, partition);
   //
   // if (dbg_count > DBG_MAX)
   // return;
   //
   // TreeNode<Object> artifactNode = null;
   // if (goodToPublish) {
   // // DEBUG
   // dbg_count++;
   //
   // artifactNode = parentNode.addChild(artifact);
   // }
   //
   // try {
   // for (Artifact child : artifact.getChildren()) {
   // processChildren(goodToPublish ? artifactNode : parentNode, child, partition, subsystem,
   // artifacts);
   // }
   // }
   // catch (SQLException ex) {
   // SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
   // }
   // }

   // private boolean artifactOnlyInPartition(Artifact artifact, String partition) throws
   // IOException {
   // try {
   // Collection<Attribute> attributes = artifact.getAttributeManager("Partition").getAttributes();
   // return attributes.size() == 1 &&
   // attributes.iterator().next().getStringData().equals(partition);
   // }
   // catch (Exception ex) {
   // // recordError(wordMl, artifact, ex);
   // return false;
   // }
   // }
}
