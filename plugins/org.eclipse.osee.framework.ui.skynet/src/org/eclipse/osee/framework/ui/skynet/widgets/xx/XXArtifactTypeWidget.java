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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXArtifactTypeWidget extends XXAbstractBaseXWidget<ArtifactTypeToken> {

   public static final WidgetId ID = WidgetId.XXArtifactTypeWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXArtifactTypeWidget() {
      this(ID, "Artifact Type");
   }

   public XXArtifactTypeWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
      set(XOption.VAL_SINGLE_SEL);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<ArtifactTypeToken> getSelectable() {
      return OseeApiService.tokenSvc().getArtifactTypes();
   }

   @Override
   public ArtifactTypeToken getSentinel() {
      return ArtifactTypeToken.SENTINEL;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof ArtifactTypeToken;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

}
