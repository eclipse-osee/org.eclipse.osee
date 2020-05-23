/*********************************************************************
 * Copyright (c) 2013 Boeing
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
