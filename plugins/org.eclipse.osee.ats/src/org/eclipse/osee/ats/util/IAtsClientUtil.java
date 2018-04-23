/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util;

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
