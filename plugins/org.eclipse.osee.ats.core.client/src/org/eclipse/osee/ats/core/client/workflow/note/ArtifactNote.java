/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.workflow.note;

import java.lang.ref.WeakReference;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
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
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "getLogXml exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public IStatus saveNoteXml(String xml) {
      try {
         getArtifact().setSoleAttributeValue(AtsAttributeTypes.StateNotes, xml);
         return Status.OK_STATUS;
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "saveLogXml exception " + ex.getLocalizedMessage());
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
         return "History for \"" + getArtifact().getArtifactTypeName() + "\" - " + getNoteId() + " - titled \"" + getArtifact().getName() + "\"";
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "getLogTitle exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public String getNoteId() {
      try {
         return AtsUtilCore.getAtsId(getArtifact());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "unknown";
   }

   @Override
   public boolean isNoteable() {
      try {
         return getArtifact().isAttributeTypeValid(AtsAttributeTypes.StateNotes);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return false;
   }

}
