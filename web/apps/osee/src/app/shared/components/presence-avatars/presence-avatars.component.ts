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
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	input,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { presenceUser } from '@osee/shared/services';

/**
 * A deterministic generic avatar: a Material icon glyph on a colored circle,
 * both chosen by a stable hash of the user's id. Independent of the (unstructured)
 * display name, so it stays consistent for a given user and never mis-parses names
 * like "Campanella, Zachary A".
 */
type presenceAvatar = {
	user: presenceUser;
	/** Material Symbols glyph name. */
	icon: string;
};

/**
 * Curated set of cute, friendly animal glyphs — intentionally light and positive, with nothing
 * that reads as an error/alert. Assigned to a user by `hash(userId) % icons.length`. These are
 * Material SYMBOLS glyphs (richer animal set than classic Material Icons), so the avatar renders
 * them with `fontSet="material-symbols-outlined"` (see the template + icons.css). Keep this list
 * to names that exist in Material Symbols so none render as a missing-glyph box.
 */
const AVATAR_ICONS: readonly string[] = [
	'pets', // paw print
	'cruelty_free', // bunny
	'owl', // owl
	'raven', // raven
	'snail', // snail
	'sound_detection_dog_barking', // dog
	'pest_control_rodent', // mouse
	'emoji_nature', // bee
	'chess_knight', // horse (knight)
	'pet_supplies', // pet bowl
	'footprint', // paw/footprint
	'cookie', // cookie
	'icecream', // ice cream
	'snowflake', // snowflake
	'local_florist', // flower bouquet
	'potted_plant', // potted plant
];

/**
 * Reusable presence avatar stack component.
 * Displays overlapping generic user avatars with a "+N" overflow indicator
 * when there are more users than `maxVisible`. Each avatar is a deterministic
 * icon+color derived from the user's id; the full name is shown on hover.
 *
 * Usage:
 * ```html
 * <osee-presence-avatars [users]="presence.users()" />
 * <osee-presence-avatars [users]="presence.users()" [maxVisible]="3" />
 * ```
 */
@Component({
	selector: 'osee-presence-avatars',
	standalone: true,
	imports: [MatIcon, MatTooltip],
	changeDetection: ChangeDetectionStrategy.OnPush,
	template: `
		@if (users().length > 0) {
			<div class="tw-flex tw-items-center">
				@for (avatar of visibleAvatars(); track avatar.user.userId) {
					<span
						class="-tw-ml-3 tw-flex tw-size-6 tw-items-center tw-justify-center tw-rounded-full tw-border-2 tw-border-background-background tw-bg-primary tw-text-background-background first:tw-ml-0"
						[matTooltip]="
							avatar.user.userName + ' is also viewing this.'
						">
						<mat-icon
							fontSet="material-symbols-outlined"
							class="tw-size-4 tw-text-base tw-leading-4"
							>{{ avatar.icon }}</mat-icon
						>
					</span>
				}
				@if (overflowCount() > 0) {
					<span
						class="-tw-ml-3 tw-flex tw-size-6 tw-items-center tw-justify-center tw-rounded-full tw-border-2 tw-border-background-background tw-bg-background-hover tw-text-[0.5625rem] tw-font-bold tw-text-foreground-base"
						[matTooltip]="overflowTooltip()">
						+{{ overflowCount() }}
					</span>
				}
			</div>
		}
	`,
})
export class PresenceAvatarsComponent {
	/** The list of other users to display. */
	users = input.required<presenceUser[]>();

	/** Maximum number of avatar circles to show before "+N". */
	maxVisible = input(3);

	protected visibleAvatars = computed<presenceAvatar[]>(() =>
		this.users()
			.slice(0, this.maxVisible())
			.map((user) => this.toAvatar(user))
	);

	protected overflowCount = computed(() =>
		Math.max(0, this.users().length - this.maxVisible())
	);

	/** Max overflow names to spell out in the tooltip before summarizing the remainder. */
	private static readonly MAX_TOOLTIP_NAMES = 10;

	protected overflowTooltip = computed(() => {
		const overflow = this.users().slice(this.maxVisible());
		const shown = overflow
			.slice(0, PresenceAvatarsComponent.MAX_TOOLTIP_NAMES)
			.map((u) => u.userName);
		const remaining = overflow.length - shown.length;
		const names =
			remaining > 0
				? `${shown.join(', ')} and ${remaining} more`
				: shown.join(', ');
		return `Also viewing: ${names}`;
	});

	/** Builds the deterministic avatar (a fixed-set icon) for a user from its stable id. */
	private toAvatar(user: presenceUser): presenceAvatar {
		const hash = stableHash(user.userId);
		return {
			user,
			icon: AVATAR_ICONS[hash % AVATAR_ICONS.length],
		};
	}
}

/**
 * Small, stable, non-cryptographic string hash (djb2). Deterministic across
 * sessions/tabs so a given userId always maps to the same avatar. Returns a
 * non-negative 32-bit integer.
 */
function stableHash(value: string): number {
	let hash = 5381;
	for (let i = 0; i < value.length; i++) {
		// hash * 33 + charCode, kept in 32-bit space.
		hash = ((hash << 5) + hash + value.charCodeAt(i)) | 0;
	}
	return hash >>> 0;
}
