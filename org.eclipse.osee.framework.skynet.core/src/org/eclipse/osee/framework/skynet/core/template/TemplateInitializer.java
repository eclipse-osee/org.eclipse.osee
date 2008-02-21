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
package org.eclipse.osee.framework.skynet.core.template;

import java.util.List;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;

/**
 * @author Donald G. Dunne
 */
public class TemplateInitializer {
   private static final String TEMPLATE_FOLDER_NAME = "Document Templates";
   private static final String EXTENSION_ID = "org.eclipse.osee.framework.skynet.core.TemplateProvider";
   private static final String EXTENSION_ELEMENT = "TemplateProvider";
   private static final String EXTENSION_CLASSNAME = "classname";

   public static void addFunctionality(Branch programBranch) throws Exception {
      Artifact programRoot =
            ArtifactPersistenceManager.getInstance().getDefaultHierarchyRootArtifact(programBranch, true);

      ArtifactSubtypeDescriptor descriptor =
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor("Folder");
      Artifact documentFolder = descriptor.makeNewArtifact(programBranch);
      documentFolder.setDescriptiveName(TEMPLATE_FOLDER_NAME);
      programRoot.addChild(documentFolder);

      createDocumentTemplates(documentFolder,
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor("Document"));

      programRoot.persist(true);
   }

   private static void createDocumentTemplates(Artifact parentFolder, ArtifactSubtypeDescriptor documentDescriptor) throws Exception {
      ExtensionDefinedObjects<ITemplateProvider> extensionDefinedObjects =
            new ExtensionDefinedObjects<ITemplateProvider>(EXTENSION_ID, EXTENSION_ELEMENT, EXTENSION_CLASSNAME);
      List<ITemplateProvider> providers = extensionDefinedObjects.getObjects();
      for (ITemplateProvider provider : providers) {
         provider.initializeTemplates(parentFolder, documentDescriptor);
      }
   }
}
