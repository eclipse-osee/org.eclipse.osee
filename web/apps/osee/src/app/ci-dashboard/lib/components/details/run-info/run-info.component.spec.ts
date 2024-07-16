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

import { RunInfoComponent } from './run-info.component';
import { CommonModule } from '@angular/common';
import { CiDetailsService } from '../../../services/ci-details.service';
import { ciDetailsServiceMock } from '../../../testing/ci-details.service.mock';

describe('RunInfoComponent', () => {
	let component: RunInfoComponent;
	let fixture: ComponentFixture<RunInfoComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [CommonModule, RunInfoComponent],
			providers: [
				{ provide: CiDetailsService, useValue: ciDetailsServiceMock },
			],
		});
		fixture = TestBed.createComponent(RunInfoComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
