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

import { StructureTablePaginatorComponent } from './structure-table-paginator.component';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('StructureTablePaginatorComponent', () => {
	let component: StructureTablePaginatorComponent;
	let fixture: ComponentFixture<StructureTablePaginatorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [StructureTablePaginatorComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: STRUCTURE_SERVICE_TOKEN,
					useValue: CurrentStateServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(StructureTablePaginatorComponent);
		fixture.componentRef.setInput('structuresCount', 10);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
