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
import SetDiffsComponent from './set-diffs.component';
import { CiDashboardControlsMockComponent } from '@osee/ci-dashboard/testing';
import { SetDropdownMultiMockComponent } from '../../testing/set-dropdown-multi.component.mock ';
import { CommonModule } from '@angular/common';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { SplitStringPipe } from '@osee/shared/utils';
import { TmoHttpService } from '../../services/tmo-http.service';
import { tmoHttpServiceMock } from '../../services/tmo-http.service.mock';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('SetDiffsComponent', () => {
	let component: SetDiffsComponent;
	let fixture: ComponentFixture<SetDiffsComponent>;

	beforeEach(() => {
		TestBed.overrideComponent(SetDiffsComponent, {
			set: {
				imports: [
					CommonModule,
					CiDashboardControlsMockComponent,
					SetDropdownMultiMockComponent,
					MatPaginatorModule,
					MatTableModule,
					MatTooltipModule,
					SplitStringPipe,
				],
			},
		}).configureTestingModule({
			imports: [SetDiffsComponent, NoopAnimationsModule],
			providers: [
				{
					provide: TmoHttpService,
					useValue: tmoHttpServiceMock,
				},
			],
		});
		fixture = TestBed.createComponent(SetDiffsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
