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
package org.eclipse.osee.ats.core.client.workflow.log;

import java.lang.ref.WeakReference;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.log.ILogStorageProvider;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ArtifactLog implements ILogStorageProvider {
   private final WeakReference<Artifact> artifactRef;

   public ArtifactLog(Artifact artifact) {
      this.artifactRef = new WeakReference<Artifact>(artifact);
   }

   @Override
   public String getLogXml() {
      try {
         return getArtifact().getSoleAttributeValue(AtsAttributeTypes.Log, "");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "getLogXml exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public IStatus saveLogXml(String xml) {
      try {
         getArtifact().setSoleAttributeValue(AtsAttributeTypes.Log, xml);
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
   public String getLogTitle() {
      try {
         return "History for \"" + getArtifact().getArtifactTypeName() + "\" - " + getArtifact().getHumanReadableId() + " - titled \"" + getArtifact().getName() + "\"";
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return "getLogTitle exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public String getLogId() {
      try {
         return getArtifact().getHumanReadableId();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return "unknown";
   }

}
