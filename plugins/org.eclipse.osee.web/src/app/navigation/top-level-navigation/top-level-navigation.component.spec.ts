/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { LayoutModule } from '@angular/cdk/layout';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { userDataAccountServiceMock } from 'src/app/ple/plconfig/testing/mockUserDataAccountService';
import { UserDataAccountService } from 'src/app/userdata/services/user-data-account.service';
import { TopLevelNavigationComponent } from './top-level-navigation.component';

describe('TopLevelNavigationComponent', () => {
	let component: TopLevelNavigationComponent;
	let fixture: ComponentFixture<TopLevelNavigationComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatIconModule,
				MatListModule,
				MatToolbarModule,
				NoopAnimationsModule,
				LayoutModule,
				RouterTestingModule,
			],
			providers: [
				{
					provide: UserDataAccountService,
					useValue: userDataAccountServiceMock,
				},
			],
			declarations: [TopLevelNavigationComponent],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TopLevelNavigationComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should compile', () => {
		expect(component).toBeTruthy();
	});
});
