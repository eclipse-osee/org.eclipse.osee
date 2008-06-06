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

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;

/**
 * @author Robert A. Fisher
 */
public class RoughRelation {
   private static Logger logger = ConfigUtil.getConfigFactory().getLogger(RoughRelation.class);

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

   public void makeReal(Branch branch, IProgressMonitor monitor) throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      RelationType relationType = RelationTypeManager.getType(relTypeName);
      Artifact aArt = ArtifactQuery.getArtifactFromId(aGuid, branch);
      Artifact bArt = ArtifactQuery.getArtifactFromId(bGuid, branch);

      if (aArt == null || bArt == null) {
         logger.log(Level.WARNING, "The relation of type " + relTypeName + " could not be created.");
         if (aArt == null) {
            logger.log(Level.WARNING, "The artifact with guid: " + aGuid + " does not exist.");
         }
         if (bArt == null) {
            logger.log(Level.WARNING, "The artifact with guid: " + bGuid + " does not exist.");
         }
      } else {
         try {
            monitor.subTask(aArt.getDescriptiveName() + " <--> " + bArt.getDescriptiveName());
            monitor.worked(1);
            RelationManager.addRelation(relationType, aArt, bArt, rationale);
            aArt.persistRelations();
         } catch (IllegalArgumentException ex) {
            logger.log(Level.WARNING, ex.toString());
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