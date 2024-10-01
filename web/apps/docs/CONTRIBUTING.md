## Contributing to OSEE

For instructions on contributing to the overall OSEE project, see the [OSEE contributing file](../../../CONTRIBUTING.md)

## Prerequisites

1. Install Node.js
   - Download a compatible version of Node.js for the current Astro version from [here](https://nodejs.org/en/download).
2. Install pnpm
   - Run `npm install -g @pnpm/exe`
     - If issues arise:
       - Open Microsoft settings
       - Search for 'Edit system variables for account'
       - Bump the `pnpm` home variable to the top of the stack
   - Restart shell/prompt/bash after install completes (make sure you cd back to the {path_to_repo}\org.eclipse.osee\web\apps\docs)
   - Check pnpm version
     - Run `pnpm -v`
   - Set pnpm store
     - Run `pnpm config set store <path/to/.pnpm-store>`
3. Install dependencies
   - Run `pnpm install`

## Running a local server

See [README.md](./README.md) for instructions on running the application.

## Adding Content

This website is built using [Astro Starlight](https://starlight.astro.build/). See those docs for general instructions on page types, customization, etc.

To add a new page to the site, follow these steps:

1. Create a new markdown file in this project under [src/content/docs/](./src/content/docs/). Place the file in a location that is scoped to the area you are documenting. For example, MIM docs go under `docs/mim/`, ATS docs would go under `docs/ats/`.
   - The files can use the `.md` extension for simple markdown content, or `.mdx` for more complex files that need components and/or conditional logic.
2. Update [astro.config.mjs](./astro.config.mjs)
   - In this file there is a large config object. Locate the `sidebar` key.
   - This sidebar array is structured exactly how the sidebar on the site is displayed. Find the location you would like your new page to be and insert a new object containing the label and slug for the new page.
3. Run a local server to check that your page is showing in the navigation and the content looks like you expect.
