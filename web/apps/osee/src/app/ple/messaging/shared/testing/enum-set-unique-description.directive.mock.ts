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
import { Directive, Input } from '@angular/core';
import {
	AbstractControl,
	AsyncValidator,
	NG_ASYNC_VALIDATORS,
	ValidationErrors,
} from '@angular/forms';
import { Observable, of } from 'rxjs';
import { enumerationSet } from '../types/enum';

@Directive({
	selector: '[oseeEnumSetUniqueDescription]',
	standalone: true,
	providers: [
		{
			provide: NG_ASYNC_VALIDATORS,
			useExisting: MockEnumSetUniqueDescriptionDirective,
			multi: true,
		},
	],
})
export class MockEnumSetUniqueDescriptionDirective implements AsyncValidator {
	@Input('oseeEnumSetUniqueDescription')
	enumSet: enumerationSet = {
		name: {
			id: '-1',
			value: '',
			typeId: '1152921504606847088',
			gammaId: '-1',
		},
		applicability: {
			id: '1',
			name: 'Base',
		},
		description: {
			id: '-1',
			value: '',
			typeId: '1152921504606847090',
			gammaId: '-1',
		},
		enumerations: [],
		id: '-1',
		gammaId: '-1',
	};
	validate(
		control: AbstractControl<any, any>
	): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
		return of(null);
	}
}
