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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConnectionValidationResultsComponent } from './connection-validation-results.component';

describe('ConnectionValidationResultsComponent', () => {
	let component: ConnectionValidationResultsComponent;
	let fixture: ComponentFixture<ConnectionValidationResultsComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [ConnectionValidationResultsComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(ConnectionValidationResultsComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('label', 'Results');
		fixture.componentRef.setInput('results', { '123': 'Error 1' });
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
