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

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;

/**
 * @author Donald G. Dunne
 */
public class XActionableItemsDam extends XTextDam {

   protected final Artifact sma;

   public XActionableItemsDam(Artifact sma) {
      super(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName());
      this.sma = sma;
   }

   private DynamicAttributeManager getDam() throws SQLException {
      return sma.getAttributeManager(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName());
   }

   private Collection<Attribute<String>> getAttributes() throws SQLException {
      return this.sma.getAttributes(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName());
   }

   public Set<ActionableItemArtifact> getActionableItems() throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
      Set<ActionableItemArtifact> ais = new HashSet<ActionableItemArtifact>();
      for (String guid : getActionableItemGuids()) {
         ais.add((ActionableItemArtifact) ArtifactQuery.getArtifactFromId(guid, BranchPersistenceManager.getAtsBranch()));
      }
      return ais;
   }

   public String getActionableItemsStr() throws Exception {
      return Artifacts.commaArts(getActionableItems());
   }

   public Set<String> getActionableItemGuids() throws SQLException {
      return sma.getAttributesToStringCollection(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName());
   }

   public void addActionableItem(ActionableItemArtifact aia) throws SQLException {
      if (!getActionableItemGuids().contains(aia.getGuid())) sma.addAttribute(
            ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName(), aia.getGuid());
   }

   public void removeActionableItem(ActionableItemArtifact aia) throws SQLException {
      sma.deleteAttribute(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName(), aia.getGuid());
   }

   public Result setActionableItems(Collection<ActionableItemArtifact> newItems) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist {
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
