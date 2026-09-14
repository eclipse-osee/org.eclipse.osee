/*********************************************************************
 * Copyright (c) 2026 Boeing
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

import { AbstractControl, NgForm, FormGroupDirective } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';

/**
 * Reports the invalid (red) state as soon as a control is invalid, without
 * waiting for the user to touch, dirty, or submit the field. Use this when a
 * form should surface its required fields immediately on render (e.g. a
 * create dialog) so the user can see which fields are blocking submission.
 */
export class ImmediateErrorStateMatcher implements ErrorStateMatcher {
	isErrorState(
		control: AbstractControl<unknown, unknown> | null,
		_form: NgForm | FormGroupDirective | null
	): boolean {
		return !!(control && control.invalid);
	}
}
