/*********************************************************************
 * Copyright (c) 2024 Boeing
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
	selector: '[oseeHasValidName]',
	standalone: true,
	providers: [
		{
			provide: NG_VALIDATORS,
			useExisting: forwardRef(() => HasValidNameDirective),
			multi: true,
		},
	],
})
export class HasValidNameDirective implements Validator {
	validate(
		control: AbstractControl<{ name: string }, { name: string }>
	): ValidationErrors | null {
		return control.value !== undefined &&
			control.value !== null &&
			typeof control.value.name === 'string' &&
			control.value.name === ''
			? { invalidName: true }
			: null;
	}
}
