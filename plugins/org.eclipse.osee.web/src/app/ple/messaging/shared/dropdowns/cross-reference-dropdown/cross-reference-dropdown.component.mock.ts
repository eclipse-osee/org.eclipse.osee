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
import {
	ErrorStateMatcher,
	ShowOnDirtyErrorStateMatcher,
} from '@angular/material/core';
import { Subject } from 'rxjs';
import { CrossReferenceDropdownComponent } from 'src/app/ple/messaging/shared/dropdowns/public-api';

@Component({
	selector: 'osee-cross-reference-dropdown',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockCrossReferenceDropdownComponent
	implements Partial<CrossReferenceDropdownComponent>
{
	@Input() id: string = '';
	@Input() name: string = '';
	@Input() required: boolean = false;

	@Input() disabled: boolean = false;

	@Input() hintHidden: boolean = false;
	@Input() crossRef: string = '';

	_crossRefChange = new Subject<string>();
	@Output() crossRefChange = new Subject<string>();

	@Input() errorMatcher: ErrorStateMatcher =
		new ShowOnDirtyErrorStateMatcher();

	@Input() allowOutsideValues: boolean = false;

	@Input() alternateObjectType: string = '';

	@Input() maximum: string = '';

	@Input() minimum: string = '';
}
