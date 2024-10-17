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

import { Component, model, output } from '@angular/core';
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { NewTypeFormComponent } from '@osee/messaging/shared/forms';
import type { PlatformType } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-new-type-form',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockNewTypeFormComponent implements Partial<NewTypeFormComponent> {
	platformType = model<PlatformType>(new PlatformTypeSentinel());
	close = output<boolean>();

	result = output<PlatformType>();
}
