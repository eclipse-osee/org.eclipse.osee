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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXArtifactListWidget extends XXAbstractBaseXWidget<ArtifactToken> {

   public static final WidgetId ID = WidgetId.XXArtifactListWidget;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXArtifactListWidget() {
      this(ID, "Artifacts");
   }

   public XXArtifactListWidget(String label) {
      this(ID, label);
   }

   public XXArtifactListWidget(WidgetId widgetId, String label) {
      super(widgetId, label, new ArtifactLabelProvider());
      set(XOption.VAL_MULTI_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public ArtifactToken getSentinel() {
      return ArtifactToken.SENTINEL;
   }

   /** Convenience method to get selected items cast to {@link Artifact}. */
   public Collection<Artifact> getSelectedArtifacts() {
      return Collections.castMatching(Artifact.class, getSelected());
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof ArtifactToken;
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

   public void setSelectableArts(Collection<Artifact> artifacts) {
      setSelectable(Collections.castAll(artifacts));
   }

}
