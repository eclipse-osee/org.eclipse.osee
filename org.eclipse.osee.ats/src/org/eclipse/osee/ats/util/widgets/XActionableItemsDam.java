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
package org.eclipse.osee.ats.util.widgets;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;

/**
 * @author Donald G. Dunne
 */
public class XActionableItemsDam extends XTextDam {

   private WeakReference<Artifact> artifactRef;

   public XActionableItemsDam(Artifact artifact) {
      super(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName());
      this.artifactRef = new WeakReference<Artifact>(artifact);
   }

   public Artifact getArtifact() throws OseeStateException {
      if (artifactRef.get() == null) {
         throw new OseeStateException("Artifact has been garbage collected");
      }
      return artifactRef.get();
   }

   public Set<ActionableItemArtifact> getActionableItems() throws OseeCoreException {
      Set<ActionableItemArtifact> ais = new HashSet<ActionableItemArtifact>();
      for (String guid : getActionableItemGuids()) {
         try {
            ActionableItemArtifact aia = AtsCacheManager.getActionableItemByGuid(guid);
            if (aia == null)
               OseeLog.log(AtsPlugin.class, Level.SEVERE, "Can't find Actionable Item for guid " + guid);
            else
               ais.add(aia);
         } catch (OseeCoreException ex) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Error getting actionable item for guid " + guid, ex);
         }
      }
      return ais;
   }

   public String getActionableItemsStr() throws OseeCoreException {
      return Artifacts.toString("; ", getActionableItems());
   }

   public List<String> getActionableItemGuids() throws OseeCoreException {
      return getArtifact().getAttributesToStringList(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName());
   }

   public void addActionableItem(ActionableItemArtifact aia) throws OseeCoreException {
      if (!getActionableItemGuids().contains(aia.getGuid())) getArtifact().addAttribute(
            ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName(), aia.getGuid());
   }

   public void removeActionableItem(ActionableItemArtifact aia) throws OseeCoreException {
      getArtifact().deleteAttribute(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName(), aia.getGuid());
   }

   public Result setActionableItems(Collection<ActionableItemArtifact> newItems) throws OseeCoreException {
      Set<ActionableItemArtifact> existingAias = getActionableItems();

      // Remove non-selected items
      for (ActionableItemArtifact existingAia : existingAias)
         if (!newItems.contains(existingAia)) removeActionableItem(existingAia);

      // Add newly-selected items
      for (ActionableItemArtifact newItem : newItems)
         if (!existingAias.contains(newItem)) addActionableItem(newItem);

      return Result.TrueResult;
   }

}
