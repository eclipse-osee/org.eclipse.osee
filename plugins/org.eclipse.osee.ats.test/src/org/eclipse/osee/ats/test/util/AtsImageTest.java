/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.test.util;

import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.framework.ui.skynet.test.cases.ImageManagerTest;

/**
 * @author Donald G. Dunne
 */
public class AtsImageTest extends ImageManagerTest {

   /**
    * @param imageClassName
    * @param oseeImages
    */
   public AtsImageTest() {
      super("AtsImage", AtsImage.values());
   }

}
