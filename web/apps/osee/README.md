# OSEE

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 11.1.4. Currently we are running version 19.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

## Deploying to Jetty

Run `ng build -c production` and copy the produced contents into /plugins/org.eclipse.osee.web.deploy/OSEE-INF/web/dist. Run your local server.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io). Tests should be run with the --browsers=ChromeHeadless flag due to some third party dependencies not playing nice in test.

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via Playwright.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.

## Configure Angular Development Environment

### Check Angular version compatibility

-   This step is essential for determining the compatible Node.js version to select during Node.js installation.
-   Determine current Angular version from `@angular/core` in [`package.json`](/package.json).
-   Check version compatibility [here](https://angular.io/guide/versions).

### Install Node.js

-   Download a compatible version of Node.js for the current Angular version from [here](https://nodejs.org/en/download).

### Verify Node.js Installation

-   Open shell/prompt/bash
-   Run the following commands:
    -   `node -v`
    -   `npm -v`

### Move to OSEE web app directory

-   `cd {path_to_repo}\org.eclipse.osee\web\apps\osee`

### Update code

-   Run:
    -   `git checkout dev`
    -   `git pull --rebase`

### Install pnpm

-   Run:
    -   `npm install -g @pnpm/exe`
        -   If issues arise:
            -   Open Microsoft settings
            -   Search for 'Edit system variables for account'
            -   Bump the `pnpm` home variable to the top of the stack
-   Restart shell/prompt/bash after install completes (make sure you cd back to the {path_to_repo}\org.eclipse.osee\web\apps\osee)

### Verify pnpm Installation

-   Run:
    -   `pnpm -v`

### Set pnpm Store
-   Run:
    -   `pnpm config set store <path/to/.pnpm-store>`

### Install Angular CLI

-   Run:
    -   `npm install -g @angular/cli`
-   Restart shell/prompt/bash after install completes (make sure you cd back to the {path_to_repo}\org.eclipse.osee\web\apps\osee)

### Verify Angular CLI Installation

-   Run:
    -   `ng version`
-   Answer 'yes' for the first question and 'no' for the second question (if prompted with questions)

### Download node dependencies

-   Run:
    -   `pnpm install`

### Running the Application

-   [Run local **OSEE application server**](../../../docs/run-local-application-server.md)
-   Run **Angular web server** from shell/prompt/bash in the {path_to_repo}\org.eclipse.osee directory:
    -   `pnpm -r run serve`
    OR
    -   `pnpm -r run serve --open` to open the page automatically

### Running unit tests

-   Run **Jasmine unit tests** from shell/prompt/bash in the {path_to_repo}\org.eclipse.osee directory:
    -   `pnpm -r run test --browsers=ChromeHeadless`

## Visual Studio Code Extensions 

Visual Studio Code extensions enhance the functionality of the editor by offering additional features, tools, and language support, thereby enabling users to customize their development environment according to their specific needs and preferences. Here are some plugins to consider: 

### Essential plugins:

-   Angular Language Service
-   ESLint
-   Prettier - Code Formatter
-   Tailwind CSS IntelliSense

### Recommended plugins:

-   Error Lens
-   GitLens
