/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.orcs.ApplicationContext;

/**
 * @author Donald G. Dunne
 */
public class AtsUtilRest {

   // TODO use real application context
   public static ApplicationContext getApplicationContext() {
      return null;
   }

   public static IOseeBranch getAtsBranch() {
      return CoreBranches.COMMON;
   }

}