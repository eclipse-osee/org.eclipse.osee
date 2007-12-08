/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.File;

/**
 * @author Jeff C. Phillips
 */
public class FileWordAttribute extends WordAttribute {

   private static FileMediaResolver resolver = new FileMediaResolver();

   public FileWordAttribute(File file) {
      super(resolver);
   }

   public void setFileLocation(String location) {
      resolver.setFileLocation(location);
   }

}
