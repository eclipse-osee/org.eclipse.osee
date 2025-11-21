/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { Directive, input } from '@angular/core';
import {
	AbstractControl,
	NG_VALIDATORS,
	ValidationErrors,
	Validator,
} from '@angular/forms';

@Directive({
	selector: '[oseeSentinelForInvalid]',
	standalone: true,
	providers: [
		{
			provide: NG_VALIDATORS,
			useExisting: SentinelValidationDirective,
			multi: true,
		},
	],
})
export class SentinelValidationDirective implements Validator {
	oseeSentinelForInvalid = input.required<string>();

	validate(control: AbstractControl): ValidationErrors | null {
		const val: unknown = control.value;
		const sentinel = this.oseeSentinelForInvalid();

		if (val == null) {
			// Let 'required' handle empty values
			return null;
		}

		if (this.isPrimitiveSentinel(val, sentinel)) {
			return { sentinelDisallowed: true };
		}

		if (this.isObjectWithIdSentinel(val, sentinel)) {
			return { sentinelDisallowed: true };
		}

		return null;
	}

	private isPrimitiveSentinel(val: unknown, sentinel: string): val is string {
		return typeof val === 'string' && val === sentinel;
	}

	private isObjectWithIdSentinel(val: unknown, sentinel: string): boolean {
		if (typeof val !== 'object' || val === null) return false;
		const rec = val as Record<string, unknown>;
		const id = rec['id'];
		return typeof id === 'string' && id === sentinel;
	}
}
