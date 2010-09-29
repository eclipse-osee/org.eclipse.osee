/*
 * Created on Sep 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.artifact.note;

import java.lang.ref.WeakReference;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;

public class ArtifactNote implements INoteStorageProvider {
   private final WeakReference<Artifact> artifactRef;

   public ArtifactNote(Artifact artifact) {
      this.artifactRef = new WeakReference<Artifact>(artifact);
   }

   @Override
   public String getNoteXml() {
      try {
         return getArtifact().getSoleAttributeValue(AtsAttributeTypes.StateNotes, "");
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "getLogXml exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public Result saveNoteXml(String xml) {
      try {
         getArtifact().setSoleAttributeValue(AtsAttributeTypes.StateNotes, xml);
         return Result.TrueResult;
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return new Result("saveLogXml exception " + ex.getLocalizedMessage());
      }
   }

   public Artifact getArtifact() throws OseeStateException {
      if (artifactRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return artifactRef.get();
   }

   @Override
   public String getNoteTitle() {
      try {
         return "History for \"" + getArtifact().getArtifactTypeName() + "\" - " + getArtifact().getHumanReadableId() + " - titled \"" + getArtifact().getName() + "\"";
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "getLogTitle exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public String getNoteId() {
      try {
         return getArtifact().getHumanReadableId();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "unknown";
   }

   @Override
   public boolean isNoteable() {
      try {
         return getArtifact().isAttributeTypeValid(AtsAttributeTypes.StateNotes);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return false;
   }

}
