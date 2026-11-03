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

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXArtifactRefWidget extends XXAbstractBaseXWidget<ArtifactToken> {

   public static final WidgetId ID = WidgetId.XXArtifactRefWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXArtifactRefWidget() {
      this(ID, "Artifact Ref: ");
   }

   public XXArtifactRefWidget(String label) {
      this(ID, label);
   }

   public XXArtifactRefWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.READ);
      set(XOption.VAL_SINGLE_SEL);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public ArtifactToken getSentinel() {
      return ArtifactToken.SENTINEL;
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

}
