/*
 * Created on May 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeGroup;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilCore {
   public final static double DEFAULT_HOURS_PER_WORK_DAY = 8;
   private static OseeGroup atsAdminGroup = new OseeGroup("AtsAdmin");

   /**
    * TODO Remove duplicate Active flags, need to convert all ats.Active to Active in DB
    * 
    * @param artifacts to iterate through
    * @param active state to validate against; Both will return all artifacts matching type
    * @param clazz type of artifacts to consider; null for all
    * @return set of Artifacts of type clazz that match the given active state of the "Active" or "ats.Active" attribute
    * value. If no attribute exists, Active == true; If does exist then attribute value "yes" == true, "no" == false.
    */
   @SuppressWarnings("unchecked")
   public static <A extends Artifact> List<A> getActive(Collection<A> artifacts, Active active, Class<? extends Artifact> clazz) throws OseeCoreException {
      List<A> results = new ArrayList<A>();
      Collection<? extends Artifact> artsOfClass =
         clazz != null ? Collections.castMatching(clazz, artifacts) : artifacts;
      for (Artifact art : artsOfClass) {
         if (active == Active.Both) {
            results.add((A) art);
         } else {
            // assume active unless otherwise specified
            boolean attributeActive = ((A) art).getSoleAttributeValue(AtsAttributeTypes.Active, false);
            if (active == Active.Active && attributeActive) {
               results.add((A) art);
            } else if (active == Active.InActive && !attributeActive) {
               results.add((A) art);
            }
         }
      }
      return results;
   }

   public static boolean isAtsAdmin() {
      try {
         return getAtsAdminGroup().isCurrentUserMember();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return false;
      }
   }

   public static OseeGroup getAtsAdminGroup() {
      return atsAdminGroup;
   }

   public static String doubleToI18nString(double d) {
      return doubleToI18nString(d, false);
   }

   public static String doubleToI18nString(double d, boolean blankIfZero) {
      if (blankIfZero && d == 0) {
         return "";
      }
      // This enables java to use same string for all 0 cases instead of creating new one
      else if (d == 0) {
         return "0.00";
      } else {
         return String.format("%4.2f", d);
      }
   }

   public static Branch getAtsBranch() throws OseeCoreException {
      return BranchManager.getCommonBranch();
   }

   public static IOseeBranch getAtsBranchToken() {
      return CoreBranches.COMMON;
   }

   public static Artifact getFromToken(IArtifactToken token) {
      Artifact toReturn = null;
      try {
         toReturn = ArtifactQuery.getArtifactFromToken(token, getAtsBranchToken());
      } catch (OseeCoreException ex) {
         // Do Nothing;
      }
      return toReturn;
   }

}
