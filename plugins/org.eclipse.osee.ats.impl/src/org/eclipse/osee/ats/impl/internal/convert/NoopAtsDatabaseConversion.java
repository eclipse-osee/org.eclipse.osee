/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.convert;

import org.eclipse.osee.ats.api.util.IAtsDatabaseConversion;
import org.eclipse.osee.framework.core.util.XResultData;

/**
 * @author Roberto E. Escobar
 */
public class NoopAtsDatabaseConversion implements IAtsDatabaseConversion {

   @Override
   public String getName() {
      return "No-Op Ats Database Conversion";
   }

   @Override
   public void run(XResultData data, boolean reportOnly) {
      data.log("Nothing to Do");
   }

   @Override
   public String getDescription() {
      return "This is an empty Ats Database Conversion task.";
   }

}
