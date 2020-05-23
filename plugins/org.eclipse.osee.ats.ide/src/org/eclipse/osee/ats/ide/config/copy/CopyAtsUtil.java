/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.ats.ide.config.copy;

/**
 * @author Donald G. Dunne
 */
public class CopyAtsUtil {

   public static String getConvertedName(ConfigData configData, String name) {
      return name.replaceFirst(configData.getSearchStr(), configData.getReplaceStr());
   }

}
