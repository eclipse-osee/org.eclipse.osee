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
package org.eclipse.osee.ats.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.workflow.AtsWorkFlow;
import org.eclipse.osee.ats.workflow.VueWorkFlow;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;

/**
 * @author Donald G. Dunne
 */
public class WorkflowDiagramFactory {

   private static WorkflowDiagramFactory instance = new WorkflowDiagramFactory();
   public static String GENERAL_DOCUMENT_ARTIFACT_NAME = "General Document";
   private static Map<Object, String> objToAtsWorkFlowXml = new HashMap<Object, String>();

   private WorkflowDiagramFactory() {
      super();
   }

   public static WorkflowDiagramFactory getInstance() {
      return instance;
   }

   /**
    * Create, import and relate to in default hierarchy the given vue workflow
    * 
    * @param pluginVueFilename
    * @param name
    * @return NativeArtifact created
    */
   public NativeArtifact importWorkflowDiagramToSkynet(InputStream inputStream, String name) throws SQLException, IOException {
      NativeArtifact art = null;
      // System.out.println("Importing diagram " + name);
      art =
            (NativeArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                  GENERAL_DOCUMENT_ARTIFACT_NAME, BranchPersistenceManager.getInstance().getAtsBranch()).makeNewArtifact();
      art.setDescriptiveName(name);
      art.setSoleAttributeValue("Extension", "vue");
      art.setNativeContent(inputStream);
      art.persistAttributes();

      Artifact diagHeadArt = AtsConfig.getInstance().getOrCreateWorkflowDiagramsArtifact();
      diagHeadArt.addChild(art);
      diagHeadArt.persist(true);
      return art;
   }

   public AtsWorkFlow getAtsWorkflowFromSkynet(String diagramName) throws IOException, SQLException {
      Artifact workflowDiagArt = getAtsWorkflowArtifact(diagramName);
      return getAtsWorkflowFromArtifact(workflowDiagArt);
   }

   public NativeArtifact getAtsWorkflowArtifact(String diagramName) throws SQLException {
      ArtifactTypeNameSearch srch =
            new ArtifactTypeNameSearch(GENERAL_DOCUMENT_ARTIFACT_NAME, diagramName,
                  BranchPersistenceManager.getInstance().getAtsBranch());
      return srch.getSingletonArtifactOrException(NativeArtifact.class);
   }

   public boolean atsWorkflowArtifactExists(String diagramName) throws SQLException {
      ArtifactTypeNameSearch srch =
            new ArtifactTypeNameSearch(GENERAL_DOCUMENT_ARTIFACT_NAME, diagramName,
                  BranchPersistenceManager.getInstance().getAtsBranch());
      return srch.getArtifacts(NativeArtifact.class).size() == 1;
   }

   public AtsWorkFlow getAtsWorkflowFromArtifact(Artifact artifact) throws IOException, SQLException {
      if (!objToAtsWorkFlowXml.containsKey(artifact)) {
         NativeArtifact nativeArtifact = (NativeArtifact) artifact;
         InputStream is = nativeArtifact.getNativeContent();
         String vueXml = AFile.readFile(is);
         objToAtsWorkFlowXml.put(artifact, vueXml);
      }
      return (AtsWorkFlow) (new VueWorkFlow(artifact.getDescriptiveName() + " - " + artifact.getHumanReadableId(),
            objToAtsWorkFlowXml.get(artifact))).getWorkflow();
   }

   public AtsWorkFlow getWorkFlowFromFilename(String workFlowFilename) {
      if (!objToAtsWorkFlowXml.containsKey(workFlowFilename)) {
         String vueXml = AFile.readFile(new File(workFlowFilename));
         objToAtsWorkFlowXml.put(workFlowFilename, vueXml);
      }
      return (AtsWorkFlow) (new VueWorkFlow(workFlowFilename, objToAtsWorkFlowXml.get(workFlowFilename))).getWorkflow();
   }

   public AtsWorkFlow getWorkFlowFromFileContents(String name, String vueXml) {
      if (!objToAtsWorkFlowXml.containsKey(name)) {
         objToAtsWorkFlowXml.put(name, vueXml);
      }
      return (AtsWorkFlow) (new VueWorkFlow(name, objToAtsWorkFlowXml.get(name))).getWorkflow();
   }

}
