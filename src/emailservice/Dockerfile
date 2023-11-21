FROM python:3.10.8-slim@sha256:49749648f4426b31b20fca55ad854caa55ff59dc604f2f76b57d814e0a47c181 as base
FROM base as builder

ARG SEALIGHTS_TOKEN
ARG BRANCH
ARG BUILD_NAME
ARG AGENT_URL
#Don't forget to add git
RUN apt-get -qq update \
    && apt-get install -y --no-install-recommends \
        wget g++ git \
    && rm -rf /var/lib/apt/lists/*

# get packages
COPY requirements.txt .
RUN pip install -r requirements.txt
RUN pip install ${AGENT_URL}

FROM builder
# Enable unbuffered logging
ENV PYTHONUNBUFFERED=1
ENV ENABLE_PROFILER=1

COPY --from=builder /usr/local/lib/python3.10/ /usr/local/lib/python3.10/
WORKDIR /email_server
COPY . .
RUN echo ${SEALIGHTS_TOKEN}
RUN sl-python config --appname emailservice --branchname ${BRANCH} --buildname ${BUILD_NAME} --workspacepath ./ --token ${SEALIGHTS_TOKEN} --exclude "*grpc*"
RUN sl-python scan --scm none --token ${SEALIGHTS_TOKEN}

# Add the application
ENV PORT "8080"
EXPOSE 8080 
CMD sl-python run python email_server.py --labid ${SL_LAB_ID} --token ${SL_TOKEN}
