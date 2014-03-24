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

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.eclipse.osee.ote.core.testPoint.CheckGroup;
import org.eclipse.osee.ote.core.testPoint.RetryGroup;

/**
 * The TestPoint interface should be implemented by objects that store pass/fail
 * data.
 * 
 * @author Robert A. Fisher
 */
@JsonSubTypes({ @JsonSubTypes.Type(value = CheckGroup.class), //
		@JsonSubTypes.Type(value = RetryGroup.class) })
public interface ITestGroup extends ITestPoint {
	public int size();
	
	@JsonProperty
	public ArrayList<ITestPoint> getTestPoints();
}
