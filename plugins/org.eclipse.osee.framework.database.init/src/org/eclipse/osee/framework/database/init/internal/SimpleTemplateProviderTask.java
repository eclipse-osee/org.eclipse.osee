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

package org.eclipse.osee.framework.database.init.internal;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

public class SimpleTemplateProviderTask implements IDbInitializationTask {

   @Override
   public void run() throws OseeCoreException {
      try {
         processTemplatesForDBInit();
      } catch (IOException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   private void processTemplatesForDBInit() throws IOException, OseeCoreException {

      Artifact templateFolder = getTemplateFolder();
      IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(
         "org.eclipse.osee.framework.ui.skynet.SimpleTemplateProviderTemplate");
      for (IExtension extension : ep.getExtensions()) {
         for (IConfigurationElement el : extension.getConfigurationElements()) {
            Artifact templateArtifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.RendererTemplate, COMMON);
            String filePath = el.getAttribute("File");
            String name = filePath.substring(filePath.lastIndexOf('/') + 1);
            name = name.substring(0, name.lastIndexOf('.'));
            URL url = Platform.getBundle(el.getContributor().getName()).getEntry(filePath);

            if (url != null) {
               templateArtifact.setName(name);
               templateArtifact.setSoleAttributeFromStream(CoreAttributeTypes.WholeWordContent, url.openStream());
               for (IConfigurationElement matchCriteriaEl : el.getChildren()) {
                  String match = matchCriteriaEl.getAttribute("match");
                  templateArtifact.addAttribute(CoreAttributeTypes.TemplateMatchCriteria, match);
               }
               templateArtifact.persist(getClass().getSimpleName());
               templateFolder.addChild(templateArtifact);
            } else {
               OseeLog.logf(SimpleTemplateProviderTask.class, Level.SEVERE, "Problem loading file %s", filePath);
            }
         }
      }
      templateFolder.persist(getClass().getSimpleName());
   }

   private Artifact getTemplateFolder() throws OseeCoreException {
      Artifact templateFolder =
         ArtifactQuery.checkArtifactFromTypeAndName(CoreArtifactTypes.HeadingMSWord, "Document Templates", COMMON);
      if (templateFolder == null) {
         Artifact rootArt = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(COMMON);

         templateFolder = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, COMMON, "Document Templates");
         rootArt.addChild(templateFolder);
         templateFolder.persist(getClass().getSimpleName());
      }
      return templateFolder;
   }
}
