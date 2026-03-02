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
package org.eclipse.osee.ats.ide.blam;

import org.eclipse.osee.ats.ide.workdef.XWidgetBuilderAts;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsBlam extends AbstractBlam {

   protected XWidgetBuilderAts wba;
   public AbstractAtsBlam() {
      this(null, DEFAULT_DESCRIPTION, BlamUiSource.DEFAULT);
   }

   public AbstractAtsBlam(String name, String usageDescription, BlamUiSource source) {
      super(name, usageDescription, source);
   }

   @Override
   protected XWidgetBuilderAts createWidgetBuilder() {
      if (wb == null) {
         wba = new XWidgetBuilderAts();
         wb = wba;
      }
      return wba;
   }

}
