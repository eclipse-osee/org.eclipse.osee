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
 * An action displayed in the collapsed toolbar section dropdown.
 */
export type ToolbarDropdownAction = {
	/** Unique identifier for emitting on selection */
	id: string;
	/** Display label */
	name: string;
	/** Material icon name */
	icon: string;
	/** Whether this action is disabled */
	disabled?: boolean;
};

/**
 * A compact dropdown button representing a collapsed toolbar section.
 * Shows a single icon with a dropdown arrow; clicking opens a panel
 * listing all section actions with icons and labels.
 *
 * Uses CDK overlay with FullscreenOverlayContainer so it works
 * inside fullscreen elements.
 */
@Component({
	selector: 'osee-toolbar-section-dropdown',
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
			class="tw-inline-flex tw-items-stretch tw-rounded tw-border tw-border-transparent hover:tw-border-osee-neutral-80 dark:hover:tw-border-osee-neutral-40">
			<button
				class="tw-flex tw-cursor-pointer tw-items-center tw-justify-center tw-gap-0.5 tw-border-0 tw-bg-transparent tw-px-1 tw-py-1 tw-outline-none hover:tw-bg-osee-neutral-90 dark:hover:tw-bg-osee-neutral-30"
				[matTooltip]="sectionName()"
				(mousedown)="$event.preventDefault()"
				(click)="toggleDropdown()">
				<mat-icon
					class="!tw-h-5 !tw-w-5 !tw-text-[20px] !tw-leading-[20px]">
					{{ icon() }}
				</mat-icon>
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
				@for (action of actions(); track action.id) {
					<button
						class="tw-flex tw-cursor-pointer tw-items-center tw-gap-3 tw-whitespace-nowrap tw-border-0 tw-bg-transparent tw-px-3 tw-py-2 tw-text-left tw-text-sm hover:tw-bg-osee-neutral-90 disabled:tw-cursor-not-allowed disabled:tw-opacity-40 disabled:hover:tw-bg-transparent dark:tw-text-osee-neutral-80 dark:hover:tw-bg-osee-neutral-30"
						[disabled]="action.disabled ?? false"
						(click)="selectAction(action)">
						<mat-icon
							class="!tw-h-5 !tw-w-5 !tw-shrink-0 !tw-text-[20px] !tw-leading-[20px]">
							{{ action.icon }}
						</mat-icon>
						<span>{{ action.name }}</span>
					</button>
				}
			</div>
		</ng-template>
	`,
})
export class ToolbarSectionDropdownComponent {
	/** Representative icon for the collapsed section */
	icon = input.required<string>();
	/** Section name shown as tooltip */
	sectionName = input.required<string>();
	/** Actions shown in the dropdown */
	actions = input.required<ToolbarDropdownAction[]>();

	/** Emitted when a dropdown action is selected */
	actionSelected = output<string>();

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

	protected selectAction(action: ToolbarDropdownAction): void {
		if (action.disabled) {
			return;
		}
		this.actionSelected.emit(action.id);
		this.showDropdown.set(false);
		this.removeScrollListener();
	}

	protected closeDropdown(): void {
		this.showDropdown.set(false);
		this.removeScrollListener();
	}

	private removeScrollListener(): void {
		if (this.scrollHandler) {
			document.removeEventListener('scroll', this.scrollHandler, true);
			this.scrollHandler = null;
		}
	}

	private readonly _cleanup = this.destroyRef.onDestroy(() => {
		this.removeScrollListener();
	});

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
