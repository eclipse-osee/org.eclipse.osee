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
package org.eclipse.osee.ote.core.environment.interfaces;

import java.util.List;


/**
 * @author Robert A. Fisher
 */
public interface ITestStation {
   
   public String getOutletIp();
   public void setOutletIp(String outletIp);
   public int getOutletPort();
   public void setOutletPort(int outletPort);
   public String getVmeConnectionName();
   public void turnPowerSupplyOnOff(boolean turnOn);
   public List<IOTypeHandlerDefinition> getSupportedDriverTypes();
   public boolean isPhysicalTypeAvailable(IOTypeDefinition physicalType);
}
