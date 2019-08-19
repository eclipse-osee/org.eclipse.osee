/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.column;

import org.eclipse.osee.ats.core.column.AtsColumnToken;
import org.eclipse.osee.ats.ide.util.xviewer.column.XViewerAtsColumnIdColumn;

/**
 * @author Jeremy A. Midvidy
 */
public class CancelledReasonDetailsColumnUI extends XViewerAtsColumnIdColumn {

   public static CancelledReasonDetailsColumnUI instance = new CancelledReasonDetailsColumnUI();

   public static CancelledReasonDetailsColumnUI getInstance() {
      return instance;
   }

   public CancelledReasonDetailsColumnUI() {
      super(AtsColumnToken.CancelledReasonDetails);
   }

}
