/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.health;

import org.eclipse.osee.ats.core.health.AbstractAtsHealthServiceImpl;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AtsHealthServiceImpl extends AbstractAtsHealthServiceImpl {

   @Override
   public XResultData validateAtsDatabase() {
      ValidateAtsDatabase valid = new ValidateAtsDatabase(null);
      XResultData results = valid.runIt(null, new XResultData());
      return results;
   }

}
