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
import { Directive, Input, input } from '@angular/core';
import {
	AbstractControl,
	ValidationErrors,
	NG_VALIDATORS,
	Validator,
	ValidatorFn,
} from '@angular/forms';

@Directive({
	selector: '[oseeHasValidDateRange]',
	standalone: true,
	providers: [
		{
			provide: NG_VALIDATORS,
			useExisting: HasValidDateRangeDirective,
			multi: true,
		},
	],
})
export class HasValidDateRangeDirective implements Validator {
	minDate = input<string>('0000-00-00');
	maxDate = input<string>('0000-00-00');

	validate(control: AbstractControl): ValidationErrors | null {
		return dateRangeValidator(this.minDate(), this.maxDate())(control);
	}
}

export function dateRangeValidator(
	minDate: string,
	maxDate: string
): ValidatorFn {
	return (control: AbstractControl): ValidationErrors | null => {
		const value = control.value;
		if (!value) {
			return null;
		}
		const date = new Date(value);
		const minDateObj = new Date(minDate);
		const maxDateObj = new Date(maxDate);

		if (isNaN(date.getTime())) {
			return { dateRange: 'Invalid date.' };
		}

		if (date < minDateObj) {
			return { dateRange: 'Date cannot be in the past.' };
		}

		if (date > maxDateObj) {
			return { dateRange: 'Date cannot exceed one year.' };
		}
		return null;
	};
}
