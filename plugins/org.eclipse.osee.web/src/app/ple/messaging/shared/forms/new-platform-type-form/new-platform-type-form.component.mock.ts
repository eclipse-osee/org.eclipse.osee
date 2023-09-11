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
import type { logicalType, PlatformType } from '@osee/messaging/shared/types';
import { Subject } from 'rxjs';
import { NewPlatformTypeFormComponent } from './new-platform-type-form.component';

@Component({
	selector: 'osee-new-platform-type-form',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockNewPlatformTypeFormComponent
	implements Partial<NewPlatformTypeFormComponent>
{
	@Input() logicalType: logicalType = {
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	};

	@Output() platformType = new Subject<PlatformType>();
}
