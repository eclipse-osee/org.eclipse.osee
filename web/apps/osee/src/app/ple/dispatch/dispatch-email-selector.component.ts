/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	inject,
	model,
	signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
	MatChipGrid,
	MatChipInput,
	MatChipRemove,
	MatChipRow,
} from '@angular/material/chips';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
	MatOption,
} from '@angular/material/autocomplete';
import {
	MatFormField,
	MatLabel,
	MatSuffix,
} from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatInput } from '@angular/material/input';
import { UserDataAccountService } from '@osee/auth';
import { ActionService } from '@osee/configuration-management/services';
import { map } from 'rxjs';

/**
 * Email selector with a filterable autocomplete dropdown and chip display.
 * Loads all active user emails. Current user's email appears at the top
 * with a "You" tag. Selected emails display as removable chips.
 *
 * Emits a comma-separated string of all selected emails.
 */
@Component({
	selector: 'osee-dispatch-email-selector',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		MatFormField,
		MatLabel,
		MatSuffix,
		MatInput,
		MatChipGrid,
		MatChipInput,
		MatChipRemove,
		MatChipRow,
		MatAutocomplete,
		MatAutocompleteTrigger,
		MatOption,
		MatIcon,
		MatIconButton,
	],
	template: `
		<mat-form-field
			appearance="outline"
			subscriptSizing="dynamic"
			class="tw-w-full">
			<mat-label>Email Addresses</mat-label>
			<mat-chip-grid #chipGrid>
				@for (email of selectedEmails(); track email) {
					<mat-chip-row (removed)="removeEmail(email)">
						{{ email }}
						<button
							matChipRemove
							aria-label="Remove email">
							<mat-icon>cancel</mat-icon>
						</button>
					</mat-chip-row>
				}
			</mat-chip-grid>
			<input
				matInput
				[matChipInputFor]="chipGrid"
				[matAutocomplete]="auto"
				[value]="filterText()"
				(input)="onFilterInput($event)"
				placeholder="Search users..." />
			@if (filterText()) {
				<button
					matSuffix
					mat-icon-button
					aria-label="Clear filter"
					(mousedown)="clearFilter($event)">
					<mat-icon>close</mat-icon>
				</button>
			}
			<mat-autocomplete
				#auto="matAutocomplete"
				(optionSelected)="selectEmail($event.option.value)">
				@for (email of filteredOptions(); track email) {
					<mat-option [value]="email">
						{{ email }}
						@if (isCurrentUser(email)) {
							<span
								class="tw-ml-2 tw-rounded tw-bg-primary-100 tw-px-1.5 tw-py-0.5 tw-text-xs tw-text-primary dark:tw-bg-primary-800 dark:tw-text-primary-200">
								You
							</span>
						}
					</mat-option>
				}
			</mat-autocomplete>
		</mat-form-field>
	`,
})
export class DispatchEmailSelectorComponent {
	/** Two-way binding: comma-separated email string. */
	readonly value = model<string>('');

	private readonly userService = inject(UserDataAccountService);
	private readonly actionService = inject(ActionService);

	protected readonly filterText = signal('');

	private readonly currentUserEmail = toSignal(
		this.userService.user.pipe(map((u) => u.email)),
		{ initialValue: '' }
	);

	private readonly allUserEmails = toSignal(
		this.actionService.users.pipe(
			map((users) =>
				users
					.flatMap((u) => {
						const emails: string[] = [];
						if (u.email) emails.push(u.email);
						return emails;
					})
					.filter((email) => email.length > 0)
					.sort((a, b) => a.localeCompare(b))
			)
		),
		{ initialValue: [] as string[] }
	);

	/** All emails sorted with current user's email at the top. */
	private readonly sortedEmails = computed(() => {
		const all = this.allUserEmails();
		const currentEmail = this.currentUserEmail();
		if (!currentEmail) return all;
		const withoutCurrent = all.filter((e) => e !== currentEmail);
		return [currentEmail, ...withoutCurrent];
	});

	protected readonly selectedEmails = computed(() => {
		const val = this.value();
		if (!val.trim()) return [] as string[];
		return val
			.split(',')
			.map((s) => s.trim())
			.filter((s) => s.length > 0);
	});

	/** Filtered options: excludes already-selected emails and applies text filter. */
	protected readonly filteredOptions = computed(() => {
		const selected = new Set(this.selectedEmails());
		const filter = this.filterText().toLowerCase().trim();
		return this.sortedEmails().filter(
			(email) =>
				!selected.has(email) &&
				(!filter || email.toLowerCase().includes(filter))
		);
	});

	isCurrentUser(email: string): boolean {
		return email === this.currentUserEmail();
	}

	selectEmail(email: string): void {
		if (!this.selectedEmails().includes(email)) {
			this.value.set([...this.selectedEmails(), email].join(','));
		}
		this.filterText.set('');
	}

	removeEmail(email: string): void {
		this.value.set(
			this.selectedEmails()
				.filter((e) => e !== email)
				.join(',')
		);
	}

	onFilterInput(event: Event): void {
		this.filterText.set((event.target as HTMLInputElement).value);
	}

	clearFilter(event: MouseEvent): void {
		event.preventDefault();
		event.stopPropagation();
		this.filterText.set('');
	}
}
