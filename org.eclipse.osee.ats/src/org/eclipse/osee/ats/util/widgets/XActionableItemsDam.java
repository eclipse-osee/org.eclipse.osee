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
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;

/**
 * @author Donald G. Dunne
 */
public class XActionableItemsDam extends XTextDam {

   protected final Artifact sma;
   private static ArtifactPersistenceManager apm = ArtifactPersistenceManager.getInstance();

   public XActionableItemsDam(Artifact sma) {
      super(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName());
      this.sma = sma;
   }

   public DynamicAttributeManager getDam() throws SQLException {
      return sma.getAttributeManager(ATSAttributes.ACTIONABLE_ITEM_GUID_ATTRIBUTE.getStoreName());
   }

   public Set<ActionableItemArtifact> getActionableItems() throws SQLException {
      Set<ActionableItemArtifact> ais = new HashSet<ActionableItemArtifact>();
      for (Attribute attr : getDam().getAttributes()) {
         try {
            ais.add((ActionableItemArtifact) apm.getArtifact(attr.getStringData(),
                  BranchPersistenceManager.getInstance().getAtsBranch()));
         } catch (SQLException ex) {
            OSEELog.logException(AtsPlugin.class, ex, false);
         }
      }
      return ais;
   }

   public String getActionableItemsStr() {
      try {
         StringBuffer sb = new StringBuffer();
         for (ActionableItemArtifact aia : getActionableItems())
            sb.append(aia.getDescriptiveName() + ", ");
         return sb.toString().replaceFirst(", $", "");
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
         return ex.getLocalizedMessage();
      }
   }

   public Set<String> getActionableItemGuids() throws SQLException {
      Set<String> ais = new HashSet<String>();
      for (Attribute attr : getDam().getAttributes())
         ais.add(attr.getStringData());
      return ais;
   }

   public void addActionableItem(ActionableItemArtifact aia) throws SQLException {
      if (!getActionableItemGuids().contains(aia.getGuid())) getDam().getNewAttribute().setStringData(aia.getGuid());
   }

   public void removeActionableItem(ActionableItemArtifact aia) throws SQLException {
      for (Attribute attr : getDam().getAttributes()) {
         if (aia.getGuid().equals(attr.getStringData())) attr.delete();
      }
   }

   public Result setActionableItems(Collection<ActionableItemArtifact> newItems) throws SQLException {
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
