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

import { Component, Input } from '@angular/core';
import { AdvancedSearchCriteria } from '../../../../../types/artifact-search';

@Component({
	selector: 'osee-advanced-search-form',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class AdvancedSearchFormMockComponent {
	@Input() data!: AdvancedSearchCriteria;
}
