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

import { PlconfigCellComponent } from './plconfig-cell.component';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { PlConfigCurrentBranchService } from '../../services/pl-config-current-branch.service';
import { plCurrentBranchServiceMock } from '../../testing/mockPlCurrentBranchService.mock';

describe('PlconfigCellComponent', () => {
	let component: PlconfigCellComponent;
	let fixture: ComponentFixture<PlconfigCellComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [PlconfigCellComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: PlConfigCurrentBranchService,
					useValue: plCurrentBranchServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(PlconfigCellComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('configId', '');
		fixture.componentRef.setInput('feature', {
			id: '',
			name: '',
			configurationValues: [],
			attributes: [],
		});
		fixture.componentRef.setInput('allowEdits', false);
		fixture.componentRef.setInput('editMode', false);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
