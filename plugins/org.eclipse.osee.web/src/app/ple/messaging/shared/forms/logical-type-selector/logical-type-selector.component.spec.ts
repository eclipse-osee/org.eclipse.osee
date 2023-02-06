/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import { LogicalTypeSelectorComponent } from './logical-type-selector.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import {
	logicalTypeMock,
	typesServiceMock,
} from '@osee/messaging/shared/testing';
import { TypesService } from '@osee/messaging/shared/services';

describe('LogicalTypeSelectorComponent', () => {
	let component: LogicalTypeSelectorComponent;
	let fixture: ComponentFixture<LogicalTypeSelectorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [LogicalTypeSelectorComponent, NoopAnimationsModule],
		}).overrideComponent(LogicalTypeSelectorComponent, {
			set: {
				providers: [
					{ provide: TypesService, useValue: typesServiceMock },
				],
			},
		});
		await TestBed.configureTestingModule({
			imports: [LogicalTypeSelectorComponent],
		}).compileComponents();

		fixture = TestBed.createComponent(LogicalTypeSelectorComponent);
		component = fixture.componentInstance;
		component.type = logicalTypeMock[0];
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
