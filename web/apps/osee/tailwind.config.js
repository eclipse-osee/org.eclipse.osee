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
let plugin = require('tailwindcss/plugin');
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
				osee: {
					// NOTE: KEEP THIS SECTION ALIGNED WITH osee-variables.css
					// css variables aren't used so intellisense shows previews of the colors. :)
					blue: {
						0: '#000000',
						1: '#00006e',
						2: '#002d6e',
						3: '#003784',
						4: '#00429b',
						5: '#004db2',
						6: '#0058ca',
						7: '#1770f6',
						8: '#558dff',
						9: '#85a9ff',
						10: '#b0c6ff',
						11: '#d9e2ff',
						12: '#edf0ff',
						13: '#faf8ff',
						14: '#fefbff',
						15: '#ffffff',
						16: '#e3f2fd',
						17: '#bbdefb',
						18: '#90caf9',
						19: '#64b5f6',
						20: '#42a5f5',
						21: '#2196f3',
						22: '#1e88e5',
						23: '#1976d2',
						24: '#1565c0',
						25: '#0d47a1',
						26: '#82b1ff',
						27: '#448aff',
						28: '#2979ff',
						29: '#2962ff',
					},
					amber: {
						0: '#000000',
						1: '#251a00',
						2: '#3f2e00',
						3: '#4c3800',
						4: '#5a4300',
						5: '#684f00',
						6: '#775a00',
						7: '#967200',
						8: '#b58a00',
						9: '#d6a400',
						10: '#f7be00',
						11: '#ffdf99',
						12: '#ffefd2',
						13: '#fff8f2',
						14: '#fffbff',
						15: '#ffffff',
						16: '#ffd54f',
						17: '#ffca28',
						18: '#ffc107',
						19: '#ffb300',
						20: '#ffc400',
						21: '#ffd740',
						22: '#ffab00',
						23: '#ffe57f',
						24: '#ff6f00',
						25: '#ff8f00',
						26: '#ffa000',
						27: '#ffe082',
						28: '#ffecb3',
						29: '#fff8e1',
					},
					yellow: {
						0: '#000000',
						1: '#1f1c00',
						2: '#363100',
						3: '#423c00',
						4: '#4f4800',
						5: '#5c5300',
						6: '#695f00',
						7: '#847800',
						8: '#a09200',
						9: '#bdad00',
						10: '#dbc900',
						11: '#fae500',
						12: '#fff39b',
						13: '#fff9e7',
						14: '#fffbff',
						15: '#ffffff',
						16: '#fffde7',
						17: '#fff9c4',
						18: '#fff59d',
						19: '#fff176',
						20: '#ffee58',
						21: '#ffeb3b',
						22: '#fdd835',
						23: '#fbc02d',
						24: '#f9a825',
						25: '#f57f17',
						26: '#ffff8d',
						27: '#ffff00',
						28: '#ffea00',
						29: '#ffd600',
					},
					green: {
						0: '#000000',
						1: '#00210b',
						2: '#003918',
						3: '#00451f',
						4: '#005226',
						5: '#00602d',
						6: '#006d35',
						7: '#008944',
						8: '#00a754',
						9: '#00c564',
						10: '#00e475',
						11: '#62ff96',
						12: '#c4ffcc',
						13: '#eaffe9',
						14: '#f5fff2',
						15: '#ffffff',
						16: '#e8f5e9',
						17: '#c8e6c9',
						18: '#a5d6a7',
						19: '#81c784',
						20: '#66bb6a',
						21: '#4caf50',
						22: '#43a047',
						23: '#388e3c',
						24: '#2e7d32',
						25: '#1b5e20',
						26: '#b9f6ca',
						27: '#69f0ae',
						28: '#00e676',
						29: '#00c853',
					},
					red: {
						0: '#000000',
						1: '#410002',
						2: '#690005',
						3: '#7e0007',
						4: '#93000a',
						5: '#a80710',
						6: '#ba1a1a',
						7: '#de3730',
						8: '#ff5449',
						9: '#ff897d',
						10: '#ffb4ab',
						11: '#ffdad6',
						12: '#ffedea',
						13: '#fff8f7',
						14: '#fffbff',
						15: '#ffffff',
						16: '#ffebee',
						17: '#ffcdd2',
						18: '#ef9a9a',
						19: '#e57373',
						20: '#ef5350',
						21: '#f44336',
						22: '#e53935',
						23: '#d32f2f',
						24: '#c62828',
						25: '#b71c1c',
						26: '#ff8a80',
						27: '#ff5252',
						28: '#ff1744',
						29: '#d50000',
					},
					neutral: {
						0: '#000000',
						10: '#1b1b1f',
						20: '#303034',
						25: '#3b3b3f',
						30: '#46464a',
						35: '#525256',
						40: '#5e5e62',
						50: '#77777a',
						60: '#919094',
						70: '#acabaf',
						80: '#c7c6ca',
						90: '#e3e2e6',
						95: '#f2f0f4',
						98: '#fbf8fd',
						99: '#fefbff',
						100: '#ffffff',
						4: '#0d0e11',
						6: '#121316',
						12: '#1f1f23',
						17: '#292a2d',
						22: '#343438',
						24: '#39393c',
						87: '#dbd9dd',
						92: '#e9e7ec',
						94: '#efedf1',
						96: '#f5f3f7',
						variant: {
							0: '#000000',
							10: '#191b23',
							20: '#2e3038',
							25: '#393b43',
							30: '#44464f',
							35: '#50525a',
							40: '#5c5e67',
							50: '#757780',
							60: '#8f9099',
							70: '#a9abb4',
							80: '#c5c6d0',
							90: '#e1e2ec',
							95: '#eff0fa',
							98: '#faf8ff',
							99: '#fefbff',
							100: '#ffffff',
						},
					},
					light: {
						blue: {
							0: '#000000',
							1: '#001e2b',
							2: '#003548',
							3: '#004058',
							4: '#004d67',
							5: '#005977',
							6: '#006688',
							7: '#0080aa',
							8: '#009cce',
							9: '#3fb7ea',
							10: '#75d1ff',
							11: '#c2e8ff',
							12: '#e2f3ff',
							13: '#f5faff',
							14: '#fbfcff',
							15: '#ffffff',
							16: '#e1f5fe',
							17: '#b3e5fc',
							18: '#81d4fa',
							19: '#4fc3f7',
							20: '#29b6f6',
							21: '#03a9f4',
							22: '#039be5',
							23: '#0288d1',
							24: '#0277bd',
							25: '#01579b',
							26: '#80d8ff',
							27: '#40c4ff',
							28: '#00b0ff',
							29: '#0091ea',
						},
					},
				},
				primary: {
					50: {
						DEFAULT: 'var(--osee-primary-16)',
						contrast: 'rgba(black, 0.87)',
					},
					100: {
						DEFAULT: 'var(--osee-primary-17)',
						contrast: 'rgba(black, 0.87)',
					},
					200: {
						DEFAULT: 'var(--osee-primary-18)',
						contrast: 'rgba(black, 0.87)',
					},
					300: {
						DEFAULT: 'var(--osee-primary-19)',
						contrast: 'rgba(black, 0.87)',
					},
					400: {
						DEFAULT: 'var(--osee-primary-20)',
						contrast: 'rgba(black, 0.87)',
					},
					500: {
						DEFAULT: 'var(--osee-primary-21)',
						contrast: 'white',
					},
					600: {
						DEFAULT: 'var(--osee-primary-22)',
						contrast: 'white',
					},
					700: {
						DEFAULT: 'var(--osee-primary-23)',
						contrast: 'white',
					},
					800: {
						DEFAULT: 'var(--osee-primary-24)',
						contrast: 'white',
					},
					900: {
						DEFAULT: 'var(--osee-primary-25)',
						contrast: 'white',
					},
					a100: {
						DEFAULT: 'var(--osee-primary-26)',
						contrast: 'white',
					},
					a200: {
						DEFAULT: 'var(--osee-primary-27)',
						contrast: 'white',
					},
					a400: {
						DEFAULT: 'var(--osee-primary-28)',
						contrast: 'white',
					},
					a700: {
						DEFAULT: 'var(--osee-primary-29)',
						contrast: 'white',
					},
					DEFAULT: {
						DEFAULT: 'var(--osee-primary-default)',
						contrast: 'white',
					},
					lighter: {
						DEFAULT: 'var(--osee-primary-lighter)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					darker: {
						DEFAULT: 'var(--osee-primary-darker)',
						contrast: 'white',
					},
				},
				accent: {
					50: {
						DEFAULT: 'var(--osee-secondary-16)',
						contrast: 'rgba(black, 0.87)',
					},
					100: {
						DEFAULT: 'var(--osee-secondary-17)',
						contrast: 'rgba(black, 0.87)',
					},
					200: {
						DEFAULT: 'var(--osee-secondary-18)',
						contrast: 'rgba(black, 0.87)',
					},
					300: {
						DEFAULT: 'var(--osee-secondary-19)',
						contrast: 'rgba(black, 0.87)',
					},
					400: {
						DEFAULT: 'var(--osee-secondary-20)',
						contrast: 'rgba(black, 0.87)',
					},
					500: {
						DEFAULT: 'var(--osee-secondary-21)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					600: {
						DEFAULT: 'var(--osee-secondary-22)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					700: {
						DEFAULT: 'var(--osee-secondary-23)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					800: {
						DEFAULT: 'var(--osee-secondary-24)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					900: {
						DEFAULT: 'var(--osee-secondary-25)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					a100: {
						DEFAULT: 'var(--osee-secondary-26)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					a200: {
						DEFAULT: 'var(--osee-secondary-27)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					a400: {
						DEFAULT: 'var(--osee-secondary-28)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					a700: {
						DEFAULT: 'var(--osee-secondary-29)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					DEFAULT: {
						DEFAULT: 'var(--osee-secondary-default)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					lighter: {
						DEFAULT: 'var(--osee-secondary-lighter)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					darker: {
						DEFAULT: 'var(--osee-secondary-darker)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
				},
				warning: {
					50: {
						DEFAULT: 'var(--osee-red-16)',
						contrast: 'rgba(black, 0.87)',
					},
					100: {
						DEFAULT: 'var(--osee-red-17)',
						contrast: 'rgba(black, 0.87)',
					},
					200: {
						DEFAULT: 'var(--osee-red-18)',
						contrast: 'rgba(black, 0.87)',
					},
					300: {
						DEFAULT: 'var(--osee-red-19)',
						contrast: 'rgba(black, 0.87)',
					},
					400: {
						DEFAULT: 'var(--osee-red-20)',
						contrast: 'rgba(black, 0.87)',
					},
					500: {
						DEFAULT: 'var(--osee-red-21)',
						contrast: 'white',
					},
					600: {
						DEFAULT: 'var(--osee-red-22)',
						contrast: 'white',
					},
					700: {
						DEFAULT: 'var(--osee-red-23)',
						contrast: 'white',
					},
					800: {
						DEFAULT: 'var(--osee-red-24)',
						contrast: 'white',
					},
					900: {
						DEFAULT: 'var(--osee-red-25)',
						contrast: 'white',
					},
					a100: {
						DEFAULT: 'var(--osee-red-26)',
						contrast: 'white',
					},
					a200: {
						DEFAULT: 'var(--osee-red-27)',
						contrast: 'white',
					},
					a400: {
						DEFAULT: 'var(--osee-red-28)',
						contrast: 'white',
					},
					a700: {
						DEFAULT: 'var(--osee-red-29)',
						contrast: 'white',
					},
					DEFAULT: {
						DEFAULT: 'var(--osee-red-28)',
						contrast: 'white',
					},
					lighter: {
						DEFAULT: 'var(--osee-red-20)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					darker: {
						DEFAULT: 'var(--osee-red-25)',
						contrast: 'white',
					},
				},
				success: {
					50: {
						DEFAULT: 'var(--osee-green-16)',
						contrast: 'rgba(black, 0.87)',
					},
					100: {
						DEFAULT: 'var(--osee-green-17)',
						contrast: 'rgba(black, 0.87)',
					},
					200: {
						DEFAULT: 'var(--osee-green-18)',
						contrast: 'rgba(black, 0.87)',
					},
					300: {
						DEFAULT: 'var(--osee-green-19)',
						contrast: 'rgba(black, 0.87)',
					},
					400: {
						DEFAULT: 'var(--osee-green-20)',
						contrast: 'rgba(black, 0.87)',
					},
					500: {
						DEFAULT: 'var(--osee-green-21)',
						contrast: 'rgba(black, 0.87)',
					},
					600: {
						DEFAULT: 'var(--osee-green-22)',
						contrast: 'white',
					},
					700: {
						DEFAULT: 'var(--osee-green-23)',
						contrast: 'white',
					},
					800: {
						DEFAULT: 'var(--osee-green-24)',
						contrast: 'white',
					},
					900: {
						DEFAULT: 'var(--osee-green-25)',
						contrast: 'white',
					},
					a100: {
						DEFAULT: 'var(--osee-green-26)',
						contrast: 'rgba(black, 0.87)',
					},
					a200: {
						DEFAULT: 'var(--osee-green-27)',
						contrast: 'rgba(black, 0.87)',
					},
					a400: {
						DEFAULT: 'var(--osee-green-28)',
						contrast: 'rgba(black, 0.87)',
					},
					a700: {
						DEFAULT: 'var(--osee-green-29)',
						contrast: 'rgba(black, 0.87)',
					},
					DEFAULT: {
						DEFAULT: 'var(--osee-green-28)',
						contrast: 'rgba(black, 0.87)',
					},
					lighter: {
						DEFAULT: 'var(--osee-green-20)',
						contrast: 'rgba(0, 0, 0, 0.87)',
					},
					darker: {
						DEFAULT: 'var(--osee-green-25)',
						contrast: 'white',
					},
				},
				foreground: {
					base: 'var(--osee-foreground-base)',
					divider: 'var(--osee-foreground-divider)',
					dividers: 'var(--osee-foreground-dividers)',
					disabled: {
						DEFAULT: 'var(--osee-foreground-disabled)',
						button: 'var(--osee-foreground-disabled-button)',
						text: 'var(--osee-foreground-disabled-text)',
					},
					elevation: 'var(--osee-foreground-elevation)',
					hint: {
						text: 'var(--osee-foreground-hint-text)',
					},
					secondary: {
						text: 'var(--osee-foreground-secondary-text)',
					},
					icon: 'var(--osee-foreground-icon)',
					icons: 'var(--osee-foreground-icons)',
					text: 'var(--osee-foreground-text)',
					slider: {
						min: 'var(--osee-foreground-slider-min)',
						off: {
							DEFAULT: 'var(--osee-foreground-slider-off)',
							active: 'var(--osee-foreground-slider-off-active)',
						},
					},
					DEFAULT: 'var(--osee-foreground-base)',
				},
				background: {
					status: {
						bar: 'var(--osee-background-status-bar)',
					},
					app: {
						bar: 'var(--osee-background-app-bar)',
					},
					background: 'var(--osee-background-background)',
					hover: 'var(--osee-background-hover)',
					card: 'var(--osee-background-card)',
					dialog: 'var(--osee-background-dialog)',
					disabled: {
						button: {
							DEFAULT: 'var(--osee-background-disabled-button)',
							toggle: 'var(--osee-background-disabled-button-toggle)',
						},
						list: {
							option: 'var(--osee-background-disabled-list-option)',
						},
					},
					raised: {
						button: 'var(--osee-background-raised-button)',
					},
					focused: {
						button: 'var(--osee-background-focused-button)',
					},
					selected: {
						button: 'var(--osee-background-selected-button)',
						disabled: {
							button: 'var(--osee-background-selected-disabled-button)',
						},
					},
					unselected: {
						chip: 'var(--osee-background-unselected-chip)',
					},
					tooltip: 'var(--osee-background-tooltip)',
					DEFAULT: 'var(--osee-background-background)',
				},
			},
			gridTemplateColumns: {
				landing: 'repeat(auto-fit, minmax(255px, 1fr))',
			},
			spacing: {
				'landing-button': '255px',
			},
			fontFamily: {
				roboto: ['Roboto', 'Helvetica Neue', 'sans-serif'],
			},
			fontWeight: {
				'mat-bold': 'bold',
			},
		},
	},
	plugins: [
		plugin(function ({ addVariant }) {
			addVariant('even-multi', '&:nth-child(4n+1)');
			addVariant('odd-multi', '&:nth-child(4n+3)');
		}),
	],
};
