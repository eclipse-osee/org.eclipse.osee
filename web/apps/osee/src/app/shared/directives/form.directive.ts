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
import { Directive, Output, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormControlStatus, NgForm } from '@angular/forms';
import { debounceTime, map } from 'rxjs';

@Directive({
	selector: '[oseeForm]',
	standalone: true,
})
export class FormDirective<T> {
	constructor(private readonly ngForm: NgForm) {}

	@Output() public readonly formStatusChange =
		this.ngForm.form.statusChanges.pipe(
			debounceTime(0),
			map((value) => value as FormControlStatus)
		);

	// @todo - Switch to signals when Angular support template directive with signals
	// private readonly _formStatusChange = toSignal(this.formStatusChange, {
	// 	initialValue: 'INVALID',
	// });

	@Output() public readonly formValueChange =
		this.ngForm.form.valueChanges.pipe(debounceTime(0));

	// @todo - Switch to signals when Angular support template directive with signals
	// private readonly _formValueChange = toSignal(this.formValueChange, {
	// 	initialValue: 'INVALID',
	// });
}
