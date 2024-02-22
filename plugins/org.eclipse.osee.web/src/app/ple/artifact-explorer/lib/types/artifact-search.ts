/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
import { NamedId } from '@osee/shared/types';

export type AdvancedSearchCriteria = {
	artifactTypes: NamedId[];
	attributeTypes: NamedId[];
	exactMatch: boolean;
	searchById: boolean;
};

export const defaultAdvancedSearchCriteria: AdvancedSearchCriteria = {
	artifactTypes: [],
	attributeTypes: [],
	exactMatch: false,
	searchById: false,
};
