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

import java.io.InputStream;

/**
 * @author Jeff C. Phillips
 */
public interface IMediaResolver {

   public byte[] getValue();

   public boolean setValue(InputStream stream);

   public void setBlobData(InputStream stream);

   public byte[] getBlobData();

   public void setVarchar(String varchar);

   public String getvarchar();

}
