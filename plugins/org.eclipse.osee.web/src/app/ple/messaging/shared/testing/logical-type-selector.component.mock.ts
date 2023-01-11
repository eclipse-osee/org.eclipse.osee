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

import { Component, Input, Output, EventEmitter } from '@angular/core';
import { logicalType } from '../types/logicaltype';
import { LogicalTypeSelectorComponent } from '../forms/logical-type-selector/logical-type-selector.component';

@Component({
	selector: 'osee-logical-type-selector',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockLogicalTypeSelectorComponent
	implements Partial<LogicalTypeSelectorComponent>
{
	@Input() type: logicalType = {
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	};
	@Output() typeChanged = new EventEmitter<logicalType>();
}
