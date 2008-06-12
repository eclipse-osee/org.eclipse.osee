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
import java.sql.Connection;
import java.sql.SQLException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

public class SimpleTemplateProviderDbTask implements IDbInitializationTask {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#canRun()
    */
   public boolean canRun() {
      return true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask#run(java.sql.Connection)
    */
   public void run(Connection connection) throws Exception {
      processTemplatesForDBInit();
   }

   private void processTemplatesForDBInit() throws SQLException, IllegalStateException, IOException, OseeCoreException {

      Artifact templateFolder = getTemplateFolder();
      IExtensionPoint ep =
            Platform.getExtensionRegistry().getExtensionPoint(
                  "org.eclipse.osee.framework.ui.skynet.SimpleTemplateProviderTemplate");
      for (IExtension extension : ep.getExtensions()) {
         for (IConfigurationElement el : extension.getConfigurationElements()) {
            Artifact templateArtifact =
                  ArtifactTypeManager.addArtifact("Renderer Template", BranchPersistenceManager.getCommonBranch());
            String filePath = el.getAttribute("File");
            String name = filePath.substring(filePath.lastIndexOf('/') + 1);
            name = name.substring(0, name.lastIndexOf('.'));
            URL url = Platform.getBundle(el.getContributor().getName()).getEntry(filePath);

            if (url != null) {
               templateArtifact.setSoleAttributeValue("Name", name);
               templateArtifact.setSoleAttributeFromStream(AttributeTypeManager.getTypeWithWordContentCheck(
                     templateArtifact, WordAttribute.CONTENT_NAME).getName(), url.openStream());
               for (IConfigurationElement matchCriteriaEl : el.getChildren()) {
                  String match = matchCriteriaEl.getAttribute("match");
                  templateArtifact.addAttribute("Template Match Criteria", match);
               }
               templateArtifact.persistAttributes();
               templateFolder.addChild(templateArtifact);
            } else {
               OSEELog.logSevere(SimpleTemplateProviderDbTask.class,
                     String.format("Problem loading file %s", filePath), false);
            }
         }
      }
      templateFolder.persistAttributesAndRelations();
   }

   private Artifact getTemplateFolder() throws SQLException {
      try {
         try {
            return ArtifactQuery.getArtifactFromTypeAndName("Folder", "Document Templates",
                  BranchPersistenceManager.getCommonBranch());
         } catch (ArtifactDoesNotExist ex) {
            Artifact rootArt =
                  ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(BranchPersistenceManager.getCommonBranch());

            Artifact templateFolder =
                  ArtifactTypeManager.addArtifact("Folder", BranchPersistenceManager.getCommonBranch(),
                        "Document Templates");
            rootArt.addChild(templateFolder);
            templateFolder.persistAttributesAndRelations();
            return templateFolder;
         }
      } catch (Exception ex) {
         OSEELog.logException(SimpleTemplateProviderDbTask.class, ex.getLocalizedMessage(), ex, false);
      }
      return null;
   }
}
