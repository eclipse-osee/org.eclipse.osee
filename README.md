# Open System Engineering Environment

The Open System Engineering Environment is a tightly integrated, extensible environment supporting Product Line Engineering in the context of an overall systems engineering approach. It is integrated around a simple, user-definable data model providing full life cycle traceability. OSEE's feature set includes Product Line Engineering, configuration management, requirements management, testing, validation, and project management.

# GitLab CI

The `.gitlab-ci.yml` file in the project root defines the GitLab Runner CI pipeline that's run when pushing to GitLab.

## Environment Variables

In order for the GitLab pipeline to run, the following environment variables must be defined:

- DOCKER_IMAGE_GIT - Name of the Docker image used to run Git commands.
- DOCKER_IMAGE_PYTHON - Name of the Docker image used to run Python scripts.
- DOCKER_IMAGE_NODE - Name of the Docker image used to run Node commands.
- EXCLUDED_KEYWORDS - Semicolon-delimited list of keywords that should not be in the codebase.
