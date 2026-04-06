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
import { CommonModule } from '@angular/common';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubElementArrayTableComponent } from './sub-element-array-table.component';
import { MockSubElementTableComponent } from '../../menus/testing/sub-element-table-dropdown.component.mock';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { elementSearch4 } from '@osee/messaging/type-element-search/testing';
import {
	provideHttpClient,
	withInterceptorsFromDi,
} from '@angular/common/http';

describe('SubElementArrayTableComponent', () => {
	let component: SubElementArrayTableComponent;
	let fixture: ComponentFixture<SubElementArrayTableComponent>;
	const expectedData = elementSearch4[0];

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				CommonModule,
				MatIconModule,
				MatDialogModule,
				MatTableModule,
				MatTooltipModule,
				MatMenuModule,
				MatFormFieldModule,
				MatInputModule,
				FormsModule,
				NoopAnimationsModule,
				RouterTestingModule,
				MockSubElementTableComponent,
			],
			providers: [
				{
					provide: ActivatedRoute,
					useValue: {
						paramMap: of(
							convertToParamMap({
								branchId: '10',
								branchType: 'working',
							})
						),
						fragment: of(null),
					},
				},
				{
					provide: STRUCTURE_SERVICE_TOKEN,
					useValue: CurrentStateServiceMock,
				},
				{
					provide: CurrentStructureService,
					useValue: CurrentStateServiceMock,
				},
				provideHttpClient(withInterceptorsFromDi()),
				provideHttpClientTesting(),
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SubElementArrayTableComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('editMode', true);
		fixture.componentRef.setInput('element', expectedData);
		fixture.componentRef.setInput('tableFieldsEditMode', true);
		fixture.detectChanges();
	});

	it('should create', async () => {
		fixture.detectChanges();
		await fixture.whenStable();
		expect(component).toBeTruthy();
		expect(component.element() === expectedData).toBeTruthy();
	});

	/**
	 * Note these tests are disabled. If someone gets the time to re-implement them in
	 * @see {SubElementTableComponent} 's tests, feel free. :)
	 * Pretty painful to do the DI for the MatDialogRef, since standalone components have a different dependency injection style
	 */
	// xdescribe('Menu Testing', () => {
	// 	let mEvent: MouseEvent;
	// 	beforeEach(() => {
	// 		mEvent = document.createEvent('MouseEvent');
	// 	});
});
