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

import java.util.Collection;
import java.util.Collections;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultData.Type;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.NoOpViewerComparator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * EnumeratedArtifact Widget to handle a list of strings to choose from in a filtered dialog. List comes from an
 * enumerated artifact's IdValue attributes. OseeTypeEnum or EnumeratedArtifact token must be specified to use this
 * widget.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXStringsEnumArtWidget extends XXAbstractBaseXWidget<String> {

   public static final WidgetId ID = WidgetId.XXStringsEnumArtWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXStringsEnumArtWidget() {
      this(ID, "");
   }

   public XXStringsEnumArtWidget(String label) {
      this(ID, label);
   }

   public XXStringsEnumArtWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.VAL_SINGLE_SEL);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getSentinel() {
      return "";
   }

   @Override
   public Collection<String> getSelectable() {
      ArtifactToken enumArt = getEnumeratedArt();
      if (enumArt != null && enumArt.isValid()) {
         ArtifactToken art = ArtifactToken.SENTINEL;
         if (enumArt.getBranch().isValid()) {
            art = ArtifactQuery.getArtifactFromTokenOrSentinel(enumArt);
         } else {
            art = ArtifactQuery.getArtifactFromId(enumArt, CoreBranches.COMMON);
         }
         if (art.isValid()) {
            return ((Artifact) art).getAttributesToStringList(CoreAttributeTypes.IdValue);
         }
      }
      return Collections.emptyList();
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected XResultData validateHandleSelection() {
      ArtifactToken enumArt = checkEnumeratedArtifact();
      if (enumArt.isInvalid()) {
         new XResultData(Type.Severe, "Enumerated Artifact %s does not exist", getEnumeratedArt().toStringWithId());
      }
      Collection<String> selectable = getSelectable();
      if (selectable.isEmpty()) {
         new XResultData(Type.Severe, "No [%s] options configured for this workflow", getLabel());
      }
      return XResultData.OK_STATUS;
   }

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   protected ViewerComparator getComparator() {
      if (getAttributeType().isValid() && getAttributeType().hasDisplayHint(DisplayHint.InOrder)) {
         // Do not sort, use enum as supplied to dialogs
         return NoOpViewerComparator.instance;
      }
      return super.getComparator();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public ArtifactToken checkEnumeratedArtifact() {
      ArtifactToken enumArt = getEnumeratedArt();
      if (enumArt.isInvalid()) {
         return ArtifactToken.SENTINEL;
      }
      if (enumArt.getBranch().isValid()) {
         enumArt = ArtifactQuery.getArtifactFromTokenOrSentinel(enumArt);
      } else {
         enumArt = ArtifactQuery.getArtifactFromId(enumArt, CoreBranches.COMMON);
      }
      return enumArt;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         setSelected(getArtifact().getAttributesToStringList(getAttributeType()), false);
      }
   }

}
