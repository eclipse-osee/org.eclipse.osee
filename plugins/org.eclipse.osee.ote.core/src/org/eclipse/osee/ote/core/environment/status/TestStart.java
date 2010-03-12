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
package org.eclipse.osee.ote.core.environment.status;

import java.io.Serializable;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class TestStart implements IServiceStatusData, Serializable {

	private static final long serialVersionUID = -3458459132672153638L;
	private final String testClassName;
	
	public TestStart(String testClassName){
		this.testClassName = testClassName;
	}
	
	public void accept(IServiceStatusDataVisitor visitor) {
		visitor.asTestStart(this);
	}

	public String getTestClassName(){
		return testClassName;
	}
}
