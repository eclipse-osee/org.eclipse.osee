/*******************************************************************************
 * Copyright (c) 2023 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkWithFilteredDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkWfdForPrograms extends XHyperlinkWithFilteredDialog<IAtsProgram> {

   public XHyperlinkWfdForPrograms(ILabelProvider labelProvider) {
      super("Program(s)", labelProvider);
   }

   public XHyperlinkWfdForPrograms() {
      super("Program(s)");
   }

   @Override
   public Collection<IAtsProgram> getSelectable() {
      return Collections.castAll(AtsApiService.get().getConfigService().getConfigurations().getIdToProgram().values());
   }

}
