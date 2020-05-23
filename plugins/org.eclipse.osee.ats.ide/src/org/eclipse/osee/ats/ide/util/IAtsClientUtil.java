/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.util;

/**
 * @author Donald G. Dunne
 */
public interface IAtsClientUtil {

   /**
    * The development of ATS requires quite a few Actions to be created. To facilitate this, this method will retrieve a
    * persistent number from the file-system so each action has a different name. By entering "tt" in the title, new
    * action wizard will be pre-populated with selections and the action name will be created as "tt <number in
    * atsNumFilename>". Get an incrementing number. This number only resets on new workspace creation. Should not be
    * used for anything but developmental purposes.
    */
   public int getAtsDeveloperIncrementingNum();

}
