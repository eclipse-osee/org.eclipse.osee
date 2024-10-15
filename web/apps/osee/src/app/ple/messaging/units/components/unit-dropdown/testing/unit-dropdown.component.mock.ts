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

import { Component, input, model } from '@angular/core';
import { ShowOnDirtyErrorStateMatcher } from '@angular/material/core';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import { UnitDropdownComponent } from '../unit-dropdown.component';

@Component({
	selector: 'osee-unit-dropdown',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockUnitDropdownComponent
	implements Partial<UnitDropdownComponent>
{
	required = input(false);
	disabled = input(false);

	hintHidden = input(false);
	unit = model<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEPLATFORMTYPEUNITS>
	>({ id: '-1', typeId: '4026643196432874344', gammaId: '-1', value: '' });

	errorMatcher = input(new ShowOnDirtyErrorStateMatcher());
}
