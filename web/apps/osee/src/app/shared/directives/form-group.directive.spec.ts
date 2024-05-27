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
import { NgForm, NgModelGroup } from '@angular/forms';
import { FormGroupDirective } from './form-group.directive';

describe('FormGroupDirective', () => {
	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: NgModelGroup,
					useValue: new NgModelGroup(
						new NgForm([], [], undefined),
						[],
						[]
					),
				},
			],
		});
	});
	it('should create an instance', () => {
		TestBed.runInInjectionContext(() => {
			const directive = new FormGroupDirective();

			expect(directive).toBeTruthy();
		});
	});
});
