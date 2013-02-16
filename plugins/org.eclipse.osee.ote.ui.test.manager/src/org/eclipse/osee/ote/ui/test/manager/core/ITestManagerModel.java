/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.test.manager.core;

import org.eclipse.osee.framework.jdk.core.type.Pair;

public interface ITestManagerModel {

   public abstract boolean hasParseExceptions();

   public abstract Pair<Integer, Integer> getParseErrorRange();

   public abstract String getParseError();

   public abstract boolean setFromXml(String xmlText);

   public abstract String getRawXml();

}