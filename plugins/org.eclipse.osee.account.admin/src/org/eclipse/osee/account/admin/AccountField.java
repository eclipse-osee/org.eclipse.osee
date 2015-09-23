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
package org.eclipse.osee.account.admin;

/**
 * Enumeration type order is important. It is used to prioritize formats when guessing a field type based on format.
 * 
 * @author Roberto E. Escobar
 */
public enum AccountField {
   LOCAL_ID,
   GUID,
   EMAIL,
   DISPLAY_NAME,
   USERNAME,
   SUBSCRIPTION_GROUP_NAME,
   UNKNOWN;
}
