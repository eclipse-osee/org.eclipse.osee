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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.linking.HttpRequest;
import org.eclipse.osee.framework.skynet.core.linking.HttpResponse;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;

/**
 * @author Roberto E. Escobar
 */
public class BranchRequest implements IHttpServerRequest {

   private static final BranchRequest instance = new BranchRequest();
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchRequest.class);

   private BranchRequest() {
   }

   public static BranchRequest getInstance() {
      return instance;
   }

   public String getUrl(Artifact artifact) {
      Map<String, String> keyValues = new HashMap<String, String>();
      return HttpUrlBuilder.getInstance().getUrlForLocalSkynetHttpServer(getRequestType(), keyValues);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#getRequestType()
    */
   public String getRequestType() {
      return "GET.BRANCHES";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#processRequest(org.eclipse.osee.framework.skynet.core.linking.HttpRequest,
    *      org.eclipse.osee.framework.skynet.core.linking.HttpResponse)
    */
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      try {
         List<Branch> branches = BranchPersistenceManager.getBranches();

         Document doc = Jaxp.newDocument();
         Element root = Jaxp.createElement(doc, "skynet.branches", "");
         doc.appendChild(root);
         for (Branch branch : branches) {
            root.appendChild(createBranchElement(doc, branch));
         }
         doc.setXmlStandalone(true);
         httpResponse.getPrintStream().println(Jaxp.xmlToString(doc, new OutputFormat(doc, "UTF-8", true)));
      } catch (Exception ex) {
         httpResponse.outputStandardError(400, "Exception handling request", ex);
      }
   }

   private Element createBranchElement(Document doc, Branch branch) {
      Element branchEl = Jaxp.createElement(doc, "branch", "");
      try {
         branchEl.setAttribute("name", branch.getBranchName());
         branchEl.setAttribute("id", Integer.toString(branch.getBranchId()));
         // branchEl.setAttribute("comment", "");
         // branchEl.setAttribute("author",
         // SkynetAuthentication.getInstance().getUserByArtId(branch.getAuthorId()).getName());
         // branchEl.setAttribute("creationDate", "");

         //TODO need to find out if this should be calling getChildBranches recursively
         //That is how it was running before so I will continue to run it that way.
         Collection<Branch> branches = branch.getChildBranches(true);
         if (branches.size() != 0) {
            for (Branch childBranch : branches) {
               branchEl.appendChild(createBranchElement(doc, childBranch));
            }
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, "Error obtaining branch information.", ex);
      }
      return branchEl;
   }
}
