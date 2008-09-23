/**
 * 
 */
package org.eclipse.osee.framework.ui.skynet;

import java.sql.SQLException;
import java.util.Comparator;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;

/**
 * @author Ryan D. Brooks
 */
public class AlphabeticalRelationComparator implements Comparator<RelationLink> {
   private final RelationSide relationSide;

   /**
    * @param relationSide
    */
   public AlphabeticalRelationComparator(RelationSide relationSide) {
      super();
      this.relationSide = relationSide;
   }

   /* (non-Javadoc)
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(RelationLink relationLink1, RelationLink relationLink2) {
      try {
         return relationLink1.getArtifact(relationSide).compareTo(relationLink2.getArtifact(relationSide));
      } catch (ArtifactDoesNotExist ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         return 0;
      } catch (SQLException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         return 0;
      }
   }
}
