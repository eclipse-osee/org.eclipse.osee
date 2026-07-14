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
	DestroyRef,
	ElementRef,
	inject,
	input,
	output,
	signal,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { filter, fromEvent } from 'rxjs';

/**
 * An option displayed in the split button dropdown.
 */
export type SplitButtonOption = {
	/** Unique value emitted when this option is selected */
	value: string;
	/** Display label */
	name: string;
	/** Material icon name */
	icon: string;
};

/**
 * A split button with a default action (icon click) and a dropdown
 * arrow that reveals additional options. Renders entirely inline
 * (no CDK overlay) so it works inside fullscreen elements.
 *
 * The outline and divider only appear on hover for a clean toolbar look.
 * The dropdown closes when clicking outside or when focus leaves.
 */
@Component({
	selector: 'osee-split-button',
	standalone: true,
	imports: [MatIcon, MatTooltip],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<span
			class="tw-group tw-relative tw-inline-flex tw-items-stretch tw-overflow-visible tw-rounded tw-border tw-border-transparent hover:tw-border-gray-300 dark:hover:tw-border-gray-600"
			[class.tw-opacity-40]="disabled()">
			<button
				class="tw-flex tw-cursor-pointer tw-items-center tw-justify-center tw-border-0 tw-bg-transparent tw-p-1 tw-outline-none hover:tw-bg-gray-100 disabled:tw-cursor-not-allowed disabled:hover:tw-bg-transparent dark:hover:tw-bg-gray-700"
				[disabled]="disabled()"
				[matTooltip]="tooltip()"
				(click)="defaultAction.emit()">
				<mat-icon
					class="!tw-h-5 !tw-w-5 !tw-text-[20px] !tw-leading-[20px]">
					{{ icon() }}
				</mat-icon>
			</button>
			<span
				class="tw-w-px tw-self-stretch tw-bg-transparent group-hover:tw-bg-gray-300 dark:group-hover:tw-bg-gray-600"></span>
			<button
				class="tw-flex tw-w-4 tw-cursor-pointer tw-items-center tw-justify-center tw-border-0 tw-bg-transparent tw-p-0 tw-outline-none hover:tw-bg-gray-100 disabled:tw-cursor-not-allowed disabled:hover:tw-bg-transparent dark:hover:tw-bg-gray-700"
				[disabled]="disabled()"
				[matTooltip]="dropdownTooltip()"
				(click)="toggleDropdown()">
				<mat-icon
					class="!tw-h-4 !tw-w-4 !tw-text-[16px] !tw-leading-[16px]">
					arrow_drop_down
				</mat-icon>
			</button>
			@if (showDropdown()) {
				<div
					class="tw-absolute tw-left-0 tw-top-full tw-z-20 tw-mt-0.5 tw-flex tw-flex-col tw-divide-y tw-divide-gray-200 tw-overflow-hidden tw-rounded tw-border tw-border-gray-300 tw-bg-white tw-shadow-md dark:tw-divide-gray-600 dark:tw-border-gray-600 dark:tw-bg-osee-neutral-6">
					@for (option of options(); track option.value) {
						<button
							class="tw-flex tw-cursor-pointer tw-items-center tw-gap-3 tw-whitespace-nowrap tw-border-0 tw-bg-transparent tw-px-3 tw-py-2 tw-text-left tw-text-sm hover:tw-bg-gray-100 dark:tw-text-gray-200 dark:hover:tw-bg-gray-700"
							(click)="selectOption(option)">
							<mat-icon
								class="!tw-h-5 !tw-w-5 !tw-shrink-0 !tw-text-[20px] !tw-leading-[20px]">
								{{ option.icon }}
							</mat-icon>
							<span>{{ option.name }}</span>
						</button>
					}
				</div>
			}
		</span>
	`,
})
export class SplitButtonComponent {
	/** Material icon for the default action button */
	icon = input.required<string>();
	/** Tooltip for the default action */
	tooltip = input('');
	/** Tooltip for the dropdown arrow */
	dropdownTooltip = input('More Options');
	/** Whether the split button is disabled */
	disabled = input(false);
	/** Options shown in the dropdown */
	options = input.required<SplitButtonOption[]>();

	/** Emitted when the main icon button is clicked */
	defaultAction = output<void>();
	/** Emitted when a dropdown option is selected */
	optionSelected = output<string>();

	protected readonly showDropdown = signal(false);

	private readonly elementRef = inject(ElementRef);
	private readonly destroyRef = inject(DestroyRef);

	private readonly _closeOnOutsideClick = (() => {
		fromEvent<MouseEvent>(document, 'mousedown')
			.pipe(
				takeUntilDestroyed(this.destroyRef),
				filter(() => this.showDropdown())
			)
			.subscribe((event) => {
				const clickedInside = this.elementRef.nativeElement.contains(
					event.target as Node
				);
				if (!clickedInside) {
					this.showDropdown.set(false);
				}
			});

		fromEvent(window, 'blur')
			.pipe(takeUntilDestroyed(this.destroyRef))
			.subscribe(() => {
				this.showDropdown.set(false);
			});
	})();

	protected toggleDropdown(): void {
		this.showDropdown.update((v) => !v);
	}

	protected selectOption(option: SplitButtonOption): void {
		this.optionSelected.emit(option.value);
		this.showDropdown.set(false);
	}
}
