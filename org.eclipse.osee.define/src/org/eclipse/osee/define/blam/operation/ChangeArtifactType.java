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
package org.eclipse.osee.define.blam.operation;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.swt.widgets.Display;

/**
 * Changes the descriptor type of an artifact to the provided descriptor.
 * 
 * @author Jeff C. Phillips
 */
public class ChangeArtifactType extends AbstractBlam {
   private List<Attribute<?>> attributesToPurge;
   private List<RelationLink> relationsToDelete;

   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      processChange(variableMap.getArtifacts("artifacts"),
            variableMap.getArtifactSubtypeDescriptor("New Artifact Type"));
   }

   /**
    * Changes the descriptor of the artifacts to the provided artifact descriptor
    * 
    * @param artifacts
    * @param descriptor
    * @throws SQLException
    */
   private void processChange(List<Artifact> artifacts, ArtifactType descriptor) throws Exception {
      if (artifacts.isEmpty()) {
         throw new IllegalArgumentException("The artifact list can not be empty");
      }

      for (Artifact artifact : artifacts) {
         processAttributes(artifact, descriptor);
         processRelations(artifact, descriptor);

         if (doesUserAcceptArtifactChange(artifact, descriptor)) {
            changeArtifactType(artifact, descriptor);
         }
      }

      // Kick Local and Remote Events
      OseeEventManager.kickArtifactsChangeTypeEvent(this, descriptor.getArtTypeId(), new LoadedArtifacts(artifacts));
   }

   /**
    * Splits the attributes of the current artifact into two groups. The attributes that are compatable for the new type
    * and the attributes that will need to be purged.
    * 
    * @param artifact
    * @param descriptor
    * @throws SQLException
    */
   private void processAttributes(Artifact artifact, ArtifactType descriptor) throws SQLException {
      attributesToPurge = new LinkedList<Attribute<?>>();

      Collection<AttributeType> attributeTypes =
            ConfigurationPersistenceManager.getAttributeTypesFromArtifactType(descriptor, artifact.getBranch());

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
    * @throws SQLException
    */
   private void processRelations(Artifact artifact, ArtifactType artifactType) throws SQLException {
      relationsToDelete = new LinkedList<RelationLink>();

      for (RelationLink link : artifact.getRelationsAll(false)) {
         if (RelationTypeManager.getRelationSideMax(link.getRelationType(), artifactType, link.getSide(artifact)) == 0) {
            relationsToDelete.add(link);
         }
      }
   }

   /**
    * @param artifact
    * @param descriptor
    * @return true if the user accepts the purging of the attributes and relations that are not compatible for the new
    *         artifact type else false.
    */
   private boolean doesUserAcceptArtifactChange(final Artifact artifact, final ArtifactType descriptor) {
      if (!relationsToDelete.isEmpty() || !attributesToPurge.isEmpty()) {
         ArtifactChangeMessageRunnable messageRunnable = new ArtifactChangeMessageRunnable(artifact, descriptor);
         Displays.ensureInDisplayThread(messageRunnable, true);
         return messageRunnable.isAccept();
      } else {
         return true;
      }
   }

   private class ArtifactChangeMessageRunnable implements Runnable {
      private boolean accept = false;
      private final Artifact artifact;
      private final ArtifactType descriptor;

      public ArtifactChangeMessageRunnable(Artifact artifact, ArtifactType descriptor) {
         this.artifact = artifact;
         this.descriptor = descriptor;
      }

      public void run() {
         accept =
               MessageDialog.openQuestion(
                     Display.getCurrent().getActiveShell(),
                     "Confirm Artifact Type Change ",
                     "There has been a conflict in changing " + artifact.getDescriptiveName() + " to " + descriptor.getName() + " type. \n" + "The following data will need to be purged " + (relationsToDelete.isEmpty() ? "" : Collections.toString(
                           relationsToDelete, ":", ",", null)) + (attributesToPurge.isEmpty() ? "" : Collections.toString(
                           attributesToPurge, ":", ",", null)));
      }

      /**
       * @return Returns the accept.
       */
      public boolean isAccept() {
         return accept;
      }
   };

   /**
    * Sets the artifact descriptor.
    * 
    * @param artifact
    * @param descriptor
    * @throws SQLException
    */
   private void changeArtifactType(Artifact artifact, ArtifactType descriptor) throws Exception {
      for (Attribute<?> attribute : attributesToPurge) {
         attribute.purge();
      }

      for (RelationLink relation : relationsToDelete) {
         relation.delete(true);
      }

      artifact.changeArtifactType(descriptor);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" /><XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"New Artifact Type\" /></xWidgets>";
   }
}