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

import { Component, computed, model } from '@angular/core';
import { TransportTypeDropdownComponent } from '@osee/messaging/transports/dropdown';
import { transportType } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-transport-type-dropdown',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockTransportTypeDropdownComponent
	implements Partial<TransportTypeDropdownComponent>
{
	transportType = model.required<transportType>();
	formId = computed(() => {
		return 'transport-type-1';
	});
}
