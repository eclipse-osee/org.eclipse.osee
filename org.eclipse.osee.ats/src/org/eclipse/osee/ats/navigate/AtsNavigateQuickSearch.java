/*
 * Created on Feb 5, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IATSArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Megumi Telles
 */
public class AtsNavigateQuickSearch extends WorldUISearchItem {
   private String searchStr;

   /**
    * @param name
    */
   public AtsNavigateQuickSearch(String name) {
      super(name);
   }

   public AtsNavigateQuickSearch(String name, String searchStr) {
      super(name);
      this.searchStr = searchStr;
   }

   /**
    * @param atsNavigateQuickSearch
    */
   public AtsNavigateQuickSearch(AtsNavigateQuickSearch atsNavigateQuickSearch) {
      super(atsNavigateQuickSearch);
      this.searchStr = atsNavigateQuickSearch.getSearchStr();
   }

   /**
    * @return
    */
   private String getSearchStr() {
      return this.searchStr;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldUISearchItem#performSearch(org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType)
    */
   @Override
   public Collection<Artifact> performSearch(SearchType searchType) throws OseeCoreException {
      try {
         List<Artifact> allArtifacts = new ArrayList<Artifact>();
         for (Artifact art : ArtifactQuery.getArtifactsFromAttributeWithKeywords(AtsPlugin.getAtsBranch(), searchStr,
               false, false)) {
            // only ATS Artifacts
            if (art instanceof IATSArtifact) {
               allArtifacts.add(art);
            }
         }
         return allArtifacts;
      } catch (Exception ex) {
         OseeLog.log("AtsNavigateQuickSearch.performSearch", Level.SEVERE, ex.getMessage(), ex);
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.search.WorldSearchItem#copy()
    */
   @Override
   public WorldSearchItem copy() {
      return new AtsNavigateQuickSearch(this);
   }

}
