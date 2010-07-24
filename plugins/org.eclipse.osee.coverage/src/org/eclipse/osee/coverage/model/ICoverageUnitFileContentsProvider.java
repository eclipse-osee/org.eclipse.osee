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
package org.eclipse.osee.coverage.model;

/**
 * Allow external mecahnism for coverage unit file contents to be provided to UI. This provides for late loading of
 * large amounts of data that isn't needed often.
 * 
 * @author Donald G. Dunne
 */
public interface ICoverageUnitFileContentsProvider {

   public String getFileContents(CoverageUnit coverageUnit);

   public void setFileContents(CoverageUnit coverageUnit, String fileContents);

}
