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
package org.eclipse.osee.ats.ide.config.copy;

/**
 * @author Donald G. Dunne
 */
public class CopyAtsUtil {

   public static String getConvertedName(ConfigData configData, String name) {
      return name.replaceFirst(configData.getSearchStr(), configData.getReplaceStr());
   }

}
