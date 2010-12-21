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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class DiagramFactory {

   private static DiagramFactory instance = new DiagramFactory();
   public final static String GENERAL_DOCUMENT_ARTIFACT_NAME = "General Document";
   private static Map<Object, String> objToAtsWorkFlowXml = new HashMap<Object, String>();

   private DiagramFactory() {
      super();
   }

   public static DiagramFactory getInstance() {
      return instance;
   }

   public Diagram getWorkFlowFromFileContents(String name, String vueXml) throws OseeCoreException {
      if (!objToAtsWorkFlowXml.containsKey(name)) {
         objToAtsWorkFlowXml.put(name, vueXml);
      }
      return new VueDiagram(name, objToAtsWorkFlowXml.get(name)).getWorkflow();
   }
}