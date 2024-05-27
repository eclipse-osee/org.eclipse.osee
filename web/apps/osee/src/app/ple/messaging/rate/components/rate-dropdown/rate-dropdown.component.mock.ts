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
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';
import { RateDropdownComponent } from './rate-dropdown.component';

@Component({
	selector: 'osee-rate-dropdown',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockRateDropdownComponent
	implements Partial<RateDropdownComponent>
{
	required = input(false);
	disabled = input(false);

	hintHidden = input(false);
	value = model<
		attribute<string, typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGERATE>
	>({ id: '-1', typeId: '2455059983007225763', gammaId: '-1', value: '' });
}
