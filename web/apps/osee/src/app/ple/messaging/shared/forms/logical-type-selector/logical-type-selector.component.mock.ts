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

import { Component, model } from '@angular/core';
import type { logicalType } from '@osee/messaging/shared/types';
import { LogicalTypeSelectorComponent } from './logical-type-selector.component';

@Component({
	selector: 'osee-logical-type-dropdown',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockLogicalTypeSelectorComponent
	implements Partial<LogicalTypeSelectorComponent>
{
	type = model.required<logicalType>();
}
