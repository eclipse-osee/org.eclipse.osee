/*
 * Created on Mar 22, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event.artifact.EventModType;
import org.eclipse.osee.framework.skynet.core.event.artifact.IArtifactListener;
import org.eclipse.osee.framework.skynet.core.event.artifact.IEventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.FilteredEventListener;

/**
 * @author Donald G. Dunne
 */
public class CoverageEventManager implements IArtifactListener {

   private static CoverageEventManager instance;
   private List<CoverageEditor> editors = new ArrayList<CoverageEditor>();
   private ArtifactTypeEventFilter artifactTypeEventFilter;
   private FilteredEventListener filteredEventListener;

   private CoverageEventManager() {
      artifactTypeEventFilter =
            new ArtifactTypeEventFilter(CoverageArtifactTypes.CoverageFolder, CoverageArtifactTypes.CoverageUnit,
                  CoverageArtifactTypes.CoveragePackage);
      filteredEventListener = new FilteredEventListener(this, artifactTypeEventFilter);
      OseeEventManager.addListener(filteredEventListener);
   }

   public static CoverageEventManager getInstance() {
      if (instance == null) {
         instance = new CoverageEventManager();
      }
      return instance;
   }

   public static void dispose() {
      if (instance != null) {
         OseeEventManager.removeListener(instance);
         instance.editors.clear();
         instance = null;
      }
   }

   public void register(CoverageEditor coverageEditor) throws OseeCoreException {
      editors.add(coverageEditor);
   }

   public void unregister(CoverageEditor coverageEditor) throws OseeCoreException {
      editors.remove(coverageEditor);
   }

   @Override
   public void handleArtifactModified(Collection<IEventBasicGuidArtifact> eventArtifacts, Sender sender) {
      for (CoverageEditor editor : editors) {
         try {
            for (IEventBasicGuidArtifact eventArt : eventArtifacts) {
               if (editor.getCoverageEditorInput().getCoveragePackageArtifact() == null) return;
               if (editor.getCoverageEditorInput().getCoveragePackageArtifact().getBranch().getGuid() != eventArt.getBranchGuid()) return;
               if (eventArt.getModType() == EventModType.Deleted || eventArt.getModType() == EventModType.ChangeType || eventArt.getModType() == EventModType.Purged) {
                  if (eventArt.getGuid().equals(editor.getCoverageEditorInput().getCoveragePackageArtifact().getGuid())) {
                     unregister(editor);
                     editor.closeEditor();
                  }
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE, ex);
         }
      }
   }

}
