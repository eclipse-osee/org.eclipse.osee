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

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * Allow external mechnism for test units to be provided to UI
 * 
 * @author Donald G. Dunne
 */
public interface ITestUnitProvider {

   public Collection<String> getTestUnits(CoverageItem coverageItem) throws OseeCoreException;

   public void addTestUnit(CoverageItem coverageItem, String testUnitName) throws OseeCoreException;

   public void removeTestUnit(CoverageItem coverageItem, String testUnitName) throws OseeCoreException;

   public void setTestUnits(CoverageItem coverageItem, Collection<String> testUnitNames) throws OseeCoreException;

   public String toXml(CoverageItem coverageItem) throws OseeCoreException;

   public void fromXml(CoverageItem coverageItem, String xml) throws OseeCoreException;
}
