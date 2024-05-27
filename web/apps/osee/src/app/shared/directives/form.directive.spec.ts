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
import { TestBed } from '@angular/core/testing';
import { NgForm } from '@angular/forms';
import { FormDirective } from './form.directive';
import { of } from 'rxjs';

describe('FormDirective', () => {
	let ngFormMock: NgForm;

	beforeEach(() => {
		ngFormMock = {
			form: {
				statusChanges: of('VALID'),
				valueChanges: of('12345'),
			},
		} as unknown as NgForm;

		spyOn(ngFormMock.form.statusChanges, 'pipe').and.callThrough();
		spyOn(ngFormMock.form.valueChanges, 'pipe').and.callThrough();

		TestBed.configureTestingModule({
			providers: [{ provide: NgForm, useValue: ngFormMock }],
		});
	});

	it('should create an instance', () => {
		TestBed.runInInjectionContext(() => {
			const directive = new FormDirective();
			expect(directive).toBeTruthy();
		});
	});
});
