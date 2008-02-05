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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.TransactionArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationLinkBase;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.swt.widgets.Display;

/**
 * Changes the descriptor type of an artifact to the provided descriptor.
 * 
 * @author Jeff C. Phillips
 */
public class ChangeArtifactType implements BlamOperation {

   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ChangeArtifactType.class);
   private static final ConfigurationPersistenceManager configurationPersistenceManager =
         ConfigurationPersistenceManager.getInstance();
   private static final RelationPersistenceManager relationPersistenceManager =
         RelationPersistenceManager.getInstance();
   private List<Attribute> attributesToPurge;
   private List<RelationLinkBase> linksToPurge;

   @SuppressWarnings("unchecked")
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      processChange(variableMap.getArtifacts("artifact"), variableMap.getArtifactSubtypeDescriptor("descriptor"));
   }

   /**
    * Changes the descriptor of the artifacts to the provided artifact descriptor
    * 
    * @param artifacts
    * @param descriptor
    * @throws SQLException
    */
   private void processChange(List<Artifact> artifacts, ArtifactSubtypeDescriptor descriptor) throws SQLException {
      if (artifacts.isEmpty()) {
         throw new IllegalArgumentException("The artifact list can not be empty");
      }

      for (Artifact artifact : artifacts) {
         processAttributes(artifact, descriptor);
         processRelations(artifact, descriptor);

         if (doesUserAcceptArtifactChange(artifact, descriptor)) {
            changeArtifactType(artifact, descriptor);

            SkynetEventManager.getInstance().kick(new TransactionArtifactModifiedEvent(artifact, ModType.Changed, this));
         }
      }
   }

   /**
    * Splits the attributes of the current artifact into two groups. The attributes that are compatable for the new type
    * and the attributes that will need to be purged.
    * 
    * @param artifact
    * @param descriptor
    */
   private void processAttributes(Artifact artifact, ArtifactSubtypeDescriptor descriptor) {
      attributesToPurge = new LinkedList<Attribute>();

      try {
         Collection<DynamicAttributeDescriptor> descriptorAttrTypes =
               configurationPersistenceManager.getAttributeTypesFromArtifactType(descriptor);

         for (DynamicAttributeManager attributeManager : artifact.getAttributes()) {

            if (!descriptorAttrTypes.contains(attributeManager.getDescriptor())) {
               attributesToPurge.addAll(attributeManager.getAttributes());
            }
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }

   /**
    * Splits the relationLinks of the current artifact into Two groups. The links that are compatable for the new type
    * and the links that will need to be pruged.
    * 
    * @param artifact
    * @param descriptor
    * @throws SQLException
    */
   private void processRelations(Artifact artifact, ArtifactSubtypeDescriptor descriptor) throws SQLException {
      linksToPurge = new LinkedList<RelationLinkBase>();

      for (IRelationLink link : artifact.getLinkManager().getLinks()) {

         if (link instanceof RelationLinkBase) {
            RelationLinkBase linkBase = (RelationLinkBase) link;
            if (linkBase.getLinkDescriptor().getRestrictionSizeFor(descriptor.getArtTypeId(),
                  linkBase.getArtifactA().equals(artifact)) == 0) {
               linksToPurge.add(linkBase);
            }
         }

      }
   }

   /**
    * @param artifact
    * @param descriptor
    * @return true if the user acceptes the purging of the attributes and realtions that are not compatable for the new
    *         artifact type else false.
    */
   private boolean doesUserAcceptArtifactChange(final Artifact artifact, final ArtifactSubtypeDescriptor descriptor) {
      if (!linksToPurge.isEmpty() || !attributesToPurge.isEmpty()) {
         ArtifactChangeMessageRunnable messageRunnable = new ArtifactChangeMessageRunnable(artifact, descriptor);
         Displays.ensureInDisplayThread(messageRunnable, true);
         return messageRunnable.isAccept();
      } else {
         return true;
      }
   }

   private class ArtifactChangeMessageRunnable implements Runnable {
      private boolean accept = false;
      private Artifact artifact;
      private ArtifactSubtypeDescriptor descriptor;

      public ArtifactChangeMessageRunnable(Artifact artifact, ArtifactSubtypeDescriptor descriptor) {
         this.artifact = artifact;
         this.descriptor = descriptor;
      }

      public void run() {
         accept =
               MessageDialog.openQuestion(
                     Display.getCurrent().getActiveShell(),
                     "Confirm Artifact Type Change ",
                     "There has been a conflict in changing " + artifact.getDescriptiveName() + " to " + descriptor.getName() + " type. \n" + "The following data will need to be purged " + (linksToPurge.isEmpty() ? "" : Collections.toString(
                           linksToPurge, ":", ",", null)) + (attributesToPurge.isEmpty() ? "" : Collections.toString(
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
   private void changeArtifactType(Artifact artifact, ArtifactSubtypeDescriptor descriptor) throws SQLException {
      for (Attribute attribute : attributesToPurge) {
         if (attribute.getPersistenceMemo() != null) {
            attribute.purge();
         }
      }

      if (!linksToPurge.isEmpty()) {
         relationPersistenceManager.purgeRelationLinks(linksToPurge);
      }

      artifact.changeArtifactType(descriptor);
      artifact.persistAttributes();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifact\" /><XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"descriptor\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }
}