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
	effect,
	inject,
	input,
	resource,
	signal,
} from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatButton } from '@angular/material/button';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatInput } from '@angular/material/input';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltip } from '@angular/material/tooltip';
import {
	Field,
	form,
	required,
	validate,
	customError,
} from '@angular/forms/signals';
import { firstValueFrom } from 'rxjs';
import { PublishLauncherHttpService } from './publish-launcher-http.service';
import { PublishLauncherResultDialogComponent } from './publish-launcher-result-dialog.component';
import type {
	PublishLauncherTabConfig,
	DropdownApiItem,
	DropdownOption,
	FilterState,
	FormState,
	TabDropdown,
} from './publish-launcher.types';

/** Matches `{key}` placeholders in URL templates for substitution. */
const URL_PLACEHOLDER = /\{([^}]+)\}/g;

/** Basic email pattern — intentionally permissive to avoid false negatives. */
const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

@Component({
	selector: 'osee-publish-launcher-tab',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		Field,
		MatButton,
		MatFormField,
		MatLabel,
		MatError,
		MatSelect,
		MatOption,
		MatCheckbox,
		MatInput,
		MatTooltip,
	],
	template: `
		@let tabConfig = tab();
		@if (tabConfig.description) {
			<p class="tw-mb-4 tw-text-gray-700 dark:tw-text-gray-300">
				{{ tabConfig.description }}
			</p>
		}

		@if (tabConfig.instructions.length) {
			<div class="tw-mb-4">
				<h3 class="tw-mb-2 tw-font-semibold">Instructions</h3>
				<ul class="tw-ml-6 tw-list-disc tw-space-y-1">
					@for (instruction of tabConfig.instructions; track $index) {
						<li class="tw-text-gray-700 dark:tw-text-gray-300">
							{{ instruction }}
						</li>
					}
				</ul>
			</div>
		}

		<form class="tw-space-y-4">
			<mat-form-field
				appearance="fill"
				class="tw-w-full">
				<mat-label>Email Addresses</mat-label>
				<input
					matInput
					[field]="$any(publishForm['email'])"
					placeholder="user1@example.com,user2@example.com" />
				@if (
					requiresEmail() &&
					publishForm['email']?.()?.touched() &&
					publishForm['email']?.()?.invalid()
				) {
					<mat-error>
						@for (
							error of publishForm['email']?.()?.errors() ?? [];
							track error.kind
						) {
							{{ error.message }}
						}
					</mat-error>
				}
			</mat-form-field>

			@for (dropdown of tabConfig.dropdowns; track dropdown.key) {
				<div class="tw-grid tw-grid-cols-1 tw-gap-4 md:tw-grid-cols-4">
					<mat-form-field
						appearance="fill"
						class="tw-w-full md:tw-col-span-3">
						<mat-label>{{ dropdown.label }}</mat-label>
						<mat-select [field]="$any(publishForm[dropdown.key])">
							@if (!isDropdownRequired(dropdown)) {
								<mat-option [value]="''">(none)</mat-option>
							}
							@for (
								option of getFilteredOptions(dropdown.key);
								track option.id
							) {
								<mat-option [value]="option.id">{{
									option.label
								}}</mat-option>
							}
						</mat-select>
						@if (
							isDropdownRequired(dropdown) &&
							publishForm[dropdown.key]?.()?.touched() &&
							hasFieldError(dropdown.key)
						) {
							<mat-error>
								Please select a
								{{ dropdown.label.toLowerCase() }}
							</mat-error>
						}
					</mat-form-field>

					@if (shouldShowFilter(dropdown.key)) {
						<mat-form-field
							appearance="fill"
							class="tw-w-full md:tw-col-span-1">
							<mat-label>Filter</mat-label>
							<input
								matInput
								[value]="getFilter(dropdown.key)"
								(input)="
									updateFilter(
										dropdown.key,
										inputValue($event)
									)
								"
								placeholder="Contains..." />
						</mat-form-field>
					}
				</div>
			}

			@for (checkbox of tabConfig.checkboxes; track checkbox.key) {
				<mat-checkbox [field]="$any(publishForm[checkbox.key])">
					{{ checkbox.label }}
				</mat-checkbox>
			}

			<div class="tw-pt-4">
				<span [matTooltip]="publishTooltip()">
					<button
						mat-flat-button
						color="primary"
						class="disabled:tw-cursor-not-allowed dark:tw-bg-primary-400 dark:tw-text-black disabled:dark:tw-bg-gray-500 disabled:dark:tw-text-gray-300"
						[disabled]="publishForm().invalid() || publishing()"
						(click)="executePublish()">
						{{ tabConfig.targetApi.button || 'Launch Publish' }}
					</button>
				</span>
			</div>
		</form>
	`,
})
export class PublishLauncherTabComponent {
	readonly tab = input.required<PublishLauncherTabConfig>();
	readonly branchId = input.required<string>();
	readonly branchType = input<string>('');

	private readonly dialog = inject(MatDialog);
	private readonly http = inject(HttpClient);
	private readonly httpService = inject(PublishLauncherHttpService);

	private readonly dropdownFilters = signal<FilterState>({});
	protected readonly publishing = signal(false);

	readonly requiresEmail = computed(() =>
		this.tab().targetApi.url.includes('{email}')
	);

	protected readonly dropdowns = computed(() => this.tab().dropdowns);

	protected readonly requiredDropdowns = computed(() =>
		this.dropdowns().filter((d) => this.isDropdownRequired(d))
	);

	private readonly formModel = signal<FormState>({ email: '' });

	protected readonly publishForm = form(this.formModel, (path) => {
		required(path['email'] as never, {
			when: () => this.requiresEmail(),
			message: 'Email addresses are required',
		});

		validate(path['email'] as never, (ctx) => {
			if (!this.requiresEmail()) return null;
			const email = String(ctx.value() || '').trim();
			if (!email) return null; // handled by required()
			const addresses = email
				.split(',')
				.map((s) => s.trim())
				.filter((s) => s.length > 0);
			const invalid = addresses.filter((a) => !EMAIL_PATTERN.test(a));
			return invalid.length > 0
				? customError({
						kind: 'email_invalid',
						message: 'One or more email addresses are invalid',
					})
				: null;
		});

		// Dynamic required validation for dropdowns runs inside validate()
		// so that this.requiredDropdowns() is read reactively, not eagerly
		// during schema construction (when input.required signals aren't set yet).
		validate(path, (ctx) => {
			const formValue = ctx.value();
			const errors = [];
			for (const dropdown of this.requiredDropdowns()) {
				const value = formValue[dropdown.key];
				if (value === null || value === undefined || value === '') {
					errors.push(
						customError({
							kind: 'required',
							message: `Please select a ${dropdown.label.toLowerCase()}`,
							key: dropdown.key,
						})
					);
				}
			}
			return errors.length > 0 ? errors : null;
		});
	});

	private readonly syncFormModel = effect(() => {
		const tabConfig = this.tab();
		const model: FormState = { email: '' };

		for (const dropdown of tabConfig.dropdowns) {
			model[dropdown.key] = '';
		}

		for (const checkbox of tabConfig.checkboxes) {
			model[checkbox.key] = checkbox.default ?? false;
		}

		this.formModel.set(model);
	});

	// resource() with HttpClient.get is used here instead of httpResource
	// because each tab has a dynamic number of dropdown APIs to fetch in a single loader.
	private readonly dropdownResource = resource({
		params: () => {
			const tab = this.tab();
			const branchId = this.branchId();
			if (!tab || !branchId) return undefined;
			return { tab, branchId };
		},
		loader: async ({ params }) => {
			if (!params) return {};
			const { tab, branchId } = params;
			const result: Record<string, readonly DropdownOption[]> = {};

			for (const dropdown of tab.dropdowns) {
				if (dropdown.options) {
					result[dropdown.key] = dropdown.options;
				} else if (dropdown.contentApi) {
					const url = dropdown.contentApi.url.replace(
						URL_PLACEHOLDER,
						(match, key: string) => {
							if (key === 'branch' || key === 'branchId')
								return encodeURIComponent(branchId);
							if (key === 'artifact' || key === 'artifactId')
								return encodeURIComponent(tab.artifact || '');
							return match;
						}
					);
					const items = await firstValueFrom(
						this.http.get<DropdownApiItem[]>(url)
					);
					result[dropdown.key] = items.map((item) => ({
						id: item.id,
						label: item.name,
					}));
				}
			}

			return result;
		},
		defaultValue: {},
	});

	private readonly urlReplacements = computed(() => {
		const branch = this.branchId();
		const artifact = this.tab().artifact || '';
		const formValue = this.publishForm().value();
		const replacements: Record<string, string> = {
			branch,
			branchId: branch,
			artifact,
			artifactId: artifact,
			email: String(formValue['email'] || '').trim(),
		};
		for (const dropdown of this.tab().dropdowns) {
			const value = formValue[dropdown.key];
			if (value !== null && value !== undefined && value !== '') {
				replacements[dropdown.key] = String(value).trim();
			} else if (!this.isDropdownRequired(dropdown)) {
				replacements[dropdown.key] = '-1';
			}
		}
		return replacements;
	});

	readonly publishTooltip = computed(() => {
		if (this.publishForm().invalid())
			return 'Please fill in all required fields';
		if (this.publishing())
			return 'Please wait for current operation to complete';
		return '';
	});

	private resolveUrlTemplate(url: string): string {
		const replacements = this.urlReplacements();
		return url.replace(URL_PLACEHOLDER, (match, key: string) => {
			const value = replacements[key];
			return value !== undefined ? encodeURIComponent(value) : match;
		});
	}

	hasFieldError(key: string): boolean {
		return this.publishForm()
			.errors()
			.some(
				(e) => (e as unknown as Record<string, unknown>)['key'] === key
			);
	}

	isDropdownRequired(dropdown: TabDropdown): boolean {
		return !!dropdown.required;
	}

	getOptions(dropdownKey: string): readonly DropdownOption[] {
		return this.dropdownResource.value()[dropdownKey] || [];
	}

	getFilter(dropdownKey: string): string {
		return this.dropdownFilters()[this.tab().key]?.[dropdownKey] || '';
	}

	getFilteredOptions(dropdownKey: string): readonly DropdownOption[] {
		const options = this.getOptions(dropdownKey);
		const filterText = this.getFilter(dropdownKey).toLowerCase().trim();
		if (!filterText) return options;
		return options.filter((o) =>
			o.label.toLowerCase().includes(filterText)
		);
	}

	shouldShowFilter(dropdownKey: string): boolean {
		return this.getOptions(dropdownKey).length > 10;
	}

	inputValue(event: Event): string {
		return (event.target as HTMLInputElement).value;
	}

	updateFilter(dropdownKey: string, value: string): void {
		this.dropdownFilters.update((state) => ({
			...state,
			[this.tab().key]: {
				...state[this.tab().key],
				[dropdownKey]: value || '',
			},
		}));
	}

	executePublish(): void {
		const tabConfig = this.tab();

		if (this.publishForm().invalid()) {
			return;
		}

		const formValue = this.publishForm().value();
		const targetUrl = this.resolveUrlTemplate(tabConfig.targetApi.url);

		this.publishing.set(true);
		if (tabConfig.targetApi.method === 'GET') {
			this.executeGetRequest(targetUrl, tabConfig, formValue);
		} else {
			this.executePostRequest(targetUrl, formValue);
		}
	}

	private executeGetRequest(
		url: string,
		tab: PublishLauncherTabConfig,
		formValue: FormState
	): void {
		const urlTemplate = tab.targetApi.url;
		let params = new HttpParams();

		if (
			!urlTemplate.includes('{branch}') &&
			!urlTemplate.includes('{branchId}')
		) {
			params = params.set('branchId', this.branchId());
		}

		for (const dropdown of tab.dropdowns) {
			const value = formValue[dropdown.key];
			if (
				!urlTemplate.includes(`{${dropdown.key}}`) &&
				value !== null &&
				value !== undefined &&
				value !== ''
			) {
				params = params.set(dropdown.key, String(value));
			}
		}

		for (const checkbox of tab.checkboxes) {
			params = params.set(
				checkbox.key,
				String(!!formValue[checkbox.key])
			);
		}

		this.httpService.executeGet(url, params).subscribe({
			next: (response) => this.showResults(response),
			complete: () => this.publishing.set(false),
		});
	}

	private executePostRequest(url: string, formValue: FormState): void {
		const body: FormState = { ...formValue };

		if (url.includes('{branch}') || url.includes('{branchId}')) {
			body['branchId'] = this.branchId();
		}

		if (url.includes('{branchType}')) {
			body['branchType'] = this.branchType();
		}

		this.httpService.executePost(url, body).subscribe({
			next: (response) => this.showResults(response),
			complete: () => this.publishing.set(false),
		});
	}

	private showResults(content: string): void {
		const parsedContent = this.tryParseJson(content);
		this.dialog.open(PublishLauncherResultDialogComponent, {
			data: { content: parsedContent },
			maxWidth: '95vw',
		});
	}

	private tryParseJson(text: string): unknown {
		try {
			return JSON.parse(text);
		} catch {
			return text;
		}
	}
}
