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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * Changes the descriptor type of an artifact to the provided descriptor.
 * 
 * @author Jeff C. Phillips
 */
public class ChangeArtifactType {
   private static List<Attribute<?>> attributesToPurge;
   private static List<RelationLink> relationsToDelete;
   private static final IStatus promptStatus = new Status(IStatus.WARNING, Activator.PLUGIN_ID, 256, "", null);

   /**
    * Changes the descriptor of the artifacts to the provided artifact descriptor
    * 
    * @param artifacts
    * @param artifactType
    */
   public static void changeArtifactType(Collection<Artifact> artifacts, ArtifactType artifactType) throws OseeCoreException {
      if (artifacts.isEmpty()) {
         throw new OseeArgumentException("The artifact list can not be empty");
      }

      for (Artifact artifact : artifacts) {
         processAttributes(artifact, artifactType);
         processRelations(artifact, artifactType);

         if (doesUserAcceptArtifactChange(artifact, artifactType)) {
            changeArtifactType(artifact, artifactType);
         }
      }

      // Kick Local and Remote Events
      OseeEventManager.kickArtifactsChangeTypeEvent(ChangeArtifactType.class, artifactType.getId(),
            new LoadedArtifacts(artifacts));
   }

   public static void changeArtifactTypeReportOnly(StringBuffer results, Collection<Artifact> artifacts, ArtifactType artifactType) throws OseeCoreException {
      if (artifacts.isEmpty()) {
         throw new OseeArgumentException("The artifact list can not be empty");
      }

      for (Artifact artifact : artifacts) {
         processAttributes(artifact, artifactType);
         processRelations(artifact, artifactType);

         if (!relationsToDelete.isEmpty() || !attributesToPurge.isEmpty()) {
            getConflictString(results, artifact, artifactType);
         }
      }
   }

   private static void getConflictString(StringBuffer results, Artifact artifact, ArtifactType artifactType) {
      results.append("There has been a conflict in changing artifact " + artifact.getGuid() + " - \"" + artifact.getName() + "\"" +
      //
      " to \"" + artifactType.getName() + "\" type. \n" + "The following data will need to be purged ");
      for (RelationLink relationLink : relationsToDelete) {
         results.append("([Relation][" + relationLink + "])");
      }
      for (Attribute<?> attribute : attributesToPurge) {
         results.append("([Attribute][" + attribute.getAttributeType().getName() + "][" + attribute.toString() + "])");
      }
      results.append("\n\n");
   }

   /**
    * Splits the attributes of the current artifact into two groups. The attributes that are compatable for the new type
    * and the attributes that will need to be purged.
    * 
    * @param artifact
    * @param descriptor
    */
   private static void processAttributes(Artifact artifact, ArtifactType artifactType) throws OseeCoreException {
      attributesToPurge = new LinkedList<Attribute<?>>();

      Collection<AttributeType> attributeTypes = artifactType.getAttributeTypes(artifact.getBranch());
      for (AttributeType attributeType : artifact.getAttributeTypes()) {
         if (!attributeTypes.contains(attributeType)) {
            attributesToPurge.addAll(artifact.getAttributes(attributeType.getName()));
         }
      }
   }

   /**
    * Splits the relationLinks of the current artifact into Two groups. The links that are compatable for the new type
    * and the links that will need to be pruged.
    * 
    * @param artifact
    * @param artifactType
    * @throws OseeCoreException
    */
   private static void processRelations(Artifact artifact, ArtifactType artifactType) throws OseeCoreException {
      relationsToDelete = new LinkedList<RelationLink>();

      for (RelationLink link : artifact.getRelationsAll(false)) {
         if (RelationTypeManager.getRelationSideMax(link.getRelationType(), artifactType, link.getSide(artifact)) == 0) {
            relationsToDelete.add(link);
         }
      }
   }

   /**
    * @param artifact
    * @param artifactType
    * @return true if the user accepts the purging of the attributes and relations that are not compatible for the new
    *         artifact type else false.
    */
   private static boolean doesUserAcceptArtifactChange(final Artifact artifact, final ArtifactType artifactType) {
      if (!relationsToDelete.isEmpty() || !attributesToPurge.isEmpty()) {

         StringBuffer sb = new StringBuffer(50);
         getConflictString(sb, artifact, artifactType);
         try {
            return (Boolean) DebugPlugin.getDefault().getStatusHandler(promptStatus).handleStatus(promptStatus,
                  sb.toString());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            return false;
         }
      } else {
         return true;
      }
   }

   /**
    * Sets the artifact descriptor.
    * 
    * @param artifact
    * @param artifactType
    * @throws OseeCoreException
    */
   private static void changeArtifactType(Artifact artifact, ArtifactType artifactType) throws OseeCoreException {
      for (Attribute<?> attribute : attributesToPurge) {
         attribute.purge();
      }

      for (RelationLink relation : relationsToDelete) {
         relation.delete(true);
      }

      artifact.changeArtifactType(artifactType);
   }

}