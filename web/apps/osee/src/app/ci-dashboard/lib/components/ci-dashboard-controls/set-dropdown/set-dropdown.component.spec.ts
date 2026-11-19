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
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { MatFormField } from '@angular/material/form-field';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { BehaviorSubject, Subject, defer } from 'rxjs';
import { CiDashboardUiService } from '../../../services/ci-dashboard-ui.service';
import { CiSetRoutedUiService } from '../../../services/ci-set-routed-ui.service';
import { CiSetsService } from '../../../services/ci-sets.service';
import { setsMock } from '../../../testing/tmo.response.mock';
import { CISetSentinel, type CISet } from '../../../types/tmo';
import { SetDropdownComponent } from './set-dropdown.component';
describe('SetDropdownComponent', () => {
	let component: SetDropdownComponent;
	let fixture: ComponentFixture<SetDropdownComponent>;
	let ciSetId: BehaviorSubject<`${number}`>;
	let urlSetId: ReturnType<typeof signal<`${number}` | undefined>>;
	let listSubject: BehaviorSubject<CISet[]>;
	let listSubscriptions: number;
	let byIdRequests: (string | undefined)[];
	let byIdValue: ReturnType<typeof signal<CISet | undefined>>;
	let byIdLoading: ReturnType<typeof signal<boolean>>;
	let byIdError: ReturnType<typeof signal<unknown>>;
	let activeOnlyValues: boolean[];
	let routedSetIds: `${number}`[];
	const secondSet: CISet = {
		...setsMock[0],
		id: '6666',
		name: {
			...setsMock[0].name,
			value: 'Qualification',
		},
	};
	const inactiveSet: CISet = {
		...secondSet,
		id: '7777',
		name: { ...secondSet.name, value: 'Retired Set' },
		active: { ...secondSet.active, value: false },
	};
	const create = (routeSet: `${number}` | '-1', settled = true) => {
		ciSetId = new BehaviorSubject<`${number}`>(routeSet);
		urlSetId = signal<`${number}` | undefined>(
			settled ? routeSet : undefined
		);
		listSubject = new BehaviorSubject<CISet[]>([setsMock[0], secondSet]);
		listSubscriptions = 0;
		byIdRequests = [];
		byIdValue = signal<CISet | undefined>(undefined);
		byIdLoading = signal(false);
		byIdError = signal<unknown>(undefined);
		activeOnlyValues = [];
		routedSetIds = [];
		TestBed.resetTestingModule();
		TestBed.configureTestingModule({
			imports: [SetDropdownComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CiSetRoutedUiService,
					useValue: {
						urlSetId,
						setCISetAndNavigate: (setId: `${number}`) =>
							routedSetIds.push(setId),
					},
				},
				{
					provide: CiDashboardUiService,
					useValue: { ciSetId },
				},
				{
					provide: CiSetsService,
					useValue: {
						// Counts subscriptions: the list is only fetched while subscribed.
						ciSets: defer(() => {
							listSubscriptions++;
							return listSubject;
						}),
						getCiSetResource: (id: () => string | undefined) => {
							byIdRequests.push(id());
							return {
								hasValue: () => byIdValue() !== undefined,
								value: byIdValue,
								isLoading: byIdLoading,
								error: byIdError,
							};
						},
						activeOnly: new BehaviorSubject(true),
						set ActiveOnly(value: boolean) {
							activeOnlyValues.push(value);
						},
					},
				},
			],
		});
		fixture = TestBed.createComponent(SetDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
		fixture.detectChanges();
	};
	const input = (): HTMLInputElement =>
		fixture.nativeElement.querySelector('input[name="autocomplete-text"]');
	beforeEach(() => {
		create('-1');
	});
	it('should create', () => {
		expect(component).toBeTruthy();
	});
	it('should use an outlined set selector', () => {
		const formField = fixture.debugElement.query(By.directive(MatFormField))
			.componentInstance as MatFormField;
		expect(formField.appearance).toBe('outline');
	});
	it('should filter sets case-insensitively in one computed list', () => {
		component.filterText.set('QUAL');
		expect(component.sets()).toEqual([secondSet]);
	});
	it('should label the active filter as applying to the set list', () => {
		expect(fixture.nativeElement.textContent).toContain('Active sets only');
	});
	it('should keep an emptied filter empty instead of restoring the selected name', () => {
		component.applyFilter({ target: { value: '' } } as unknown as Event);
		expect(component.inputText()).toBe('');
		expect(component.sets().length).toBe(2);
	});
	it('should clear the selection from the keyboard', () => {
		const clear: HTMLButtonElement = fixture.nativeElement.querySelector(
			'button[aria-label="Clear CI Set selection"]'
		);
		routedSetIds.length = 0;
		clear.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter' }));
		expect(routedSetIds).toEqual(['-1']);
	});
	it('should preserve the active-only service setting', () => {
		component.setActiveOnly({ checked: false } as MatCheckboxChange);
		expect(activeOnlyValues).toEqual([false]);
	});
	it('should default to the first set and route explicit selections', () => {
		expect(component.selectedSet()).toEqual(setsMock[0]);
		expect(routedSetIds).toEqual([setsMock[0].id]);
		routedSetIds.length = 0;
		component.selectSet(secondSet);
		expect(routedSetIds).toEqual([secondSet.id]);
	});
	it('should clear the route without immediately selecting the first set again', () => {
		const preventDefault = vi.fn();
		const stopPropagation = vi.fn();
		routedSetIds.length = 0;
		component.clearSelection({
			preventDefault,
			stopPropagation,
		} as unknown as MouseEvent);
		expect(preventDefault).toHaveBeenCalledOnce();
		expect(stopPropagation).toHaveBeenCalledOnce();
		expect(component.filterText()).toBe('');
		expect(component.selectedSet()).toBeUndefined();
		expect(routedSetIds).toEqual(['-1']);
	});
	it('should request the list when no set is in the URL', () => {
		expect(listSubscriptions).toBe(1);
	});
	describe('with a set in the URL', () => {
		beforeEach(() => {
			create(secondSet.id);
			byIdValue.set(secondSet);
			fixture.detectChanges();
		});
		it('should label the field from the by-id lookup without loading the list', () => {
			expect(listSubscriptions).toBe(0);
			expect(component.selectedSet()).toEqual(secondSet);
			expect(input().value).toBe('Qualification');
			expect(byIdRequests).toEqual([secondSet.id]);
		});
		it('should request the list only once the field is focused', () => {
			input().dispatchEvent(new Event('focus'));
			fixture.detectChanges();
			fixture.detectChanges();
			expect(listSubscriptions).toBe(1);
			expect(component.sets().length).toBe(2);
			expect(component.selectedSet()).toEqual(secondSet);
		});
		it('should request the list when the user types', () => {
			component.applyFilter({
				target: { value: 'q' },
			} as unknown as Event);
			fixture.detectChanges();
			fixture.detectChanges();
			expect(listSubscriptions).toBe(1);
		});
		it('should not fetch the list when Active Only is toggled before opening', () => {
			component.setActiveOnly({ checked: false } as MatCheckboxChange);
			fixture.detectChanges();
			expect(activeOnlyValues).toEqual([false]);
			expect(listSubscriptions).toBe(0);
		});
	});
	describe('while the router has not settled', () => {
		it('should not request the list before the URL is known', () => {
			create('-1', false);
			expect(listSubscriptions).toBe(0);
		});
		it('should never request the list when undefined resolves to a set id', async () => {
			create(secondSet.id, false);
			urlSetId.set(secondSet.id);
			byIdValue.set(secondSet);
			fixture.detectChanges();
			// ngModel writes the input value asynchronously.
			await fixture.whenStable();
			fixture.detectChanges();
			await fixture.whenStable();
			expect(listSubscriptions).toBe(0);
			expect(input().value).toBe('Qualification');
		});
		it('should request the list when undefined resolves to no set', () => {
			create('-1', false);
			urlSetId.set('-1');
			fixture.detectChanges();
			fixture.detectChanges();
			expect(listSubscriptions).toBe(1);
		});
	});
	it('should show an inactive set name independent of the list', async () => {
		create(inactiveSet.id);
		listSubject.next([setsMock[0]]);
		byIdValue.set(inactiveSet);
		fixture.detectChanges();
		await fixture.whenStable();
		fixture.detectChanges();
		await fixture.whenStable();
		expect(listSubscriptions).toBe(0);
		expect(input().value).toBe('Retired Set');
		expect(component.missingSelectedSet()).toBe(false);
	});
	it('should show a loading placeholder while the by-id request is in flight', () => {
		create(secondSet.id);
		byIdLoading.set(true);
		fixture.detectChanges();
		expect(component.loadingSelectedSet()).toBe(true);
		expect(input().placeholder).toBe('Loading set…');
		expect(
			fixture.nativeElement.querySelector('[aria-label="Loading CI Set"]')
		).not.toBeNull();
		byIdLoading.set(false);
		byIdValue.set(secondSet);
		fixture.detectChanges();
		expect(component.loadingSelectedSet()).toBe(false);
		expect(input().placeholder).toBe('');
	});
	it('should report a set that is not found on the branch', () => {
		create('1234');
		byIdValue.set(CISetSentinel);
		fixture.detectChanges();
		expect(component.missingSelectedSet()).toBe(true);
		expect(input().placeholder).toBe('Set not found on this branch.');
	});
	it('should report a failed by-id request without blanking the field', () => {
		create('1234');
		byIdError.set(new Error('boom'));
		fixture.detectChanges();
		expect(input().placeholder).toBe('Unable to load set.');
	});
	it('should show a loading option while the list loads', () => {
		const pending = new Subject<CISet[]>();
		TestBed.resetTestingModule();
		TestBed.configureTestingModule({
			imports: [SetDropdownComponent],
			providers: [
				provideNoopAnimations(),
				{
					provide: CiSetRoutedUiService,
					useValue: {
						urlSetId: signal('-1'),
						setCISetAndNavigate: vi.fn(),
					},
				},
				{
					provide: CiDashboardUiService,
					useValue: { ciSetId: new BehaviorSubject('-1') },
				},
				{
					provide: CiSetsService,
					useValue: {
						ciSets: pending,
						getCiSetResource: () => ({
							hasValue: () => false,
							value: signal(undefined),
							isLoading: signal(false),
							error: signal(undefined),
						}),
						activeOnly: new BehaviorSubject(true),
					},
				},
			],
		});
		const f = TestBed.createComponent(SetDropdownComponent);
		f.detectChanges();
		f.detectChanges();
		expect(f.componentInstance.listLoading()).toBe(true);
		pending.next([setsMock[0]]);
		f.detectChanges();
		expect(f.componentInstance.listLoading()).toBe(false);
	});
});
