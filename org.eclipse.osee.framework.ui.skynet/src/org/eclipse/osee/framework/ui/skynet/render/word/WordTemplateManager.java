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
package org.eclipse.osee.framework.ui.skynet.render.word;

import java.io.IOException;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;

/**
 * @author Donald G. Dunne
 */
public class WordTemplateManager {

   public static void addWordTemplates(Branch programBranch) throws Exception {
      Artifact programRoot =
            ArtifactPersistenceManager.getInstance().getDefaultHierarchyRootArtifact(programBranch, true);

      ArtifactSubtypeDescriptor descriptor =
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor("Folder", programBranch);
      Artifact documentFolder = descriptor.makeNewArtifact();
      documentFolder.setDescriptiveName("Document Templates");
      programRoot.addChild(documentFolder);

      createDocumentTemplates(documentFolder,
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor("Document", programBranch));

      programRoot.persist(true);
   }

   private static void createDocumentTemplates(Artifact documentFolder, ArtifactSubtypeDescriptor documentDescriptor) throws SQLException, IOException {
      WordRenderer wordRenderer =
            (WordRenderer) RendererManager.getInstance().getRendererById("org.eclipse.osee.framework.ui.skynet.word");
      for (PresentationType presentationType : PresentationType.values()) {
         Artifact document = documentDescriptor.makeNewArtifact();
         documentFolder.addChild(document);
         wordRenderer.setDefaultTemplates(document, presentationType, documentFolder.getBranch());
      }
   }

}
