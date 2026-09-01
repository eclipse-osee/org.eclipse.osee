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
import { toSignal } from '@angular/core/rxjs-interop';
import { HttpClient, HttpParams } from '@angular/common/http';
import { httpResource } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';
import { MatButton } from '@angular/material/button';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption } from '@angular/material/core';
import {
	MatAutocomplete,
	MatAutocompleteTrigger,
} from '@angular/material/autocomplete';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatInput } from '@angular/material/input';
import { MatDialog } from '@angular/material/dialog';
import { MatTooltip } from '@angular/material/tooltip';
import { Field, form, validate, customError } from '@angular/forms/signals';
import { firstValueFrom } from 'rxjs';
import { apiURL } from '@osee/environments';
import {
	DragAndDropUploadComponent,
	BranchPickerComponent,
	CurrentViewSelectorComponent,
} from '@osee/shared/components';
import { DispatchEmailSelectorComponent } from './dispatch-email-selector.component';
import { DispatchHttpService } from './dispatch-http.service';
import {
	DispatchResultDialogComponent,
	type DispatchResultDialogData,
} from './dispatch-result-dialog.component';
import type {
	DispatchTabConfig,
	DropdownApiItem,
	DropdownOption,
	FilterState,
	FormState,
	TabDropdown,
	TabFileInput,
} from './dispatch.types';
import { ViewsRoutedUiService } from '@osee/shared/services';

/** Matches `{key}` placeholders in URL templates for substitution. */
const URL_PLACEHOLDER = /\{([^}]+)\}/g;

@Component({
	selector: 'osee-dispatch-tab',
	changeDetection: ChangeDetectionStrategy.OnPush,
	imports: [
		Field,
		MatButton,
		MatFormField,
		MatLabel,
		MatError,
		MatOption,
		MatCheckbox,
		MatInput,
		MatTooltip,
		MatAutocomplete,
		MatAutocompleteTrigger,
		DragAndDropUploadComponent,
		CurrentViewSelectorComponent,
		DispatchEmailSelectorComponent,
		BranchPickerComponent,
	],
	template: `
		@let tabConfig = tab();

		@if (tabConfig.description) {
			<div class="tw-mb-2 tw-text-lg tw-font-semibold">Description:</div>
			<p class="tw-mb-4 tw-text-gray-700 dark:tw-text-gray-300">
				{{ tabConfig.description }}
			</p>
		}

		@if (tabConfig.instructions) {
			<div class="tw-mb-4">
				<div class="tw-mb-2 tw-text-lg tw-font-semibold">
					Instructions:
				</div>
				<div
					class="tw-text-gray-700 dark:tw-text-gray-300"
					[innerHtml]="instructionsHtml()"></div>
			</div>
		}

		<form class="tw-flex tw-flex-col tw-gap-4">
			@for (dropdown of tabConfig.dropdowns; track dropdown.key) {
				@if (areDependenciesMet(dropdown)) {
					<div>
						@switch (dropdown.component) {
							@case ('branchSelector') {
								<osee-branch-picker />
							}
							@case ('viewSelector') {
								<osee-current-view-selector />
							}
							@case ('emailSelector') {
								<osee-dispatch-email-selector
									[(value)]="emailValue" />
							}
							@default {
								<mat-form-field
									appearance="outline"
									subscriptSizing="dynamic"
									class="tw-w-full">
									<mat-label>{{ dropdown.label }}</mat-label>
									<input
										type="text"
										matInput
										[value]="getDisplayValue(dropdown.key)"
										(input)="
											updateFilter(
												dropdown.key,
												inputValue($event)
											)
										"
										(focus)="
											clearInputOnFocus(
												dropdown.key,
												$event
											)
										"
										[matAutocomplete]="dropdownAuto" />
									<mat-autocomplete
										#dropdownAuto="matAutocomplete"
										(optionSelected)="
											selectDropdownOption(
												dropdown.key,
												$event.option.value
											)
										">
										@if (!isDropdownRequired(dropdown)) {
											<mat-option [value]="''"
												>(none)</mat-option
											>
										}
										@for (
											option of getFilteredOptions(
												dropdown.key
											);
											track option.id
										) {
											<mat-option [value]="option.id">{{
												option.label
											}}</mat-option>
										}
									</mat-autocomplete>
									@if (
										isDropdownRequired(dropdown) &&
										publishForm[
											dropdown.key
										]?.()?.touched() &&
										hasFieldError(dropdown.key)
									) {
										<mat-error>
											Please select a
											{{ dropdown.label.toLowerCase() }}
										</mat-error>
									}
								</mat-form-field>
							}
						}
					</div>
				}
			}

			@for (checkbox of tabConfig.checkboxes; track checkbox.key) {
				<mat-checkbox [field]="$any(publishForm[checkbox.key])">
					{{ checkbox.label }}
				</mat-checkbox>
			}

			@if (tabConfig.fileInputs?.length) {
				<div class="tw-space-y-4">
					@for (
						fileInput of tabConfig.fileInputs;
						track fileInput.key
					) {
						<div>
							<h4
								class="tw-mb-1 tw-text-sm tw-font-medium tw-text-gray-700 dark:tw-text-gray-300">
								{{ fileInput.label }}
								@if (fileInput.required) {
									<span class="tw-text-red-600">*</span>
								}
							</h4>
							<osee-drag-and-drop-upload
								[accept]="fileInput.accept"
								[multiple]="fileInput.multiple ?? false"
								[title]="
									'Drag & drop ' +
									fileInput.label.toLowerCase() +
									' here'
								"
								[buttonLabel]="
									'Choose ' + fileInput.label.toLowerCase()
								"
								(filesSelected)="
									onFilesSelected(fileInput.key, $event)
								" />
							@if (getSelectedFileNames(fileInput.key).length) {
								<ul
									class="tw-mt-2 tw-list-inside tw-list-disc tw-text-sm tw-text-gray-600 dark:tw-text-gray-400">
									@for (
										name of getSelectedFileNames(
											fileInput.key
										);
										track name
									) {
										<li>{{ name }}</li>
									}
								</ul>
							}
							@if (
								fileInput.required &&
								!hasFiles(fileInput.key) &&
								publishing()
							) {
								<p
									class="tw-mt-1 tw-text-sm tw-text-red-600 dark:tw-text-red-400">
									Please upload a file.
								</p>
							}
						</div>
					}
				</div>
			}

			<div class="tw-pt-4">
				<span
					class="tw-inline-block"
					[matTooltip]="publishTooltip()">
					<button
						mat-flat-button
						type="button"
						color="primary"
						class="disabled:tw-cursor-not-allowed"
						[disabled]="publishDisabled()"
						(click)="executePublish()">
						{{ tabConfig.targetApi.button || 'Launch Publish' }}
					</button>
				</span>
			</div>
		</form>
	`,
})
export class DispatchTabComponent {
	readonly tab = input.required<DispatchTabConfig>();
	readonly branchId = input.required<string>();
	readonly branchType = input<string>('');

	private readonly dialog = inject(MatDialog);
	private readonly http = inject(HttpClient);
	private readonly httpService = inject(DispatchHttpService);
	private readonly viewsService = inject(ViewsRoutedUiService);
	private readonly sanitizer = inject(DomSanitizer);

	private readonly currentViewId = toSignal(this.viewsService.viewId, {
		initialValue: '-1',
	});

	private readonly dropdownFilters = signal<FilterState>({});
	private readonly fileSelections = signal<
		Readonly<Record<string, readonly File[]>>
	>({});
	protected readonly publishing = signal(false);
	protected readonly emailValue = signal<string>('');

	/** Generic state for registered components keyed by dropdown key. */
	private readonly componentValues = signal<Readonly<Record<string, string>>>(
		{}
	);

	protected readonly dropdowns = computed(() => this.tab().dropdowns);

	private readonly instructionsResource = httpResource.text(() => ({
		url: apiURL + '/define/word/convertMarkdownToHtmlPreview',
		method: 'POST' as const,
		body: this.tab().instructions || '',
		headers: { 'Content-Type': 'text/plain' },
	}));

	// Trust boundary: the instructions markdown comes from DispatchConfig
	// artifacts, which are authored on the common branch by privileged users
	// (the same trust level as any other server-controlled config), and is
	// rendered to HTML by the server's markdown converter. We bypass Angular's
	// sanitizer to preserve the converter's formatting. If config authoring is
	// ever opened to untrusted users, replace bypassSecurityTrustHtml with
	// sanitize(SecurityContext.HTML, ...) here.
	protected readonly instructionsHtml = computed(() =>
		this.sanitizer.bypassSecurityTrustHtml(
			this.instructionsResource.value() ?? ''
		)
	);

	protected readonly hasBranch = computed(() => {
		const id = this.branchId();
		return !!id && id !== '0' && id !== '';
	});

	/**
	 * Checks whether all dependencies declared in `dependsOn` have a non-empty
	 * value in either `componentValues` or the form model.
	 */
	areDependenciesMet(dropdown: TabDropdown): boolean {
		const deps = dropdown.dependsOn;
		if (!deps || deps.length === 0) return true;
		const compValues = this.componentValues();
		const formValue = this.publishForm().value();
		return deps.every((key) => {
			const compVal = compValues[key];
			if (compVal && compVal !== '-1' && compVal !== '0') return true;
			const formVal = formValue[key];
			if (
				formVal !== null &&
				formVal !== undefined &&
				formVal !== '' &&
				formVal !== '-1'
			)
				return true;
			// Also check branchId input for the 'branch' dependency
			if (key === 'branch') return this.hasBranch();
			return false;
		});
	}

	protected readonly fileInputs = computed(() => this.tab().fileInputs ?? []);

	private readonly requiredFileInputs = computed(() =>
		this.fileInputs().filter((f) => f.required)
	);

	protected readonly hasAllRequiredFiles = computed(() =>
		this.requiredFileInputs().every(
			(f) => (this.fileSelections()[f.key]?.length ?? 0) > 0
		)
	);

	protected readonly requiredDropdowns = computed(() =>
		this.dropdowns().filter(
			(d) => this.isDropdownRequired(d) && !d.component
		)
	);

	private readonly formModel = signal<FormState>({});

	protected readonly publishForm = form(this.formModel, (path) => {
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
		const model: FormState = {};

		for (const dropdown of tabConfig.dropdowns) {
			if (!dropdown.component) {
				model[dropdown.key] = '';
			}
		}

		for (const checkbox of tabConfig.checkboxes) {
			model[checkbox.key] = checkbox.default ?? false;
		}

		this.formModel.set(model);
	});

	/**
	 * Syncs the routed view ID into the generic componentValues map.
	 */
	private readonly syncViewToComponentValues = effect(() => {
		const viewId = this.currentViewId();
		const viewDropdown = this.tab().dropdowns.find(
			(d) => d.component === 'viewSelector'
		);
		if (viewDropdown) {
			this.componentValues.update((state) => ({
				...state,
				[viewDropdown.key]: viewId !== '-1' ? viewId : '-1',
			}));
		}
	});

	/**
	 * Syncs the emailValue signal into the generic componentValues map.
	 */
	private readonly syncEmailToComponentValues = effect(() => {
		const email = this.emailValue();
		const emailDropdown = this.tab().dropdowns.find(
			(d) => d.component === 'emailSelector'
		);
		if (emailDropdown) {
			this.componentValues.update((state) => ({
				...state,
				[emailDropdown.key]: email.trim(),
			}));
		}
	});

	/**
	 * Syncs the branchId input into the generic componentValues map.
	 */
	private readonly syncBranchToComponentValues = effect(() => {
		const branch = this.branchId();
		const branchDropdown = this.tab().dropdowns.find(
			(d) => d.component === 'branchSelector'
		);
		if (branchDropdown) {
			this.componentValues.update((state) => ({
				...state,
				[branchDropdown.key]: branch || '',
			}));
		}
	});

	// resource() with HttpClient.get is used here instead of httpResource
	// because each tab has a dynamic number of dropdown APIs to fetch in a single loader.
	private readonly dropdownResource = resource({
		params: () => {
			const tab = this.tab();
			const branchId = this.branchId();
			// Read componentValues so the resource re-runs when dependencies change
			const compValues = this.componentValues();
			if (!tab) return undefined;
			return { tab, branchId, compValues };
		},
		loader: async ({ params }) => {
			if (!params) return {};
			const { tab, branchId, compValues } = params;
			const result: Record<string, readonly DropdownOption[]> = {};

			for (const dropdown of tab.dropdowns) {
				// Skip if dependencies aren't met
				if (dropdown.dependsOn?.length) {
					const depsMet = dropdown.dependsOn.every((key) => {
						const val = compValues[key];
						return val && val !== '-1' && val !== '0' && val !== '';
					});
					if (!depsMet) continue;
				}

				if (dropdown.options) {
					result[dropdown.key] = dropdown.options;
				} else if (dropdown.contentApi) {
					const url =
						apiURL +
						dropdown.contentApi.url.replace(
							URL_PLACEHOLDER,
							(match, key: string) => {
								if (key === 'branch' || key === 'branchId')
									return encodeURIComponent(branchId);
								if (key === 'artifact' || key === 'artifactId')
									return encodeURIComponent(
										tab.artifact || ''
									);
								return match;
							}
						);
					try {
						const items = await firstValueFrom(
							this.http.get<DropdownApiItem[]>(url)
						);
						result[dropdown.key] = items.map((item) => ({
							id: item.id,
							label: item.name,
						}));
					} catch (e) {
						console.warn(
							`[Dispatch] Failed to fetch options for dropdown "${dropdown.key}":`,
							e
						);
						result[dropdown.key] = [];
					}
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
		const compValues = this.componentValues();
		const replacements: Record<string, string> = {
			branch,
			branchId: branch,
			artifact,
			artifactId: artifact,
		};
		for (const dropdown of this.tab().dropdowns) {
			if (dropdown.component) {
				// Value comes from the registered component via componentValues
				const compVal = compValues[dropdown.key];
				replacements[dropdown.key] = compVal ?? '';
			} else {
				const value = formValue[dropdown.key];
				if (value !== null && value !== undefined && value !== '') {
					replacements[dropdown.key] = String(value).trim();
				} else if (!this.isDropdownRequired(dropdown)) {
					replacements[dropdown.key] = '-1';
				}
			}
		}
		return replacements;
	});

	readonly publishTooltip = computed(() => {
		if (this.publishForm().invalid())
			return 'Please fill in all required fields.';
		if (!this.hasAllRequiredFiles())
			return 'Please upload all required files.';
		if (this.publishing())
			return 'Please wait for current operation to complete.';
		return '';
	});

	readonly publishDisabled = computed(
		() =>
			this.publishForm().invalid() ||
			this.publishing() ||
			!this.hasAllRequiredFiles()
	);

	private resolveUrlTemplate(url: string): string {
		const replacements = this.urlReplacements();
		const unresolved: string[] = [];
		const resolved = url.replace(URL_PLACEHOLDER, (_match, key: string) => {
			const value = replacements[key];
			if (value !== undefined) {
				return encodeURIComponent(value);
			}
			unresolved.push(key);
			return '';
		});

		if (unresolved.length > 0) {
			console.warn(
				`[Dispatch] Unresolved URL placeholders: {${unresolved.join('}, {')}}. Check that the config has matching dropdowns or components for these keys.`
			);
		}

		if (resolved.startsWith('http://') || resolved.startsWith('https://')) {
			return resolved;
		}

		return apiURL + resolved;
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

	/** Returns the display label for the currently selected option, or the filter text if typing. */
	getDisplayValue(dropdownKey: string): string {
		const filterText = this.getFilter(dropdownKey);
		if (filterText) return filterText;
		const formValue = this.publishForm().value();
		const selectedId = formValue[dropdownKey];
		if (!selectedId || selectedId === '') return '';
		const options = this.getOptions(dropdownKey);
		const match = options.find((o) => o.id === selectedId);
		return match?.label ?? String(selectedId);
	}

	/** Clears the input text on focus so the user can type to filter. */
	clearInputOnFocus(dropdownKey: string, event: FocusEvent): void {
		const input = event.target as HTMLInputElement;
		input.value = '';
		this.updateFilter(dropdownKey, '');
	}

	/** Handles selection from the autocomplete dropdown. */
	selectDropdownOption(dropdownKey: string, value: string): void {
		this.formModel.update((model) => ({
			...model,
			[dropdownKey]: value,
		}));
		this.updateFilter(dropdownKey, '');
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

	onFilesSelected(key: string, files: File[]): void {
		this.fileSelections.update((state) => ({
			...state,
			[key]: files,
		}));
	}

	getSelectedFileNames(key: string): readonly string[] {
		return (this.fileSelections()[key] ?? []).map((f) => f.name);
	}

	hasFiles(key: string): boolean {
		return (this.fileSelections()[key]?.length ?? 0) > 0;
	}

	executePublish(): void {
		const tabConfig = this.tab();

		if (this.publishDisabled()) {
			return;
		}

		const formValue = this.publishForm().value();
		const targetUrl = this.resolveUrlTemplate(tabConfig.targetApi.url);
		const files = this.fileSelections();
		const hasFiles = Object.values(files).some((f) => f.length > 0);

		this.publishing.set(true);
		if (tabConfig.targetApi.method === 'GET') {
			this.executeGetRequest(targetUrl, tabConfig, formValue);
		} else if (hasFiles && this.getRawFileInput()) {
			this.executePostRawFileRequest(targetUrl);
		} else if (hasFiles) {
			this.executePostWithFilesRequest(targetUrl, formValue, files);
		} else {
			this.executePostRequest(targetUrl, formValue);
		}
	}

	private executeGetRequest(
		url: string,
		tab: DispatchTabConfig,
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

		this.httpService
			.executeGet(url, params)
			.subscribe(this.resultObserver());
	}

	private executePostRequest(url: string, formValue: FormState): void {
		const body: FormState = { ...formValue };

		this.httpService
			.executePost(url, body)
			.subscribe(this.resultObserver());
	}

	private executePostWithFilesRequest(
		url: string,
		formValue: FormState,
		files: Readonly<Record<string, readonly File[]>>
	): void {
		const body: FormState = { ...formValue };

		this.httpService
			.executePostWithFiles(url, body, files)
			.subscribe(this.resultObserver());
	}

	private executePostRawFileRequest(url: string): void {
		const rawInput = this.getRawFileInput();
		if (!rawInput) return;

		const files = this.fileSelections()[rawInput.key];
		if (!files?.length) return;

		this.httpService
			.executePostRawFile(url, files[0] as File, rawInput.contentType!)
			.subscribe(this.resultObserver());
	}

	/**
	 * Returns the first file input that specifies a contentType (raw body mode).
	 * When contentType is set, the file is sent as the raw request body with that
	 * content type header instead of as multipart form data.
	 */
	private getRawFileInput(): TabFileInput | undefined {
		return this.fileInputs().find((f) => !!f.contentType);
	}

	/**
	 * Shared observer for publish requests. On success the result dialog is
	 * shown; on error the spinner is cleared and no dialog opens (the error
	 * has already been surfaced via UiService.ErrorText by the HTTP service),
	 * so a failed request is not mistaken for an empty successful response.
	 */
	private resultObserver() {
		return {
			next: (response: string) => this.showResults(response),
			error: () => this.publishing.set(false),
			complete: () => this.publishing.set(false),
		};
	}

	private showResults(content: string): void {
		const parsedContent = this.tryParseJson(content);
		const dialogData: DispatchResultDialogData = {
			content: parsedContent,
			downloadFileName: this.tab().downloadFileName,
		};
		this.dialog.open(DispatchResultDialogComponent, {
			data: dialogData,
			minWidth: '40vw',
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
