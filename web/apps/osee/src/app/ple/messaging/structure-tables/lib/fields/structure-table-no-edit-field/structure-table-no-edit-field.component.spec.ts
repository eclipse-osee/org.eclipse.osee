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

import {
	CurrentStateServiceMock,
	messagesMock,
	structuresMock,
} from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { StructureTableLongTextFieldComponent } from '../structure-table-long-text-field/structure-table-long-text-field.component';
import { MockStructureTableLongTextFieldComponent } from '../structure-table-long-text-field/structure-table-long-text-field.component.mock';
import { StructureTableNoEditFieldComponent } from './structure-table-no-edit-field.component';

describe('StructureTableNoEditFieldComponent', () => {
	let component: StructureTableNoEditFieldComponent;
	let fixture: ComponentFixture<StructureTableNoEditFieldComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(StructureTableNoEditFieldComponent, {
			add: {
				imports: [MockStructureTableLongTextFieldComponent],
			},
			remove: {
				imports: [StructureTableLongTextFieldComponent],
			},
		})
			.configureTestingModule({
				imports: [StructureTableNoEditFieldComponent],
				providers: [
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(StructureTableNoEditFieldComponent);
		fixture.componentRef.setInput('header', 'name');
		fixture.componentRef.setInput('structure', structuresMock[0]);
		fixture.componentRef.setInput('message', messagesMock[0]);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
