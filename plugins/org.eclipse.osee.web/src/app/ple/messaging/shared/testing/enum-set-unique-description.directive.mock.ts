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
		name: '',
		applicability: {
			id: '1',
			name: 'Base',
		},
		description: '',
	};
	validate(
		control: AbstractControl<any, any>
	): Promise<ValidationErrors | null> | Observable<ValidationErrors | null> {
		return of(null);
	}
}
