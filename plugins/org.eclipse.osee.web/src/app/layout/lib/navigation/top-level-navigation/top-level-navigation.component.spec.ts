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
import { UserDataAccountService } from '@osee/auth';
import { userDataAccountServiceMock } from '@osee/auth/testing';
import { TestScheduler } from 'rxjs/testing';
import { navigationStructure } from './top-level-navigation-structure';

import { TopLevelNavigationComponent } from './top-level-navigation.component';

export const tests = (
	serviceMock: Partial<UserDataAccountService>,
	isAdminTest: boolean
) => {
	let component: TopLevelNavigationComponent;
	let fixture: ComponentFixture<TopLevelNavigationComponent>;
	let scheduler: TestScheduler;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatIconModule,
				MatListModule,
				MatToolbarModule,
				NoopAnimationsModule,
				LayoutModule,
				RouterTestingModule,
				TopLevelNavigationComponent,
			],
			providers: [
				{
					provide: UserDataAccountService,
					useValue: serviceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(TopLevelNavigationComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	it('should compile', () => {
		expect(component).toBeTruthy();
	});

	it('should get elements matching user permissions', () => {
		scheduler.run(() => {
			// Using the messaging configuration elements for this test
			const elements = navigationStructure[0].children.filter(
				(c) => c.label === 'Messaging Configuration'
			)[0].children;
			const elementsNoAdmin = elements.filter(
				(e) => e.requiredRoles.length == 0
			);
			scheduler
				.expectObservable(component.getElementsWithPermission(elements))
				.toBe('(a|)', { a: isAdminTest ? elements : elementsNoAdmin });
		});
	});
};

describe('TopLevelNavigationComponent', () => {
	tests(userDataAccountServiceMock, false);
});
