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
import { of } from 'rxjs';
import { logicalType } from '../types/logicaltype';
import { NewPlatformTypeFormPage2Component } from '../forms/new-platform-type-form-page2/new-platform-type-form-page2.component';

@Component({
	selector: 'osee-new-platform-type-form-page2',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockNewPlatformTypeFormPage2Component
	implements Partial<NewPlatformTypeFormPage2Component>
{
	@Input() logicalType: logicalType = {
		id: '-1',
		name: '',
		idString: '-1',
		idIntValue: -1,
	};
	@Output() attributes = of({
		platformType: {},
		createEnum: false,
		enumSetId: '',
		enumSetName: '',
		enumSetDescription: '',
		enumSetApplicability: { id: '1', name: 'Base' },
		enums: [],
	});
}
