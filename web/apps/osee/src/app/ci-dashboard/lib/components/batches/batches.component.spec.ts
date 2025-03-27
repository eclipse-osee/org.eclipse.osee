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
import BatchesComponent from './batches.component';
import { TmoHttpService } from '../../services/tmo-http.service';
import { tmoHttpServiceMock } from '../../services/tmo-http.service.mock';
import { CiDashboardControlsMockComponent } from '@osee/ci-dashboard/testing';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BatchDropdownMockComponent } from './batch-dropdown/batch-dropdown.component.mock';
import { RouterTestingModule } from '@angular/router/testing';
import { MatIconModule } from '@angular/material/icon';

describe('BatchesComponent', () => {
	let component: BatchesComponent;
	let fixture: ComponentFixture<BatchesComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(BatchesComponent, {
			set: {
				imports: [
					CommonModule,
					RouterTestingModule,
					CiDashboardControlsMockComponent,
					MatTableModule,
					MatTooltipModule,
					MatIconModule,
					BatchDropdownMockComponent,
				],
			},
		}).configureTestingModule({
			imports: [BatchesComponent],
			providers: [
				{ provide: TmoHttpService, useValue: tmoHttpServiceMock },
			],
		});
		fixture = TestBed.createComponent(BatchesComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
