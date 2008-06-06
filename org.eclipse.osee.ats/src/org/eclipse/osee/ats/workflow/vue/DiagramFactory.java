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
package org.eclipse.osee.ats.workflow.vue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.AtsConfig;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;

/**
 * @author Donald G. Dunne
 */
public class DiagramFactory {

   private static DiagramFactory instance = new DiagramFactory();
   public static String GENERAL_DOCUMENT_ARTIFACT_NAME = "General Document";
   private static Map<Object, String> objToAtsWorkFlowXml = new HashMap<Object, String>();

   private DiagramFactory() {
      super();
   }

   public static DiagramFactory getInstance() {
      return instance;
   }

   /**
    * Create, import and relate to in default hierarchy the given vue workflow
    * 
    * @param pluginVueFilename
    * @param name
    * @return NativeArtifact created
    * @throws MultipleAttributesExist
    */
   public NativeArtifact importDiagramToSkynet(InputStream inputStream, String name) throws Exception {
      NativeArtifact art = null;
      // System.out.println("Importing diagram " + name);
      art =
            (NativeArtifact) ArtifactTypeManager.addArtifact(GENERAL_DOCUMENT_ARTIFACT_NAME,
                  BranchPersistenceManager.getAtsBranch(), name);
      art.setSoleAttributeValue("Extension", "vue");
      art.setNativeContent(inputStream);
      art.persistAttributes();

      Artifact diagHeadArt = AtsConfig.getInstance().getOrCreateAtsHeadingArtifact();
      diagHeadArt.addChild(art);
      diagHeadArt.persistAttributesAndRelations();
      return art;
   }

   public Diagram getAtsWorkflowFromSkynet(String diagramName) throws Exception {
      Artifact workflowDiagArt = getAtsWorkflowArtifact(diagramName);
      return getAtsWorkflowFromArtifact(workflowDiagArt);
   }

   public NativeArtifact getAtsWorkflowArtifact(String diagramName) throws Exception {
      return (NativeArtifact) ArtifactQuery.getArtifactFromTypeAndName(GENERAL_DOCUMENT_ARTIFACT_NAME, diagramName,
            AtsPlugin.getAtsBranch());

   }

   public boolean atsWorkflowArtifactExists(String diagramName) throws SQLException {
      return ArtifactQuery.getArtifactsFromTypeAndName(GENERAL_DOCUMENT_ARTIFACT_NAME, diagramName,
            AtsPlugin.getAtsBranch()).size() == 1;
   }

   public Diagram getAtsWorkflowFromArtifact(Artifact artifact) throws IOException, SQLException, MultipleAttributesExist, AttributeDoesNotExist {
      if (!objToAtsWorkFlowXml.containsKey(artifact)) {
         NativeArtifact nativeArtifact = (NativeArtifact) artifact;
         InputStream is = nativeArtifact.getNativeContent();
         String vueXml = AFile.readFile(is);
         objToAtsWorkFlowXml.put(artifact, vueXml);
      }
      return (Diagram) (new VueDiagram(artifact.getDescriptiveName() + " - " + artifact.getHumanReadableId(),
            objToAtsWorkFlowXml.get(artifact))).getWorkflow();
   }

   public Diagram getWorkFlowFromFilename(String workFlowFilename) {
      if (!objToAtsWorkFlowXml.containsKey(workFlowFilename)) {
         String vueXml = AFile.readFile(new File(workFlowFilename));
         objToAtsWorkFlowXml.put(workFlowFilename, vueXml);
      }
      return (Diagram) (new VueDiagram(workFlowFilename, objToAtsWorkFlowXml.get(workFlowFilename))).getWorkflow();
   }

   public Diagram getWorkFlowFromFileContents(String name, String vueXml) {
      if (!objToAtsWorkFlowXml.containsKey(name)) {
         objToAtsWorkFlowXml.put(name, vueXml);
      }
      return (Diagram) (new VueDiagram(name, objToAtsWorkFlowXml.get(name))).getWorkflow();
   }

}
