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

import { StructureTableToolbarAddActionsComponent } from './structure-table-toolbar-add-actions.component';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';

describe('StructureTableToolbarAddActionsComponent', () => {
	let component: StructureTableToolbarAddActionsComponent;
	let fixture: ComponentFixture<StructureTableToolbarAddActionsComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [StructureTableToolbarAddActionsComponent],
			providers: [
				{
					provide: STRUCTURE_SERVICE_TOKEN,
					useValue: CurrentStateServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(
			StructureTableToolbarAddActionsComponent
		);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
