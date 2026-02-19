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
	Component,
	computed,
	effect,
	inject,
	Input,
	Inject,
	signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import {
	FormBuilder,
	FormGroup,
	ReactiveFormsModule,
	Validators,
} from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';

import { MatTabsModule } from '@angular/material/tabs';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatInputModule } from '@angular/material/input';
import {
	MAT_DIALOG_DATA,
	MatDialog,
	MatDialogModule,
} from '@angular/material/dialog';
import { BranchPickerComponent } from '@osee/shared/components';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';
import { shareReplay, tap } from 'rxjs/operators';

type BranchType = 'working' | 'baseline' | '';

interface ConfigOption {
	id: string | number;
	label: string;
}

interface DropdownApiItem {
	id: string;
	name: string;
	typeId?: string;
	typeName?: string;
}

interface TabDropdown {
	key: string;
	label: string;
	options: ConfigOption[];
	contentApi?: TargetApi;
	value?: string;
}

interface TabCheckbox {
	key: string;
	label: string;
	default?: boolean;
}

interface TargetApi {
	method: 'GET' | 'POST';
	url: string;
	button?: string;
}

interface BookExplorerTabConfig {
	key: string;
	label: string;
	description?: string;
	instructions?: string[];
	dropdowns: TabDropdown[];
	checkboxes: TabCheckbox[];
	targetApi: TargetApi;
	artifact?: string;
}

interface BookExplorerConfig {
	title?: string;
	tabs: BookExplorerTabConfig[];
}

@Component({
	selector: 'osee-book-explorer-result-dialog',
	standalone: true,
	imports: [CommonModule, MatDialogModule, MatButtonModule],
	template: `
		<h2 mat-dialog-title>Results</h2>

		<mat-dialog-content
			class="tw-max-h-[70vh] tw-w-[min(90vw,900px)] tw-overflow-auto">
			@if (isHtml) {
				<div [innerHTML]="displayContent"></div>
			} @else {
				<pre class="tw-whitespace-pre-wrap tw-break-words">{{
					displayContent
				}}</pre>
			}
		</mat-dialog-content>

		<mat-dialog-actions align="end">
			<button
				mat-button
				mat-dialog-close>
				Close
			</button>
		</mat-dialog-actions>
	`,
})
export class BookExplorerResultDialogComponent {
	constructor(
		@Inject(MAT_DIALOG_DATA)
		public data: { content: unknown }
	) {}

	get isHtml(): boolean {
		return (
			typeof this.data.content === 'string' &&
			/<[a-z][\s\S]*>/i.test(this.data.content)
		);
	}

	get displayContent(): string {
		if (typeof this.data.content === 'string') {
			return this.data.content;
		}

		try {
			return JSON.stringify(this.data.content, null, 2);
		} catch {
			return String(this.data.content);
		}
	}
}

@Component({
	selector: 'osee-book-explorer',
	standalone: true,
	imports: [
		CommonModule,
		ReactiveFormsModule,
		MatTabsModule,
		MatButtonModule,
		MatFormFieldModule,
		MatSelectModule,
		MatCheckboxModule,
		MatInputModule,
		MatDialogModule,
		BranchPickerComponent,
	],
	template: `
		@if (config(); as config) {
			<mat-tab-group>
				@for (tab of config.tabs; track tab.key) {
					<mat-tab [label]="tab.label">
						<div class="tw-p-4">
							<osee-branch-picker />

							<div class="tw-mt-4">
								Selected Branch: {{ currentBranchId }}
							</div>

							@if (!hasBranch()) {
								<div
									class="tw-mt-3 tw-rounded tw-border tw-border-amber-300 tw-bg-amber-50 tw-p-3 tw-text-amber-900"
									role="alert">
									Please select a branch to continue.
								</div>
							}

							@if (tab.description) {
								<p class="tw-mt-4">
									{{ tab.description }}
								</p>
							}

							@if (tab.instructions?.length) {
								<div class="tw-mt-2">
									<div class="tw-font-semibold">
										Instructions
									</div>
									<ul class="tw-ml-6 tw-list-disc">
										@for (
											line of tab.instructions;
											track $index
										) {
											<li>{{ line }}</li>
										}
									</ul>
								</div>
							}

							@if (requiresEmail(tab)) {
								<form
									class="tw-mt-4"
									[formGroup]="forms[tab.key]">
									<mat-form-field
										appearance="fill"
										class="tw-w-full">
										<mat-label>Email Addresses</mat-label>
										<input
											matInput
											formControlName="email"
											required
											placeholder="user1@example.com,user2@example.com" />
										@if (
											forms[tab.key]
												.get('email')
												?.hasError('required') &&
											forms[tab.key].get('email')?.touched
										) {
											<mat-error>
												Email Addresses is required.
											</mat-error>
										}
									</mat-form-field>
								</form>
							}

							<form
								class="tw-mt-4 tw-space-y-4"
								[formGroup]="forms[tab.key]"
								[class.tw-opacity-50]="!hasBranch()">
								<fieldset
									[disabled]="!hasBranch()"
									class="tw-m-0 tw-border-0 tw-p-0">
									@for (dd of tab.dropdowns; track dd.key) {
										<div
											class="tw-grid tw-grid-cols-1 tw-gap-4 md:tw-grid-cols-4">
											<mat-form-field
												appearance="fill"
												class="tw-w-full md:tw-col-span-3">
												<mat-label>{{
													dd.label
												}}</mat-label>
												<mat-select
													[formControlName]="dd.key"
													[required]="
														isDropdownRequired(dd)
													">
													@if (
														!isDropdownRequired(dd)
													) {
														<mat-option
															[value]="null"
															>(none)</mat-option
														>
													}
													@for (
														opt of getFilteredDropdownOptionsFor(
															tab,
															dd
														);
														track opt.id
													) {
														<mat-option
															[value]="opt.id">
															{{ opt.label }}
														</mat-option>
													}
												</mat-select>
												@if (
													forms[tab.key]
														.get(dd.key)
														?.hasError(
															'required'
														) &&
													forms[tab.key].get(dd.key)
														?.touched
												) {
													<mat-error>
														Please select a
														{{
															dd.label.toLowerCase()
														}}.
													</mat-error>
												}
											</mat-form-field>

											<mat-form-field
												appearance="fill"
												class="tw-w-full md:tw-col-span-1">
												<mat-label>Filter</mat-label>
												<input
													matInput
													[value]="
														getDropdownFilter(
															tab.key,
															dd.key
														)
													"
													(input)="
														setDropdownFilter(
															tab.key,
															dd.key,
															$any($event.target)
																.value
														)
													"
													placeholder="Contains..." />
											</mat-form-field>
										</div>
									}

									@for (cb of tab.checkboxes; track cb.key) {
										<mat-checkbox
											[formControlName]="cb.key">
											{{ cb.label }}
										</mat-checkbox>
									}
								</fieldset>
							</form>

							<button
								mat-raised-button
								color="primary"
								class="tw-mt-4"
								[disabled]="!hasBranch()"
								(click)="doWork(tab)">
								{{ tab.targetApi.button || 'Do Work' }}
							</button>
						</div>
					</mat-tab>
				}
			</mat-tab-group>
		}
	`,
})
export class BookExplorerComponent {
	@Input() set branchType(branchType: BranchType) {
		const value = branchType ?? '';
		this.branchTypeInput.set(value);
		this.uiService.typeValue = value;
	}

	@Input() set branchId(branchId: string) {
		const value = branchId ?? '';
		this.branchIdInput.set(value);
		this.uiService.idValue = value;
	}

	private readonly uiService = inject(UiService);
	private readonly branchRouteState = inject(BranchRoutedUIService);
	private readonly http = inject(HttpClient);
	private readonly fb = inject(FormBuilder);
	private readonly dialog = inject(MatDialog);

	private readonly branchTypeInput = signal<BranchType>('');
	private readonly branchIdInput = signal('');

	readonly routeBranchId = toSignal(this.branchRouteState.id, {
		initialValue: '',
	});

	readonly hasBranch = computed(() => !!this.currentBranchId);

	readonly forms: Record<string, FormGroup> = {};
	readonly dropdownOptions: Record<string, Record<string, ConfigOption[]>> =
		{};
	readonly dropdownFilters: Record<string, Record<string, string>> = {};

	readonly config = toSignal(
		this.http
			.get<BookExplorerConfig>(
				'/orcs/branch/570/artifact/10716029/attribute/type/1152921504606847380'
			)
			.pipe(
				tap((cfg) => {
					this.ensureForms(cfg);
				}),
				shareReplay({ bufferSize: 1, refCount: true })
			),
		{ initialValue: undefined }
	);

	private readonly dropdownContentEffect = effect(() => {
		const cfg = this.config();
		const branchId = this.currentBranchId;

		if (!cfg || !branchId) return;

		this.loadDropdownContent(cfg);
	});

	get currentBranchId(): string {
		return String(
			this.branchIdInput() ||
				this.routeBranchId() ||
				this.uiService.idValue ||
				''
		).trim();
	}

	get currentBranchType(): BranchType {
		return (
			this.branchTypeInput() ||
			(this.uiService.typeValue as BranchType) ||
			''
		);
	}

	isDropdownRequired(dd: TabDropdown): boolean {
		return dd.value === 'required';
	}

	setDropdownFilter(tabKey: string, dropdownKey: string, value: string) {
		this.dropdownFilters[tabKey] ??= {};
		this.dropdownFilters[tabKey][dropdownKey] = value ?? '';
	}

	getDropdownFilter(tabKey: string, dropdownKey: string): string {
		return this.dropdownFilters[tabKey]?.[dropdownKey] ?? '';
	}

	private ensureForms(cfg: BookExplorerConfig) {
		for (const tab of cfg.tabs) {
			if (this.forms[tab.key]) continue;

			const group: Record<string, unknown> = {};

			if (this.requiresEmail(tab)) {
				group['email'] = ['', Validators.required];
			}

			for (const dd of tab.dropdowns) {
				group[dd.key] = this.isDropdownRequired(dd)
					? [null, Validators.required]
					: [null];
			}

			for (const cb of tab.checkboxes) {
				group[cb.key] = [cb.default ?? false];
			}

			this.forms[tab.key] = this.fb.group(group);
		}
	}

	private resolveUrlTemplate(
		url: string,
		tab?: BookExplorerTabConfig,
		formValue?: Record<string, unknown>
	) {
		const branch = this.currentBranchId;
		const artifact = String(tab?.artifact ?? '').trim();

		const replacements: Record<string, string> = {
			branch,
			branchId: branch,
			artifact,
			artifactId: artifact,
			email: String(formValue?.['email'] ?? '').trim(),
		};

		if (tab) {
			for (const dd of tab.dropdowns) {
				const selectedValue = formValue?.[dd.key];
				const hasValue =
					selectedValue !== null &&
					selectedValue !== undefined &&
					selectedValue !== '';

				if (hasValue) {
					replacements[dd.key] = String(selectedValue).trim();
				} else if (!this.isDropdownRequired(dd)) {
					replacements[dd.key] = '-1';
				}
			}
		}

		let resolved = url;
		resolved = resolved.replace(/\{([^}]+)\}/g, (match, key: string) => {
			const value = replacements[key];
			return value !== undefined ? encodeURIComponent(value) : match;
		});

		if (resolved.includes('{')) {
			console.warn('Unresolved URL template tokens in:', {
				url,
				resolved,
				tabKey: tab?.key,
				replacements,
			});
		}

		return resolved;
	}

	requiresEmail(tab: BookExplorerTabConfig) {
		return tab.targetApi.url.includes('{email}');
	}

	private mapDropdownItems(
		items: DropdownApiItem[] | null | undefined
	): ConfigOption[] {
		return (items ?? []).map((x) => ({
			id: x.id,
			label: x.name,
		}));
	}

	private loadDropdownOptions(tab: BookExplorerTabConfig, dd: TabDropdown) {
		if (!dd.contentApi) return;

		const url = this.resolveUrlTemplate(dd.contentApi.url, tab);
		const request =
			dd.contentApi.method === 'GET'
				? this.http.get<DropdownApiItem[]>(url)
				: this.http.post<DropdownApiItem[]>(url, {});

		request.subscribe((items) => {
			this.dropdownOptions[tab.key][dd.key] =
				this.mapDropdownItems(items);
		});
	}

	private loadDropdownContent(cfg: BookExplorerConfig) {
		if (!this.currentBranchId) return;

		for (const tab of cfg.tabs) {
			this.dropdownOptions[tab.key] ??= {};
			this.dropdownFilters[tab.key] ??= {};

			for (const dd of tab.dropdowns) {
				this.dropdownFilters[tab.key][dd.key] ??= '';
				this.loadDropdownOptions(tab, dd);
			}
		}
	}

	getDropdownOptionsFor(
		tab: BookExplorerTabConfig,
		dd: TabDropdown
	): ConfigOption[] {
		if (dd.contentApi) {
			return this.dropdownOptions[tab.key]?.[dd.key] ?? [];
		}
		return dd.options ?? [];
	}

	getFilteredDropdownOptionsFor(
		tab: BookExplorerTabConfig,
		dd: TabDropdown
	): ConfigOption[] {
		const filterValue = this.getDropdownFilter(tab.key, dd.key)
			.trim()
			.toLowerCase();

		const options = this.getDropdownOptionsFor(tab, dd);

		if (!filterValue) {
			return options;
		}

		return options.filter((opt) =>
			String(opt.label ?? '')
				.toLowerCase()
				.includes(filterValue)
		);
	}

	shouldShowDropdownFilter(
		tab: BookExplorerTabConfig,
		dd: TabDropdown
	): boolean {
		return this.getDropdownOptionsFor(tab, dd).length > 10;
	}

	private tryParseResponse(response: string): unknown {
		try {
			return JSON.parse(response);
		} catch {
			return response;
		}
	}

	private showResults(content: unknown) {
		this.dialog.open(BookExplorerResultDialogComponent, {
			data: { content },
			maxWidth: '95vw',
		});
	}

	doWork(tab: BookExplorerTabConfig) {
		const branchId = this.currentBranchId;
		if (!branchId) return;

		const form = this.forms[tab.key];
		if (!form) return;

		if (form.invalid) {
			form.markAllAsTouched();
			return;
		}

		const value = form.getRawValue();
		const targetUrl = this.resolveUrlTemplate(
			tab.targetApi.url,
			tab,
			value
		);

		let params = new HttpParams();

		if (
			!tab.targetApi.url.includes('{branch}') &&
			!tab.targetApi.url.includes('{branchId}')
		) {
			params = params.set('branchId', branchId);
		}

		for (const dd of tab.dropdowns) {
			const id = value[dd.key];
			const token = `{${dd.key}}`;

			if (
				!tab.targetApi.url.includes(token) &&
				id !== null &&
				id !== undefined &&
				id !== ''
			) {
				params = params.set(dd.key, String(id));
			}
		}

		for (const cb of tab.checkboxes) {
			params = params.set(cb.key, String(!!value[cb.key]));
		}

		if (tab.targetApi.method === 'GET') {
			this.http
				.get(targetUrl, {
					params,
					responseType: 'text',
				})
				.subscribe((response) => {
					this.showResults(this.tryParseResponse(response));
				});
		} else {
			const body: Record<string, unknown> = { ...value };

			if (
				tab.targetApi.url.includes('{branch}') ||
				tab.targetApi.url.includes('{branchId}')
			) {
				body['branchId'] = branchId;
			}

			if (tab.targetApi.url.includes('{branchType}')) {
				body['branchType'] = this.currentBranchType;
			}

			this.http
				.post(targetUrl, body, {
					responseType: 'text',
				})
				.subscribe((response) => {
					this.showResults(this.tryParseResponse(response));
				});
		}
	}
}

export default BookExplorerComponent;
