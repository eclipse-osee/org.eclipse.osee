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
import { MessageTypeDropdownComponent } from './message-type-dropdown.component';

@Component({
	selector: 'osee-message-type-dropdown',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockMessageTypeDropdownComponent
	implements Partial<MessageTypeDropdownComponent>
{
	required = input(false);
	disabled = input(false);

	hintHidden = input(false);
	value = model<
		attribute<
			string,
			| typeof ATTRIBUTETYPEIDENUM.INTERFACEMESSAGETYPE
			| typeof ATTRIBUTETYPEIDENUM.MESSAGEGENERATIONTYPE
		>
	>({ id: '-1', typeId: '2455059983007225770', gammaId: '-1', value: '' });
}
