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

package org.eclipse.osee.framework.ui.skynet.templates;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.IDbInitializationTask;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;

public class SimpleTemplateProviderDbTask implements IDbInitializationTask {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#run(java.sql.Connection)
    */
   public void run(OseeConnection connection) throws OseeCoreException {
      try {
         processTemplatesForDBInit();
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   private void processTemplatesForDBInit() throws IOException, OseeCoreException {

      Artifact templateFolder = getTemplateFolder();
      IExtensionPoint ep =
            Platform.getExtensionRegistry().getExtensionPoint(
                  "org.eclipse.osee.framework.ui.skynet.SimpleTemplateProviderTemplate");
      for (IExtension extension : ep.getExtensions()) {
         for (IConfigurationElement el : extension.getConfigurationElements()) {
            Artifact templateArtifact =
                  ArtifactTypeManager.addArtifact("Renderer Template", BranchManager.getCommonBranch());
            String filePath = el.getAttribute("File");
            String name = filePath.substring(filePath.lastIndexOf('/') + 1);
            name = name.substring(0, name.lastIndexOf('.'));
            URL url = Platform.getBundle(el.getContributor().getName()).getEntry(filePath);

            if (url != null) {
               templateArtifact.setSoleAttributeValue("Name", name);
               templateArtifact.setSoleAttributeFromStream(WordAttribute.WHOLE_WORD_CONTENT, url.openStream());
               for (IConfigurationElement matchCriteriaEl : el.getChildren()) {
                  String match = matchCriteriaEl.getAttribute("match");
                  templateArtifact.addAttribute("Template Match Criteria", match);
               }
               templateArtifact.persistAttributes();
               templateFolder.addChild(templateArtifact);
            } else {
               OseeLog.log(SimpleTemplateProviderDbTask.class, Level.SEVERE, String.format("Problem loading file %s",
                     filePath));
            }
         }
      }
      templateFolder.persistAttributesAndRelations();
   }

   private Artifact getTemplateFolder() throws OseeCoreException {
      try {
         return ArtifactQuery.getArtifactFromTypeAndName("Folder", "Document Templates",
               BranchManager.getCommonBranch());
      } catch (ArtifactDoesNotExist ex) {
         Artifact rootArt =
               ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(BranchManager.getCommonBranch());

         Artifact templateFolder =
               ArtifactTypeManager.addArtifact("Folder", BranchManager.getCommonBranch(),
                     "Document Templates");
         rootArt.addChild(templateFolder);
         templateFolder.persistAttributesAndRelations();
         return templateFolder;
      }
   }
}
