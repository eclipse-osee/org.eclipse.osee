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
	inject,
	input,
	output,
	signal,
} from '@angular/core';
import {
	CdkConnectedOverlay,
	CdkOverlayOrigin,
	ConnectedPosition,
	Overlay,
	OverlayContainer,
	FullscreenOverlayContainer,
	ScrollStrategy,
} from '@angular/cdk/overlay';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';

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
 * arrow that reveals additional options. Uses CDK overlay with
 * FullscreenOverlayContainer so it works inside fullscreen elements
 * and properly positions/sizes the dropdown.
 *
 * The outline and divider only appear on hover for a clean toolbar look.
 */
@Component({
	selector: 'osee-split-button',
	standalone: true,
	imports: [MatIcon, MatTooltip, CdkConnectedOverlay, CdkOverlayOrigin],
	providers: [
		{ provide: OverlayContainer, useClass: FullscreenOverlayContainer },
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		<span
			cdkOverlayOrigin
			#trigger="cdkOverlayOrigin"
			class="tw-group tw-relative tw-inline-flex tw-items-stretch tw-overflow-visible tw-rounded tw-border tw-border-transparent hover:tw-border-osee-neutral-80 dark:hover:tw-border-osee-neutral-40"
			[class.tw-opacity-40]="disabled()">
			<button
				class="tw-flex tw-cursor-pointer tw-items-center tw-justify-center tw-border-0 tw-bg-transparent tw-p-1 tw-outline-none hover:tw-bg-osee-neutral-90 disabled:tw-cursor-not-allowed disabled:hover:tw-bg-transparent dark:hover:tw-bg-osee-neutral-30"
				[disabled]="disabled()"
				[matTooltip]="tooltip()"
				(click)="defaultAction.emit()">
				<mat-icon
					class="!tw-h-5 !tw-w-5 !tw-text-[20px] !tw-leading-[20px]">
					{{ icon() }}
				</mat-icon>
			</button>
			<span
				class="tw-w-px tw-self-stretch tw-bg-transparent group-hover:tw-bg-osee-neutral-80 dark:group-hover:tw-bg-osee-neutral-40"></span>
			<button
				class="tw-flex tw-w-4 tw-cursor-pointer tw-items-center tw-justify-center tw-border-0 tw-bg-transparent tw-p-0 tw-outline-none hover:tw-bg-osee-neutral-90 disabled:tw-cursor-not-allowed disabled:hover:tw-bg-transparent dark:hover:tw-bg-osee-neutral-30"
				[disabled]="disabled()"
				[matTooltip]="dropdownTooltip()"
				(mousedown)="$event.preventDefault()"
				(click)="toggleDropdown()">
				<mat-icon
					class="!tw-h-4 !tw-w-4 !tw-text-[16px] !tw-leading-[16px]">
					arrow_drop_down
				</mat-icon>
			</button>
		</span>

		<ng-template
			cdkConnectedOverlay
			[cdkConnectedOverlayOrigin]="trigger"
			[cdkConnectedOverlayOpen]="showDropdown()"
			[cdkConnectedOverlayPositions]="positions"
			[cdkConnectedOverlayScrollStrategy]="scrollStrategy"
			[cdkConnectedOverlayHasBackdrop]="false"
			cdkConnectedOverlayFlexibleDimensions
			(overlayOutsideClick)="closeDropdown()"
			(detach)="closeDropdown()"
			(window:resize)="closeDropdown()">
			<div
				class="tw-flex tw-flex-col tw-divide-y tw-divide-osee-neutral-90 tw-overflow-y-auto tw-rounded tw-border tw-border-osee-neutral-80 tw-bg-osee-neutral-100 tw-shadow-md dark:tw-divide-osee-neutral-40 dark:tw-border-osee-neutral-40 dark:tw-bg-osee-neutral-6">
				@for (option of options(); track option.value) {
					<button
						class="tw-flex tw-cursor-pointer tw-items-center tw-gap-3 tw-whitespace-nowrap tw-border-0 tw-bg-transparent tw-px-3 tw-py-2 tw-text-left tw-text-sm hover:tw-bg-osee-neutral-90 dark:tw-text-osee-neutral-80 dark:hover:tw-bg-osee-neutral-30"
						(click)="selectOption(option)">
						<mat-icon
							class="!tw-h-5 !tw-w-5 !tw-shrink-0 !tw-text-[20px] !tw-leading-[20px]">
							{{ option.icon }}
						</mat-icon>
						<span>{{ option.name }}</span>
					</button>
				}
			</div>
		</ng-template>
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

	private readonly destroyRef = inject(DestroyRef);
	private readonly overlay = inject(Overlay);
	protected readonly scrollStrategy: ScrollStrategy =
		this.overlay.scrollStrategies.close();

	private scrollHandler: (() => void) | null = null;

	protected toggleDropdown(): void {
		const opening = !this.showDropdown();
		this.showDropdown.set(opening);
		if (opening) {
			this.scrollHandler = () => this.showDropdown.set(false);
			document.addEventListener('scroll', this.scrollHandler, true);
		} else {
			this.removeScrollListener();
		}
	}

	protected selectOption(option: SplitButtonOption): void {
		this.optionSelected.emit(option.value);
		this.showDropdown.set(false);
		this.removeScrollListener();
	}

	private removeScrollListener(): void {
		if (this.scrollHandler) {
			document.removeEventListener('scroll', this.scrollHandler, true);
			this.scrollHandler = null;
		}
	}

	protected closeDropdown(): void {
		this.showDropdown.set(false);
		this.removeScrollListener();
	}

	private readonly _cleanup = (() => {
		this.destroyRef.onDestroy(() => {
			this.removeScrollListener();
		});
	})();

	protected readonly positions: ConnectedPosition[] = [
		{
			originX: 'start',
			originY: 'bottom',
			overlayX: 'start',
			overlayY: 'top',
		},
		{
			originX: 'start',
			originY: 'top',
			overlayX: 'start',
			overlayY: 'bottom',
		},
	];
}
