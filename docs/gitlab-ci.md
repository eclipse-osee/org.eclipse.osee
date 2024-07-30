<div style="border: 1px solid red; padding: 10px; background-color: #ffe6e6;">
  <strong>Deprecation Warning:</strong>
  This feature is deprecated as the team has elected to migrate to GitHub rather than GitLab. GitLab related templates will not be kept up to date moving forward. Please consider utilizing our GitHub Actions configurations under the ".github" directory.
</div>

# GitLab CI

The `.gitlab-ci.yml` file in the project root defines the GitLab Runner CI pipeline that's run when pushing to GitLab.

## Environment Variables

In order for the GitLab pipeline to run, the following environment variables must be defined:

- DOCKER_IMAGE_GIT - Name of the Docker image used to run Git commands.
- DOCKER_IMAGE_PYTHON - Name of the Docker image used to run Python scripts.
- DOCKER_IMAGE_NODE - Name of the Docker image used to run Node commands.
- EXCLUDED_KEYWORDS - Semicolon-delimited list of keywords that should not be in the codebase.
- GITLAB_API_TOKEN - Access Token to access GitLab REST API (preferably owned by the project and has read permissions).
