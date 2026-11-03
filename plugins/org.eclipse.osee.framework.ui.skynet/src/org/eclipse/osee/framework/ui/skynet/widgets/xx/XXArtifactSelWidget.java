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

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXArtifactSelWidget extends XXAbstractBaseXWidget<ArtifactToken> {

   public static final WidgetId ID = WidgetId.XXArtifactSelWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXArtifactSelWidget() {
      this(ID, "Artifact");
   }

   public XXArtifactSelWidget(String label) {
      this(ID, label);
   }

   public XXArtifactSelWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.VAL_SINGLE_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public ArtifactToken getSentinel() {
      return ArtifactToken.SENTINEL;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof ArtifactId;
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

   public void setBranch(BranchToken branch) {
      widData.setBranch(branch);
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   protected void initializeSelected() {
      if (xxArt.isArtifact() && getAttributeType().isValid()) {
         try {
            // ArtifactId attribute stores an ArtifactId, not a String; read it as a string to get the id
            String value = getArtifact().getSoleAttributeValueAsString(getAttributeType(), "");
            if (Strings.isNumeric(value)) {
               ArtifactId artId = ArtifactId.valueOf(value);
               if (artId.isValid()) {
                  BranchToken branch = widData.getBranch().isValid() ? widData.getBranch() : //
                     widData.isCommon() ? CoreBranches.COMMON : getArtifact().getBranch();
                  Artifact art = ArtifactQuery.getArtifactOrNull(artId, branch, DeletionFlag.EXCLUDE_DELETED);
                  if (art != null) {
                     setSelected(art.getToken(), false);
                  }
               }
            }
         } catch (Exception ex) {
            // Skip if value can't be resolved
         }
      }
   }

}
