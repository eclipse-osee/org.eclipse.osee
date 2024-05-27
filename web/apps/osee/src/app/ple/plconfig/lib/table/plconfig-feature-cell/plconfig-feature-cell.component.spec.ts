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

import { PlconfigFeatureCellComponent } from './plconfig-feature-cell.component';
import { DialogService } from '../../services/dialog.service';
import { DialogServiceMock } from '../../testing/mockDialogService.mock';

describe('PlconfigFeatureCellComponent', () => {
	let component: PlconfigFeatureCellComponent;
	let fixture: ComponentFixture<PlconfigFeatureCellComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [PlconfigFeatureCellComponent],
			providers: [
				{ provide: DialogService, useValue: DialogServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(PlconfigFeatureCellComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('feature', {
			id: '',
			name: '',
			configurationValues: [],
			attributes: [],
		});
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
