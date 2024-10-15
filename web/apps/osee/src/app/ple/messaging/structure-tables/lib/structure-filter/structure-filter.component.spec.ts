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

import { StructureFilterComponent } from './structure-filter.component';
import { MockMimHeaderComponent } from '@osee/messaging/shared/headers/testing';
import { MimHeaderComponent } from '@osee/messaging/shared/headers';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('StructureFilterComponent', () => {
	let component: StructureFilterComponent;
	let fixture: ComponentFixture<StructureFilterComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(StructureFilterComponent, {
			add: {
				imports: [MockMimHeaderComponent],
			},
			remove: {
				imports: [MimHeaderComponent],
			},
		})
			.configureTestingModule({
				imports: [StructureFilterComponent],
				providers: [provideNoopAnimations()],
			})
			.compileComponents();

		fixture = TestBed.createComponent(StructureFilterComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
