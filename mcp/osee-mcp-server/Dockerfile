FROM python:3.12-slim

RUN useradd -u 2000 -m osee-mcp
WORKDIR /app
COPY pyproject.toml ./
COPY src ./src
RUN pip install --no-cache-dir .

USER osee-mcp

# Bind all interfaces inside the container; publish the port deliberately.
ENV MCP_HOST=0.0.0.0 \
    MCP_PORT=8000 \
    OSEE_BASE_URL=http://osee-server:8089
EXPOSE 8000

# OSEE_API_KEY must be supplied at run time, never baked into the image.
ENTRYPOINT ["osee-mcp-server"]
