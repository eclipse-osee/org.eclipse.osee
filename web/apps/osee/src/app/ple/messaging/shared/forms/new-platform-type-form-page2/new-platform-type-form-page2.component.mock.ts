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
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import type { PlatformType, logicalType } from '@osee/messaging/shared/types';
import { NewPlatformTypeFormPage2Component } from './new-platform-type-form-page2.component';

@Component({
	selector: 'osee-new-platform-type-form-page2',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockNewPlatformTypeFormPage2Component
	implements Partial<NewPlatformTypeFormPage2Component>
{
	logicalType = input.required<logicalType>();
	platformType = model<PlatformType>(new PlatformTypeSentinel());
}
