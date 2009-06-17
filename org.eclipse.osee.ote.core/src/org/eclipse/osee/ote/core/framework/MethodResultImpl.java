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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.logging.IHealthStatus;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class MethodResultImpl implements IMethodResult {

	public static final MethodResultImpl OK = new MethodResultImpl();
	
	public static MethodResultImpl combine(IMethodResult result1, IMethodResult result2){
		if(result1== OK && result2 == OK){
			return OK;
		} else {
			return new MethodResultImpl(result1, result2);
		}
	}
	
	private List<IHealthStatus> statuses;
	private ReturnCode returnCode;
	
	public MethodResultImpl(){
		statuses = new ArrayList<IHealthStatus>();
		returnCode = ReturnCode.OK;
	}
	
	private MethodResultImpl(IMethodResult result1, IMethodResult result2){
		if(result1 != OK){
			addStatus(result1.getStatus());
			setReturnCode(result1.getReturnCode());
		}
		if(result2 != OK){
			addStatus(result2.getStatus());
			setReturnCode(result2.getReturnCode());
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.core.framework.IMethodResult#getReturnCode()
	 */
	public ReturnCode getReturnCode() {
		return returnCode;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.core.framework.IMethodResult#getStatus()
	 */
	public List<IHealthStatus> getStatus() {
		return statuses;
	}
	
	public void setReturnCode(ReturnCode returnCode){
		this.returnCode = returnCode;
	}
	
	public void addStatus(IHealthStatus status){
		statuses.add(status);
	}

	/**
	 * @param status
	 */
	public void addStatus(List<IHealthStatus> status) {
		statuses.addAll(status);
	}

	public String toString(){
	   StringBuilder sb = new StringBuilder();
	   sb.append(returnCode.toString());
	   sb.append("\n");
	   for(IHealthStatus status:statuses){
	      sb.append(status.toString());
	      sb.append("\n");
	   }
	   return sb.toString();
	}
}
