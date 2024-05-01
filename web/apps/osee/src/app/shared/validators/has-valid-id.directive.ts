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
import { Directive, forwardRef } from '@angular/core';
import {
	AbstractControl,
	NG_VALIDATORS,
	ValidationErrors,
	Validator,
} from '@angular/forms';

@Directive({
	selector: '[oseeHasValidId]',
	standalone: true,
	providers: [
		{
			provide: NG_VALIDATORS,
			useExisting: forwardRef(() => HasValidIdDirective),
			multi: true,
		},
	],
})
export class HasValidIdDirective implements Validator {
	constructor() {}
	validate(
		control: AbstractControl<
			{ id: string | number },
			{ id: string | number }
		>
	): ValidationErrors | null {
		return control.value !== undefined &&
			control.value !== null &&
			((typeof control.value.id === 'string' &&
				control.value.id === '-1') ||
				control.value.id === -1)
			? { invalidId: true }
			: null;
	}
}
