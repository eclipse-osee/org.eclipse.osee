/*********************************************************************
 * Copyright (c) 2025 Boeing
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
import { By } from '@angular/platform-browser';
import { MatTooltip } from '@angular/material/tooltip';
import { presenceUser } from '@osee/shared/services';
import { PresenceAvatarsComponent } from './presence-avatars.component';

function user(userId: string, userName: string): presenceUser {
	return { userId, userName } as presenceUser;
}

describe('PresenceAvatarsComponent', () => {
	let fixture: ComponentFixture<PresenceAvatarsComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [PresenceAvatarsComponent],
		});
		fixture = TestBed.createComponent(PresenceAvatarsComponent);
	});

	function setUsers(users: presenceUser[], maxVisible?: number) {
		fixture.componentRef.setInput('users', users);
		if (maxVisible !== undefined) {
			fixture.componentRef.setInput('maxVisible', maxVisible);
		}
		fixture.detectChanges();
	}

	/** The mat-icon glyph text of each rendered avatar circle. */
	function avatarIcons(): string[] {
		return Array.from(
			fixture.nativeElement.querySelectorAll('mat-icon')
		).map((el) => (el as HTMLElement).textContent?.trim() ?? '');
	}

	/** The overflow indicator text, or null if none is rendered. */
	function overflowText(): string | null {
		const spans = Array.from(
			fixture.nativeElement.querySelectorAll('span')
		) as HTMLElement[];
		const overflow = spans.find((el) =>
			(el.textContent ?? '').trim().startsWith('+')
		);
		return overflow ? (overflow.textContent ?? '').trim() : null;
	}

	it('renders nothing when there are no users', () => {
		setUsers([]);
		expect(fixture.nativeElement.querySelector('div')).toBeNull();
		expect(avatarIcons()).toEqual([]);
	});

	it('renders one generic avatar icon per visible user', () => {
		setUsers([user('1', 'Joe Smith'), user('2', 'Sam Adams')]);
		const icons = avatarIcons();
		expect(icons).toHaveLength(2);
		// Every glyph comes from the fixed curated icon set (never derived initials).
		icons.forEach((icon) => expect(icon.length).toBeGreaterThan(0));
	});

	it('assigns a deterministic icon+color from the stable user id, not the name', () => {
		// Same id, wildly different (and unstructured) names -> identical avatar.
		setUsers([user('42', 'Campanella, Zachary A')]);
		const first = fixture.nativeElement.querySelector(
			'mat-icon'
		) as HTMLElement;
		const firstIcon = first.textContent?.trim();
		const firstColorClass = (first.parentElement as HTMLElement).className;

		setUsers([user('42', 'totally different name')]);
		const second = fixture.nativeElement.querySelector(
			'mat-icon'
		) as HTMLElement;
		expect(second.textContent?.trim()).toBe(firstIcon);
		expect((second.parentElement as HTMLElement).className).toBe(
			firstColorClass
		);
	});

	it('is stable across re-renders for the same id', () => {
		setUsers([user('user-99', 'Someone')]);
		const before = avatarIcons();
		setUsers([user('user-99', 'Someone')]);
		expect(avatarIcons()).toEqual(before);
	});

	it('does not mis-parse an unstructured name (regression: "Last, First M")', () => {
		// The old implementation derived initials from the name and produced wrong
		// output for comma/multi-token names. The avatar must not contain letters
		// pulled from the name; it is a fixed-set glyph keyed by id.
		setUsers([user('7', 'Campanella, Zachary A')]);
		const icon = avatarIcons()[0];
		// A curated glyph name (e.g. 'pets'), never a 1-2 char initials string.
		expect(icon.length).toBeGreaterThan(2);
	});

	it('shows a +N overflow indicator beyond maxVisible', () => {
		setUsers(
			[
				user('1', 'Aa Aa'),
				user('2', 'Bb Bb'),
				user('3', 'Cc Cc'),
				user('4', 'Dd Dd'),
				user('5', 'Ee Ee'),
			],
			3
		);
		expect(avatarIcons()).toHaveLength(3);
		expect(overflowText()).toBe('+2');
	});

	it('renders no overflow indicator when within maxVisible', () => {
		setUsers([user('1', 'Aa Aa'), user('2', 'Bb Bb')], 3);
		expect(overflowText()).toBeNull();
	});

	it('sets the tooltip to the full user name', () => {
		setUsers([user('1', 'Campanella, Zachary A')]);
		const tooltip = fixture.debugElement
			.queryAll(By.directive(MatTooltip))
			.map((de) => de.injector.get(MatTooltip).message);
		expect(tooltip[0]).toContain('Campanella, Zachary A');
	});
});
