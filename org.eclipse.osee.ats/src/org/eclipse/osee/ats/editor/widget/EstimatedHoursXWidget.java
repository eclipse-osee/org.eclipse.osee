/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.widget;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XFloatDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class EstimatedHoursXWidget extends XFloatDam {

   public EstimatedHoursXWidget(SMAManager smaMgr, Composite composite, int horizontalSpan, XModifiedListener xModListener) {
      super(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getDisplayName());
      try {
         if (xModListener != null) {
            addXModifiedListener(xModListener);
         }
         setArtifact(smaMgr.getSma(), ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName());
         setFillHorizontally(true);
         createWidgets(composite, horizontalSpan);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
   }

}
