/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { provideNoopAnimations } from '@angular/platform-browser/animations';

import { MockPlatformTypeCardComponent } from '@osee/messaging/shared/testing';
import { BehaviorSubject, of } from 'rxjs';
import { PlMessagingTypesUIService } from '../services/pl-messaging-types-ui.service';
import { TypeGridComponent } from './type-grid.component';

describe('TypeGridComponent', () => {
	let component: TypeGridComponent;
	let fixture: ComponentFixture<TypeGridComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(TypeGridComponent, {
			set: {
				imports: [MockPlatformTypeCardComponent],
				providers: [
					{
						provide: PlMessagingTypesUIService,
						useValue: {
							filterString: '',
							columnCountNumber: 1,
							columnCount: new BehaviorSubject(1),
							singleLineAdjustment: of(0),
							BranchId: of('10'),
						},
					},
				],
			},
		})
			.configureTestingModule({
				imports: [TypeGridComponent],
				declarations: [],
				providers: [provideNoopAnimations()],
			})
			.compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TypeGridComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('platformTypes', []);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
