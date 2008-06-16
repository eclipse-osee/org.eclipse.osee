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
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.AFile;

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
