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

import { StructureInterfaceComponent } from './structure-interface.component';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { MockMessagingControlsComponent } from '@osee/messaging/shared/main-content/testing';
import { MessagingControlsComponent } from '@osee/messaging/shared/main-content';
import { CurrentViewSelectorComponent } from '@osee/shared/components';
import { provideRouter } from '@angular/router';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { MockStructureTableComponent } from '@osee/messaging/structure-tables/testing';
import { StructureTableComponent } from '../tables/structure-table/structure-table.component';
import { MockCurrentViewSelectorComponent } from '@osee/shared/components/testing';

describe('StructureInterfaceComponent', () => {
	let component: StructureInterfaceComponent;
	let fixture: ComponentFixture<StructureInterfaceComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(StructureInterfaceComponent, {
			add: {
				imports: [
					MockMessagingControlsComponent,
					MockCurrentViewSelectorComponent,
					MockStructureTableComponent,
				],
			},
			remove: {
				imports: [
					MessagingControlsComponent,
					CurrentViewSelectorComponent,
					StructureTableComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [StructureInterfaceComponent],
				providers: [
					provideRouter([]),
					provideNoopAnimations(),
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(StructureInterfaceComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
