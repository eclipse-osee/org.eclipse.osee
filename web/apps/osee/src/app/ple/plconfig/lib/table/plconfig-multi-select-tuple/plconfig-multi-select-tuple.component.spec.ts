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

import { PlconfigMultiSelectTupleComponent } from './plconfig-multi-select-tuple.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { PlConfigCurrentBranchService } from 'src/app/ple/plconfig/lib/services/pl-config-current-branch.service';
import { plCurrentBranchServiceMock } from 'src/app/ple/plconfig/lib/testing/mockPlCurrentBranchService.mock';

describe('PlconfigMultiSelectTupleComponent', () => {
	let component: PlconfigMultiSelectTupleComponent;
	let fixture: ComponentFixture<PlconfigMultiSelectTupleComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [PlconfigMultiSelectTupleComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(PlconfigMultiSelectTupleComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('value', []);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
