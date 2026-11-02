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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
// CodeReview.IgnoreXxWid
@Component(service = XWidget.class, immediate = true)
public class XXVersionListWidget extends XXAbstractBaseXWidget<IAtsVersion> {

   public static final WidgetId ID = WidgetIdAts.XXVersionListWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXVersionListWidget() {
      this(ID, "Versions");
   }

   public XXVersionListWidget(String label) {
      this(ID, label);
   }

   public XXVersionListWidget(WidgetId widgetId, String label) {
      super(widgetId, label, new AtsObjectLabelProvider());
      set(XOption.VAL_SINGLE_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public IAtsVersion getSentinel() {
      return null;
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

   /** Convenience method to set input from a typed version collection. */
   public void setInputVersions(Collection<? extends IAtsVersion> versions) {
      setSelectable(new ArrayList<>(versions));
   }

}
