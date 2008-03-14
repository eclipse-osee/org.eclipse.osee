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
package org.eclipse.osee.framework.ui.skynet.httpRequests;

import java.sql.SQLException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.linking.HttpRequest;
import org.eclipse.osee.framework.skynet.core.linking.HttpResponse;
import org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTreeRequest implements IHttpServerRequest {

   private static final String BRANCH_KEY = "branchId";
   private static final String FROM_KEY = "guid";
   private static final String LEVELS_KEY = "levels";
   private static final ArtifactTreeRequest instance = new ArtifactTreeRequest();
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactTreeRequest.class);

   private ArtifactTreeRequest() {
   }

   public static ArtifactTreeRequest getInstance() {
      return instance;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#getRequestType()
    */
   public String getRequestType() {
      return "GET.ARTIFACT.TREE";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#processRequest(org.eclipse.osee.framework.skynet.core.linking.HttpRequest, org.eclipse.osee.framework.skynet.core.linking.HttpResponse)
    */
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      String branchKey = httpRequest.getParameter(BRANCH_KEY);
      String guidLevel = httpRequest.getParameter(FROM_KEY);
      String levelKey = httpRequest.getParameter(LEVELS_KEY);

      try {
         Branch branch = BranchPersistenceManager.getInstance().getBranch(Integer.parseInt(branchKey));

         Artifact artifact = null;
         if (Strings.isValid(guidLevel)) {
            artifact = ArtifactPersistenceManager.getInstance().getArtifact(guidLevel, branch);
         } else {
            artifact = ArtifactPersistenceManager.getInstance().getDefaultHierarchyRootArtifact(branch);
         }

         Document doc = Jaxp.newDocument();
         Element root = Jaxp.createElement(doc, "artifact.tree", "");
         doc.appendChild(root);

         if (Strings.isValid(guidLevel)) {
            root.setAttribute("guid", guidLevel);
         }

         if (artifact != null) {
            int level = 0;
            try {
               level = Integer.parseInt(levelKey);
            } catch (NumberFormatException ex) {
               level = 1;
            }
            buildArtifactTree(doc, root, artifact, level);
         }
         doc.setXmlStandalone(true);
         httpResponse.getPrintStream().println(Jaxp.xmlToString(doc, new OutputFormat(doc, "UTF-8", true)));
      } catch (Exception ex) {
         httpResponse.outputStandardError(400, "Exception handling request", ex);
      }
   }

   private void buildArtifactTree(Document doc, Element element, Artifact artifact, final int level) {
      if (level > 0) {
         Set<Artifact> children;
         try {
            children = artifact.getChildren();

            for (Artifact child : children) {
               Element childElement = createArtifactElement(doc, child, level);
               element.appendChild(childElement);
               buildArtifactTree(doc, childElement, child, level - 1);
            }
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error sending error string", ex);
         }
      }
   }

   private Element createArtifactElement(Document doc, Artifact artifact, int level) {
      Element newElement = Jaxp.createElement(doc, "artifact", "");
      newElement.setAttribute("name", artifact.getDescriptiveName());
      newElement.setAttribute("guid", artifact.getGuid());
      if (level <= 1) {
         try {
            newElement.setAttribute("hasChildren", Boolean.toString(artifact.getChildren().size() > 0));
         } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error sending error string", ex);
         }
      }
      return newElement;
   }
}
