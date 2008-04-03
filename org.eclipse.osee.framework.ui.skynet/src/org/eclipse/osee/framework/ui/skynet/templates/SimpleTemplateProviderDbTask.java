/*
 * Created on Apr 1, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
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

   private void processTemplatesForDBInit() throws SQLException, IllegalStateException, IOException {

      ArtifactSubtypeDescriptor templateDescriptor =
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor("Renderer Template");
      Artifact templateFolder = getTemplateFolder();
      IExtensionPoint ep =
            Platform.getExtensionRegistry().getExtensionPoint(
                  "org.eclipse.osee.framework.ui.skynet.SimpleTemplateProviderTemplate");
      for (IExtension extension : ep.getExtensions()) {
         for (IConfigurationElement el : extension.getConfigurationElements()) {
            Artifact templateArtifact =
                  templateDescriptor.makeNewArtifact(BranchPersistenceManager.getInstance().getCommonBranch());
            String filePath = el.getAttribute("File");
            String name = filePath.substring(filePath.lastIndexOf('/') + 1);
            name = name.substring(0, name.lastIndexOf('.'));
            URL url = Platform.getBundle(el.getContributor().getName()).getEntry(filePath);

            if (url != null) {
               templateArtifact.setSoleStringAttributeValue("Name", name);
               templateArtifact.setSoleAttributeFromStream("Word Formatted Content", url.openStream());
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
      templateFolder.persistAttributesAndLinks();
   }

   private Artifact getTemplateFolder() throws SQLException {
      Artifact templateFolder =
            ArtifactPersistenceManager.getInstance().getArtifactFromTypeName("Folder", "Document Templates",
                  BranchPersistenceManager.getInstance().getCommonBranch(), false);

      if (templateFolder == null) {

         Artifact rootArt =
               ArtifactPersistenceManager.getInstance().getDefaultHierarchyRootArtifact(
                     BranchPersistenceManager.getInstance().getCommonBranch(), true);

         ArtifactSubtypeDescriptor folderDescriptor =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor("Folder");
         templateFolder = folderDescriptor.makeNewArtifact(BranchPersistenceManager.getInstance().getCommonBranch());
         templateFolder.setDescriptiveName("Document Templates");
         rootArt.addChild(templateFolder);
         rootArt.persist(true);
      }
      return templateFolder;
   }
}
