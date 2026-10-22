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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.util.AtsProgramLabelProvider;
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
public class XXProgramWidget extends XXAbstractBaseXWidget<IAtsProgram> {

   public static final WidgetId ID = WidgetIdAts.XXProgramWidget;

   protected Collection<IAtsProgram> atsPrograms;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXProgramWidget() {
      this(ID, "Program");
   }

   public XXProgramWidget(WidgetId id, String label) {
      super(id, label, new AtsProgramLabelProvider());
      set(XOption.VAL_SINGLE_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public IAtsProgram getSentinel() {
      return IAtsProgram.SENTINEL;
   }

   @Override
   public Collection<IAtsProgram> getSelectable() {
      if (atsPrograms == null) {
         boolean activeOnly = is(XOption.ACTIVE);
         atsPrograms = new ArrayList<>();
         for (IAtsProgram program : AtsApiService.get().getProgramService().getPrograms()) {
            if ((activeOnly && program.isActive()) || !activeOnly) {
               atsPrograms.add(program);
            }
         }
      }
      return atsPrograms;
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof IAtsProgram;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public ILabelProvider getLabelProvider() {
      return new AtsProgramLabelProvider();
   }

}
