/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public enum WidgetStatus {

   Valid,
   Empty,
   Invalid_Type, // string entered when should be int
   Invalid_Range, // entered 8 when should be >2 and <5 or entered 5 characters when min =8
   Invalid_Incompleted, // entered 2 integers when should be 3; have to enter hours for defect
   Exception;

   public boolean isValid() {
      return this == Valid;
   }

   public boolean isEmpty() {
      return this == Empty;
   }
}
