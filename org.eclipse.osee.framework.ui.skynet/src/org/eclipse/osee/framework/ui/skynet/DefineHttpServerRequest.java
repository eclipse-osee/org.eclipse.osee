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

package org.eclipse.osee.framework.ui.skynet;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.linking.HttpRequest;
import org.eclipse.osee.framework.skynet.core.linking.HttpResponse;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * @author Donald G. Dunne
 */
public class DefineHttpServerRequest implements IHttpServerRequest {

   private static DefineHttpServerRequest instance = new DefineHttpServerRequest();

   public DefineHttpServerRequest() {
      super();
   }

   public static DefineHttpServerRequest getInstance() {
      return instance;
   }

   public String getUrl(Artifact artifact) {
      Map<String, String> keyValues = new HashMap<String, String>();
      String guid = artifact.getGuid();
      if (Strings.isValid(guid)) {
         keyValues.put("guid", guid);
      }
      return HttpUrlBuilder.getInstance().getUrlForLocalSkynetHttpServer(getRequestType(), keyValues);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#getRequestType()
    */
   public String getRequestType() {
      return "Define";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#processRequest(org.eclipse.osee.framework.skynet.core.linking.HttpRequest, org.eclipse.osee.framework.skynet.core.linking.HttpResponse)
    */
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      final Branch branch = BranchPersistenceManager.getInstance().getDefaultBranch();
      String guid = httpRequest.getParameter("guid");
      try {
         final Artifact artifact = ArtifactPersistenceManager.getInstance().getArtifact(guid, branch);
         if (artifact == null) {
            httpResponse.outputStandardError(400, "Artifact can not be found in OSEE on branch " + branch);
            return;
         }
         Displays.ensureInDisplayThread(new Runnable() {

            public void run() {

               IWorkbenchPage page = AWorkbench.getActivePage();
               try {
                  page.openEditor(new ArtifactEditorInput(artifact),
                        "org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor");
                  page.getViewReferences();
               } catch (PartInitException ex) {
                  SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
               }
            }
         });
         String html =
               AHTML.simplePage(artifact.getDescriptiveName() + " has been opened in OSEE on branch " + branch + "<br><br>" + "<form><input type=button onClick='window.opener=self;window.close()' value='Close'></form>");
         httpResponse.getPrintStream().println(html);
      } catch (Exception ex) {
         httpResponse.outputStandardError(400, "Exception handling request ");
      }
   }

}
