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
import { Component, Input, Output } from '@angular/core';
import { TransportTypeFormComponent } from '@osee/messaging/shared/forms';
import {
	TransportType,
	transportTypeAttributes,
	TransportTypeForm,
} from '@osee/messaging/shared/types';
import { Subject } from 'rxjs';

@Component({
	selector: 'osee-transport-type-form',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockTransportTypeFormComponent
	implements Partial<TransportTypeFormComponent>
{
	@Input() transportType: TransportTypeForm = new TransportType();

	@Output() completion = new Subject<
		{ type: 'CANCEL' } | { type: 'SUBMIT'; data: transportTypeAttributes }
	>();
}
