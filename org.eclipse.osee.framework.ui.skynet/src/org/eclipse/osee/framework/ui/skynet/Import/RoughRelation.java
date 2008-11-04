/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.Import;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Robert A. Fisher
 */
public class RoughRelation {
   private String relTypeName;
   private String aGuid;
   private String bGuid;
   private String rationale;
   private int aOrderValue;
   private int bOrderValue;

   public RoughRelation(String relTypeName, String aGuid, String bGuid, String rationale, int aOrderValue, int bOrderValue) {
      this.relTypeName = relTypeName;
      this.aGuid = aGuid;
      this.bGuid = bGuid;
      this.rationale = rationale;
      this.aOrderValue = aOrderValue;
      this.bOrderValue = bOrderValue;
   }

   public void makeReal(SkynetTransaction transaction, IProgressMonitor monitor) throws OseeCoreException {
      RelationType relationType = RelationTypeManager.getType(relTypeName);
      Artifact aArt = ArtifactQuery.getArtifactFromId(aGuid, transaction.getBranch());
      Artifact bArt = ArtifactQuery.getArtifactFromId(bGuid, transaction.getBranch());

      if (aArt == null || bArt == null) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING,
               "The relation of type " + relTypeName + " could not be created.");
         if (aArt == null) {
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "The artifact with guid: " + aGuid + " does not exist.");
         }
         if (bArt == null) {
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "The artifact with guid: " + bGuid + " does not exist.");
         }
      } else {
         try {
            monitor.subTask(aArt.getDescriptiveName() + " <--> " + bArt.getDescriptiveName());
            monitor.worked(1);
            RelationManager.addRelation(relationType, aArt, bArt, rationale);
            aArt.persistRelations(transaction);
         } catch (IllegalArgumentException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, ex.getLocalizedMessage());
         }
      }
   }

   /**
    * @return Returns the relTypeName.
    */
   public String getRelTypeName() {
      return relTypeName;
   }
}