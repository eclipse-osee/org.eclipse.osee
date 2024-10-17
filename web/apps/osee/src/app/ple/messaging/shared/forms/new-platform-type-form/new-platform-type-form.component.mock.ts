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

import { Component, input, model } from '@angular/core';
import type { PlatformType, logicalType } from '@osee/messaging/shared/types';
import { NewPlatformTypeFormComponent } from './new-platform-type-form.component';

@Component({
	selector: 'osee-new-platform-type-form',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockNewPlatformTypeFormComponent
	implements Partial<NewPlatformTypeFormComponent>
{
	logicalType = input<logicalType>({
		id: '-1',
		name: '',
		idString: '',
		idIntValue: 0,
	});

	platformType = model.required<PlatformType>();
}
