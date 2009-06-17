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
package org.eclipse.osee.ote.core.framework;

import org.eclipse.osee.framework.logging.OseeLog;


/**
 * @author Andrew M. Finkbeiner
 *
 */
public class ResultBuilder {
	
	private MethodResultImpl result;
	private boolean logToHM;
	
	public ResultBuilder(boolean logToHM){
		result = new MethodResultImpl();
		this.logToHM = logToHM;
	}
	
	public MethodResultImpl append(IMethodResult result){
		if(logToHM){
			OseeLog.reportStatus(result.getStatus());
		}
		this.result.addStatus(result.getStatus());
		if(result.getReturnCode() != ReturnCode.OK){
			this.result.setReturnCode(result.getReturnCode());
		}
		return this.result;
	}
	
	public IMethodResult get(){
		return this.result;
	}
	
	public boolean isReturnStatusOK(){
		return this.result.getReturnCode() == ReturnCode.OK;		
	}
	
	public String toString(){
	   return result.toString();
	}
}
