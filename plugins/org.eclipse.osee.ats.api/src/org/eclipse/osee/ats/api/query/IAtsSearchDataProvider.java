/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.query;

import java.util.List;

/**
 * @author Donald G. Dunne
 */
public interface IAtsSearchDataProvider {

   AtsSearchData fromJson(String namespace, String json);

   AtsSearchData createSearchData(String namespace, String searchName);

   List<String> getSupportedNamespaces();

}
