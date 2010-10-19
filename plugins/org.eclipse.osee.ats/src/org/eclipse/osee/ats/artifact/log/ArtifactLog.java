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
package org.eclipse.osee.ats.artifact.log;

import java.lang.ref.WeakReference;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;

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
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "getLogXml exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public Result saveLogXml(String xml) {
      try {
         getArtifact().setSoleAttributeValue(AtsAttributeTypes.Log, xml);
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
   public String getLogTitle() {
      try {
         return "History for \"" + getArtifact().getArtifactTypeName() + "\" - " + getArtifact().getHumanReadableId() + " - titled \"" + getArtifact().getName() + "\"";
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "getLogTitle exception " + ex.getLocalizedMessage();
      }
   }

   @Override
   public String getLogId() {
      try {
         return getArtifact().getHumanReadableId();
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "unknown";
   }

}
