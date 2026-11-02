/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Given a parent artifact, open a filter dialog to select from the names of its children. The widget is
 * <b>String-based</b>: the selected/stored value is the child's <i>name</i>, not an artifact reference. This is
 * intentional so a previously-stored value still displays even if it is no longer one of the parent's children (the
 * selectable list can change over time). Example usage: a list of UserGroups (Navigation, Communications, etc.) whose
 * members are notified of a selection; user groups and their members can be dynamically changed. Subclasses needing the
 * resolved artifact (e.g. to notify members) can resolve a selected name via {@link #getSelectableArtifact(String)}.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXChildSelWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXChildSelWidget;

   private ArtifactToken parentArt = ArtifactToken.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXChildSelWidget() {
      this(ID, "", ArtifactToken.SENTINEL);
   }

   public XXChildSelWidget(WidgetId widgetId, String label, ArtifactToken parentArt) {
      // only one constructor should call super - others should chain via this()
      super(widgetId, label);
      this.parentArt = parentArt;
      set(XOption.VAL_SINGLE_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getSentinel() {
      return "";
   }

   // ------------------------------------------------------------------
   // Selectable from parent's children (by name)
   // ------------------------------------------------------------------
   @Override
   public Collection<String> getSelectable() {
      List<String> items = new ArrayList<>();
      for (Artifact child : getSelectableArtifacts()) {
         items.add(child.getName());
      }
      return items;
   }

   // ------------------------------------------------------------------
   // Persistence
   // ------------------------------------------------------------------
   @Override
   protected TransactionToken handleSelectedPersist() {
      if (!isAttributeStorage()) {
         return TransactionToken.SENTINEL;
      }
      Artifact artifact = getArtifact();
      SkynetTransaction tx = TransactionManager.createTransaction(artifact.getBranch(), "Set " + getLabel());
      if (isNotSelected()) {
         artifact.deleteAttributes(getAttributeType());
      } else if (isValueSingleSelectFromSelectable()) {
         artifact.setSoleAttributeFromString(getAttributeType(), getSelectedFirst());
      } else {
         artifact.setAttributeValues(getAttributeType(), new ArrayList<>(getSelected()));
      }
      tx.addArtifact(artifact);
      TransactionToken txToken = tx.execute();
      if (txToken != null && txToken.isValid()) {
         handleTransactionCompleted();
      }
      // Never return null; execute() returns null when nothing to persist
      return txToken == null ? TransactionToken.SENTINEL : txToken;
   }

   /** Override to filter which children are selectable. Default accepts all. */
   protected boolean isSelectable(Artifact art) {
      return true;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   /**
    * @return the parent's children that pass {@link #isSelectable(Artifact)}, as artifacts. Used to build the
    * selectable name list and to let subclasses resolve a selected name back to its artifact.
    */
   protected Collection<Artifact> getSelectableArtifacts() {
      if (parentArt == null || parentArt.isInvalid()) {
         return Collections.emptyList();
      }
      List<Artifact> items = new ArrayList<>();
      Artifact parent = ArtifactQuery.getArtifactFromId(parentArt.getId(),
         parentArt.getBranch().isValid() ? parentArt.getBranch() : COMMON);
      for (Artifact child : parent.getChildren()) {
         if (isSelectable(child)) {
            items.add(child);
         }
      }
      return items;
   }

   /**
    * Resolve a selected name back to its selectable child artifact.
    *
    * @return the matching child artifact, or null if no current child has that name
    */
   protected Artifact getSelectableArtifact(String name) {
      for (Artifact child : getSelectableArtifacts()) {
         if (child.getName().equals(name)) {
            return child;
         }
      }
      return null;
   }

   /** Override to perform actions after a successful persist (e.g., send notifications). */
   protected void handleTransactionCompleted() {
      // default: do nothing
   }

   public ArtifactToken getParentArt() {
      return parentArt;
   }

   public void setParentArt(ArtifactToken parentArt) {
      this.parentArt = parentArt;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   /**
    * Load whatever names are stored in the artifact attribute verbatim. Because the widget is String-based, a stored
    * value is preserved and displayed even if it is no longer one of the parent's children.
    */
   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         Collection<String> attributeValues = getArtifact().getAttributesToStringList(getAttributeType());
         setSelected(new ArrayList<>(attributeValues), false);
      }
   }

}
