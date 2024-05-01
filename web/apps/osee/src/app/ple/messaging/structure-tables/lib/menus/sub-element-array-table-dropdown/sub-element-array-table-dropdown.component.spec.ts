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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { NgIf, AsyncPipe } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { RouterLink } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { SubElementArrayTableDropdownComponent } from './sub-element-array-table-dropdown.component';
import {
	CurrentStateServiceMock,
	elementsMock,
	elementTableDropdownServiceMock,
} from '@osee/messaging/shared/testing';
import { CurrentStructureService } from '@osee/messaging/shared/services';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { MatDividerModule } from '@angular/material/divider';
import { ElementTableDropdownService } from '../../services/element-table-dropdown.service';

describe('SubElementTableDropdownComponent', () => {
	let component: SubElementArrayTableDropdownComponent;
	let fixture: ComponentFixture<SubElementArrayTableDropdownComponent>;
	let loader: HarnessLoader;
	let service: CurrentStructureService;

	beforeEach(async () => {
		await TestBed.overrideComponent(SubElementArrayTableDropdownComponent, {
			set: {
				imports: [
					NgIf,
					AsyncPipe,
					RouterTestingModule,
					RouterLink,
					MatMenuModule,
					MatIconModule,
					MatDialogModule,
					MatFormFieldModule,
					MatDividerModule,
				],
				providers: [
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: CurrentStructureService,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: ElementTableDropdownService,
						useValue: elementTableDropdownServiceMock,
					},
				],
			},
		})
			.configureTestingModule({
				imports: [SubElementArrayTableDropdownComponent],
				providers: [
					{
						provide: STRUCTURE_SERVICE_TOKEN,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: CurrentStructureService,
						useValue: CurrentStateServiceMock,
					},
					{
						provide: ElementTableDropdownService,
						useValue: elementTableDropdownServiceMock,
					},
				],
			})
			.compileComponents();
		service = TestBed.inject(CurrentStructureService);

		fixture = TestBed.createComponent(
			SubElementArrayTableDropdownComponent
		);
		component = fixture.componentInstance;
		component.headerElement = elementsMock[0];
		component.element = elementsMock[0];
		fixture.detectChanges();
		loader = TestbedHarnessEnvironment.loader(fixture);
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
