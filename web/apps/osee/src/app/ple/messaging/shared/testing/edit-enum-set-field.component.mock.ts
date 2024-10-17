/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import { Component, input, model, output } from '@angular/core';
import { EditEnumSetFieldComponent } from '@osee/messaging/shared/forms';
import type { bitSize, enumerationSet } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-edit-enum-set-field',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockEditEnumSetFieldComponent
	implements Partial<EditEnumSetFieldComponent>
{
	editable = input(false);

	bitSize = input.required<bitSize>();
	enumSet = model.required<enumerationSet>();

	unique = output<boolean>();
}
