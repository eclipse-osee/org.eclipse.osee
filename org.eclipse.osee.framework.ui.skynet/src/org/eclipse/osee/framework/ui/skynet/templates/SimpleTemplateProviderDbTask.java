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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;
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

   private void processTemplatesForDBInit() throws SQLException, IllegalStateException, IOException, MultipleAttributesExist {

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
               templateArtifact.setSoleXAttributeValue("Name", name);
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
      try {
         return ArtifactQuery.getArtifactFromTypeAndName("Folder", "Document Templates",
               BranchPersistenceManager.getCommonBranch());
      } catch (MultipleArtifactsExist ex) {
         OSEELog.logException(SimpleTemplateProviderDbTask.class, ex.getLocalizedMessage(), ex, false);
      } catch (ArtifactDoesNotExist ex) {
         Artifact rootArt =
               ArtifactPersistenceManager.getInstance().getDefaultHierarchyRootArtifact(
                     BranchPersistenceManager.getCommonBranch(), true);

         Artifact templateFolder =
               ArtifactTypeManager.addArtifact("Folder", BranchPersistenceManager.getCommonBranch(),
                     "Document Templates");
         rootArt.addChild(templateFolder);
         rootArt.persist(true);
         return templateFolder;
      }
      return null;
   }
}
