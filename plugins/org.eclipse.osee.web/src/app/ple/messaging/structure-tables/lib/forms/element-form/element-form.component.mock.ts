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

import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { ElementDialog } from '@osee/messaging/shared/types';
import { Subject } from 'rxjs';

import { ElementFormComponent } from './element-form.component';

@Component({
	selector: 'osee-element-form',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockElementFormComponent implements Partial<ElementFormComponent> {
	@Input() data: ElementDialog = {
		id: '',
		name: '',
		element: {},
		type: new PlatformTypeSentinel(),
	};
	@Input() reset!: number | null;

	@Output() dataChange = new Subject<ElementDialog>();
}
