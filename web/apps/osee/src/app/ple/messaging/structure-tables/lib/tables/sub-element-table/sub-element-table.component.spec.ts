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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { CommonModule } from '@angular/common';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { SimpleChange } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatTableDataSource, MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SubElementTableComponent } from './sub-element-table.component';
import { MockSubElementTableComponent } from '../../menus/testing/sub-element-table-dropdown.component.mock';
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { element } from '@osee/messaging/shared/types';
import { elementSearch1 } from '@osee/messaging/type-element-search/testing';

describe('SubElementTableComponent', () => {
	let component: SubElementTableComponent;
	let fixture: ComponentFixture<SubElementTableComponent>;
	let loader: HarnessLoader;
	let service: CurrentStructureService;
	let expectedData: element[] = elementSearch1.splice(1, 3);

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
				HttpClientTestingModule,
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
			],
		}).compileComponents();
		service = TestBed.inject(CurrentStructureService);
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(SubElementTableComponent);
		component = fixture.componentInstance;
		component.editMode = true;
		component.data = expectedData;
		component.dataSource = new MatTableDataSource(expectedData);
		component.dataSource.filter = 'name1';
		component.filter = 'element: name1';
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', async () => {
		fixture.detectChanges();
		await fixture.whenStable();
		expect(component).toBeTruthy();
		expect(component.data === expectedData).toBeTruthy();
		expect(component.filter === 'element: name1').toBeTruthy();
		expect(component.dataSource.filter === 'name1').toBeTruthy();
	});
	it('should update filter on changes', async () => {
		fixture.detectChanges();
		await fixture.whenStable();
		component.filter = 'element: name2';
		component.ngOnChanges({
			data: new SimpleChange(expectedData, expectedData, false),
			filter: new SimpleChange('element: name1', 'element: name2', false),
		});
		await fixture.whenStable();
		expect(component).toBeTruthy();
	});
});
