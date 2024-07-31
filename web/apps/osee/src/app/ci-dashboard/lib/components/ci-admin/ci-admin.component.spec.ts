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
import CiAdminComponent from './ci-admin.component';
import { CiDashboardControlsMockComponent } from '@osee/ci-dashboard/testing';
import { MockSubsystemListComponent } from './subsystems-list/subsystems-list.component.mock';
import { MockTeamsListComponent } from './teams-list/teams-list.component.mock';

describe('CiAdminComponent', () => {
	let component: CiAdminComponent;
	let fixture: ComponentFixture<CiAdminComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(CiAdminComponent, {
			set: {
				imports: [
					CiDashboardControlsMockComponent,
					MockSubsystemListComponent,
					MockTeamsListComponent,
				],
			},
		})
			.configureTestingModule({
				imports: [CiAdminComponent],
			})
			.compileComponents();

		fixture = TestBed.createComponent(CiAdminComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
