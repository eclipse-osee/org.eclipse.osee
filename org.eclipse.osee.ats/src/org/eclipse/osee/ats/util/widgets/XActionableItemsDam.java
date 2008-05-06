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
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
      for (Attribute<String> attr : getAttributes()) {
         ais.add((ActionableItemArtifact) ArtifactQuery.getArtifactFromId(attr.getValue(),
               BranchPersistenceManager.getAtsBranch()));
      }
      return ais;
   }

   public String getActionableItemsStr() {
      try {
         StringBuffer sb = new StringBuffer();
         for (ActionableItemArtifact aia : getActionableItems())
            sb.append(aia.getDescriptiveName() + ", ");
         return sb.toString().replaceFirst(", $", "");
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return ex.getLocalizedMessage();
      }
   }

   public Set<String> getActionableItemGuids() throws SQLException {
      Set<String> ais = new HashSet<String>();
      for (Attribute<String> attr : getAttributes()) {
         ais.add(attr.getValue());
      }
      return ais;
   }

   public void addActionableItem(ActionableItemArtifact aia) throws SQLException {
      if (!getActionableItemGuids().contains(aia.getGuid())) {
         Attribute<String> attribute = getDam().getNewAttribute();
         attribute.setValue(aia.getGuid());
      }
   }

   public void removeActionableItem(ActionableItemArtifact aia) throws SQLException {
      for (Attribute<String> attr : getAttributes()) {
         if (aia.getGuid().equals(attr.getValue())) attr.delete();
      }
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
