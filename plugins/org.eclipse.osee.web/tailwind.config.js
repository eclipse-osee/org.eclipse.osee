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
/** @type {import('tailwindcss').Config} */
module.exports = {
	content: [
		'./src/**/*.{html,ts}',
		'!./node_modules/**/*',
		'!./**/_theme.scss',
	],
	prefix: 'tw-',
	important: true,
	theme: {
		extend: {
			colors: {
				primary: {
					50: {
						DEFAULT: 'var(--mat-primary-50)',
						contrast: 'var(--mat-primary-50-contrast)',
					},
					100: {
						DEFAULT: 'var(--mat-primary-100)',
						contrast: 'var(--mat-primary-100-contrast)',
					},
					200: {
						DEFAULT: 'var(--mat-primary-200)',
						contrast: 'var(--mat-primary-200-contrast)',
					},
					300: {
						DEFAULT: 'var(--mat-primary-300)',
						contrast: 'var(--mat-primary-300-contrast)',
					},
					400: {
						DEFAULT: 'var(--mat-primary-400)',
						contrast: 'var(--mat-primary-400-contrast)',
					},
					500: {
						DEFAULT: 'var(--mat-primary-500)',
						contrast: 'var(--mat-primary-500-contrast)',
					},
					600: {
						DEFAULT: 'var(--mat-primary-600)',
						contrast: 'var(--mat-primary-600-contrast)',
					},
					700: {
						DEFAULT: 'var(--mat-primary-700)',
						contrast: 'var(--mat-primary-700-contrast)',
					},
					800: {
						DEFAULT: 'var(--mat-primary-800)',
						contrast: 'var(--mat-primary-800-contrast)',
					},
					900: {
						DEFAULT: 'var(--mat-primary-900)',
						contrast: 'var(--mat-primary-900-contrast)',
					},
					a100: {
						DEFAULT: 'var(--mat-primary-a100)',
						contrast: 'var(--mat-primary-a100-contrast)',
					},
					a200: {
						DEFAULT: 'var(--mat-primary-a200)',
						contrast: 'var(--mat-primary-a200-contrast)',
					},
					a400: {
						DEFAULT: 'var(--mat-primary-a400)',
						contrast: 'var(--mat-primary-a400-contrast)',
					},
					a700: {
						DEFAULT: 'var(--mat-primary-a700)',
						contrast: 'var(--mat-primary-a700-contrast)',
					},
					DEFAULT: {
						DEFAULT: 'var(--mat-primary-default)',
						contrast: 'var(--mat-primary-default-contrast)',
					},
					lighter: {
						DEFAULT: 'var(--mat-primary-lighter)',
						contrast: 'var(--mat-primary-lighter-contrast)',
					},
					darker: {
						DEFAULT: 'var(--mat-primary-darker)',
						contrast: 'var(--mat-primary-darker-contrast)',
					},
				},
				accent: {
					50: {
						DEFAULT: 'var(--mat-accent-50)',
						contrast: 'var(--mat-accent-50-contrast)',
					},
					100: {
						DEFAULT: 'var(--mat-accent-100)',
						contrast: 'var(--mat-accent-100-contrast)',
					},
					200: {
						DEFAULT: 'var(--mat-accent-200)',
						contrast: 'var(--mat-accent-200-contrast)',
					},
					300: {
						DEFAULT: 'var(--mat-accent-300)',
						contrast: 'var(--mat-accent-300-contrast)',
					},
					400: {
						DEFAULT: 'var(--mat-accent-400)',
						contrast: 'var(--mat-accent-400-contrast)',
					},
					500: {
						DEFAULT: 'var(--mat-accent-500)',
						contrast: 'var(--mat-accent-500-contrast)',
					},
					600: {
						DEFAULT: 'var(--mat-accent-600)',
						contrast: 'var(--mat-accent-600-contrast)',
					},
					700: {
						DEFAULT: 'var(--mat-accent-700)',
						contrast: 'var(--mat-accent-700-contrast)',
					},
					800: {
						DEFAULT: 'var(--mat-accent-800)',
						contrast: 'var(--mat-accent-800-contrast)',
					},
					900: {
						DEFAULT: 'var(--mat-accent-900)',
						contrast: 'var(--mat-accent-900-contrast)',
					},
					a100: {
						DEFAULT: 'var(--mat-accent-a100)',
						contrast: 'var(--mat-accent-a100-contrast)',
					},
					a200: {
						DEFAULT: 'var(--mat-accent-a200)',
						contrast: 'var(--mat-accent-a200-contrast)',
					},
					a400: {
						DEFAULT: 'var(--mat-accent-a400)',
						contrast: 'var(--mat-accent-a400-contrast)',
					},
					a700: {
						DEFAULT: 'var(--mat-accent-a700)',
						contrast: 'var(--mat-accent-a700-contrast)',
					},
					DEFAULT: {
						DEFAULT: 'var(--mat-accent-default)',
						contrast: 'var(--mat-accent-default-contrast)',
					},
					lighter: {
						DEFAULT: 'var(--mat-accent-lighter)',
						contrast: 'var(--mat-accent-lighter-contrast)',
					},
					darker: {
						DEFAULT: 'var(--mat-accent-darker)',
						contrast: 'var(--mat-accent-darker-contrast)',
					},
				},
				warning: {
					50: {
						DEFAULT: 'var(--mat-warning-50)',
						contrast: 'var(--mat-warning-50-contrast)',
					},
					100: {
						DEFAULT: 'var(--mat-warning-100)',
						contrast: 'var(--mat-warning-100-contrast)',
					},
					200: {
						DEFAULT: 'var(--mat-warning-200)',
						contrast: 'var(--mat-warning-200-contrast)',
					},
					300: {
						DEFAULT: 'var(--mat-warning-300)',
						contrast: 'var(--mat-warning-300-contrast)',
					},
					400: {
						DEFAULT: 'var(--mat-warning-400)',
						contrast: 'var(--mat-warning-400-contrast)',
					},
					500: {
						DEFAULT: 'var(--mat-warning-500)',
						contrast: 'var(--mat-warning-500-contrast)',
					},
					600: {
						DEFAULT: 'var(--mat-warning-600)',
						contrast: 'var(--mat-warning-600-contrast)',
					},
					700: {
						DEFAULT: 'var(--mat-warning-700)',
						contrast: 'var(--mat-warning-700-contrast)',
					},
					800: {
						DEFAULT: 'var(--mat-warning-800)',
						contrast: 'var(--mat-warning-800-contrast)',
					},
					900: {
						DEFAULT: 'var(--mat-warning-900)',
						contrast: 'var(--mat-warning-900-contrast)',
					},
					a100: {
						DEFAULT: 'var(--mat-warning-a100)',
						contrast: 'var(--mat-warning-a100-contrast)',
					},
					a200: {
						DEFAULT: 'var(--mat-warning-a200)',
						contrast: 'var(--mat-warning-a200-contrast)',
					},
					a400: {
						DEFAULT: 'var(--mat-warning-a400)',
						contrast: 'var(--mat-warning-a400-contrast)',
					},
					a700: {
						DEFAULT: 'var(--mat-warning-a700)',
						contrast: 'var(--mat-warning-a700-contrast)',
					},
					DEFAULT: {
						DEFAULT: 'var(--mat-warning-default)',
						contrast: 'var(--mat-warning-default-contrast)',
					},
					lighter: {
						DEFAULT: 'var(--mat-warning-lighter)',
						contrast: 'var(--mat-warning-lighter-contrast)',
					},
					darker: {
						DEFAULT: 'var(--mat-warning-darker)',
						contrast: 'var(--mat-warning-darker-contrast)',
					},
				},
				success: {
					50: {
						DEFAULT: 'var(--mat-success-50)',
						contrast: 'var(--mat-success-50-contrast)',
					},
					100: {
						DEFAULT: 'var(--mat-success-100)',
						contrast: 'var(--mat-success-100-contrast)',
					},
					200: {
						DEFAULT: 'var(--mat-success-200)',
						contrast: 'var(--mat-success-200-contrast)',
					},
					300: {
						DEFAULT: 'var(--mat-success-300)',
						contrast: 'var(--mat-success-300-contrast)',
					},
					400: {
						DEFAULT: 'var(--mat-success-400)',
						contrast: 'var(--mat-success-400-contrast)',
					},
					500: {
						DEFAULT: 'var(--mat-success-500)',
						contrast: 'var(--mat-success-500-contrast)',
					},
					600: {
						DEFAULT: 'var(--mat-success-600)',
						contrast: 'var(--mat-success-600-contrast)',
					},
					700: {
						DEFAULT: 'var(--mat-success-700)',
						contrast: 'var(--mat-success-700-contrast)',
					},
					800: {
						DEFAULT: 'var(--mat-success-800)',
						contrast: 'var(--mat-success-800-contrast)',
					},
					900: {
						DEFAULT: 'var(--mat-success-900)',
						contrast: 'var(--mat-success-900-contrast)',
					},
					a100: {
						DEFAULT: 'var(--mat-success-a100)',
						contrast: 'var(--mat-success-a100-contrast)',
					},
					a200: {
						DEFAULT: 'var(--mat-success-a200)',
						contrast: 'var(--mat-success-a200-contrast)',
					},
					a400: {
						DEFAULT: 'var(--mat-success-a400)',
						contrast: 'var(--mat-success-a400-contrast)',
					},
					a700: {
						DEFAULT: 'var(--mat-success-a700)',
						contrast: 'var(--mat-success-a700-contrast)',
					},
					DEFAULT: {
						DEFAULT: 'var(--mat-success-default)',
						contrast: 'var(--mat-success-default-contrast)',
					},
					lighter: {
						DEFAULT: 'var(--mat-success-lighter)',
						contrast: 'var(--mat-success-lighter-contrast)',
					},
					darker: {
						DEFAULT: 'var(--mat-success-darker)',
						contrast: 'var(--mat-success-darker-contrast)',
					},
				},
				foreground: {
					base: 'var(--mat-foreground-base)',
					divider: 'var(--mat-foreground-divider)',
					dividers: 'var(--mat-foreground-dividers)',
					disabled: {
						DEFAULT: 'var(--mat-foreground-disabled)',
						button: 'var(--mat-foreground-disabled-button)',
						text: 'var(--mat-foreground-disabled-text)',
					},
					elevation: 'var(--mat-foreground-elevation)',
					hint: {
						text: 'var(--mat-foreground-hint-text)',
					},
					secondary: {
						text: 'var(--mat-foreground-secondary-text)',
					},
					icon: 'var(--mat-foreground-icon)',
					icons: 'var(--mat-foreground-icons)',
					text: 'var(--mat-foreground-text)',
					slider: {
						min: 'var(--mat-foreground-slider-min)',
						off: {
							DEFAULT: 'var(--mat-foreground-slider-off)',
							active: 'var(--mat-foreground-slider-off-active)',
						},
					},
					DEFAULT: 'var(--mat-foreground-base)',
				},
				background: {
					status: {
						bar: 'var(--mat-background-status-bar)',
					},
					app: {
						bar: 'var(--mat-background-app-bar)',
					},
					background: 'var(--mat-background-background)',
					hover: 'var(--mat-background-hover)',
					card: 'var(--mat-background-card)',
					dialog: 'var(--mat-background-dialog)',
					disabled: {
						button: {
							DEFAULT: 'var(--mat-background-disabled-button)',
							toggle: 'var(--mat-background-disabled-button-toggle)',
						},
						list: {
							option: 'var(--mat-background-disabled-list-option)',
						},
					},
					raised: {
						button: 'var(--mat-background-raised-button)',
					},
					focused: {
						button: 'var(--mat-background-focused-button)',
					},
					selected: {
						button: 'var(--mat-background-selected-button)',
						disabled: {
							button: 'var(--mat-background-selected-disabled-button)',
						},
					},
					unselected: {
						chip: 'var(--mat-background-unselected-chip)',
					},
					tooltip: 'var(--mat-background-tooltip)',
					DEFAULT: 'var(--mat-background-background)',
				},
			},
		},
	},
	plugins: [],
};
