# OSEE

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 11.1.4. Currently we are running version 14.2.4.

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

Run `ng e2e` to execute the end-to-end tests via Cypress. It is recommended to leverage the --watch=false and --headless=true flags when running e2e tests. You may also specify a spec range such as 'cypress/integration/ple/plconfig/\*_/_'.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI Overview and Command Reference](https://angular.io/cli) page.

## Configure Angular Development Environment

### Check Angular version compatibility

- This step is essential for determining the compatible Node.js version to select during Node.js installation.
- Determine current Angular version from `@angular/core` in [`package.json`](/package.json).
- Check version compatibility [here](https://angular.io/guide/versions).

### Install Node.js

- Download a compatible version of Node.js for the current Angular version from [here](https://nodejs.org/en/download).

### Verify Node.js Installation

- Open shell/prompt/bash.
- Run:
  - `node -v`
  - `npm -v`

### Move to OSEE web directory

- `cd {path_to_repo}\org.eclipse.osee\plugins\org.eclipse.osee.web`

### Update code

- Run:
  - `git checkout dev`
  - `git pull --rebase`


### Install pnpm

- Run:
  - `npm install -g @pnpm/exe`
  - Restart shell/prompt/bash after install completes.

### Verify pnpm Installation

- Run:
  - `pnpm -v`

### Install Angular CLI

- Run:
  - `npm install -g @angular/cli`

### Verify Angular CLI Installation

- Run:
  - `ng version`

### Download node dependencies

- Run:
  - `pnpm install`

### Running the Application

- Run local **OSEE application server**:
  - Open Eclipse IDE client used for development.
  - Switch to debug perspective.
  - Click debug configurations dropdown button.
  - Click "debug configurations..."
  - Click "OSEE_Application_Server_[PostGreSQL]" to run local server.
- If database is not populated:
  - Run local server.
  - Run "AtsIde_Integration_TestSuite".
  - Wait for "database initialization complete".
  - Terminate "AtsIde_Integration_TestSuite".
- Run **Angular web server** from shell/prompt/bash (make sure you are still in the \git\org.eclipse.osee\plugins\org.eclipse.osee.web directory):
  - `ng serve`
    OR
  - `ng serve --open` to open the page automatically.

## Visual Studio Code Extensions 

Visual Studio Code extensions enhance the functionality of the editor by offering additional features, tools, and language support, thereby enabling users to customize their development environment according to their specific needs and preferences. Here are some plugins to consider: 

### Essential plugins:

- Angular Language Service
- ESLint
- Prettier - Code Formatter
- Tailwind CSS IntelliSense

### Recommended plugins:

- Error Lens
- GitLens