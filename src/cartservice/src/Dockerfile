# Copyright 2021 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License. 

# https://mcr.microsoft.com/product/dotnet/sdk
FROM mcr.microsoft.com/dotnet/sdk:8.0.100-rc.1@sha256:07414cffa075d67d97a26ad13b0838d9ecb0e22df3384667881ac7a741c472aa as builder

ARG SEALIGHTS_TOKEN
ARG BRANCH
ARG BUILD_NAME
ARG SERVICE_NAME=cartservice

WORKDIR /app
COPY cartservice.csproj .
RUN dotnet restore cartservice.csproj \
    -r linux-musl-x64
COPY . .
RUN dotnet publish cartservice.csproj \
    -p:PublishSingleFile=false \
    -r linux-musl-x64 \
    --self-contained true \
    -p:PublishTrimmed=True \
    -p:TrimMode=Full \
    -c release \
    -o /cartservice \
    --no-restore

	
WORKDIR /sealights

RUN wget -nv -O sealights-dotnet-agent-linux.tar.gz https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-linux-self-contained.tar.gz
RUN mkdir sl-dotnet-agent && tar -xzf ./sealights-dotnet-agent-linux.tar.gz --directory ./sl-dotnet-agent
RUN echo "[Sealights] .NetCore Agent version is: `cat ./sl-dotnet-agent/version.txt`"
RUN dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll config --appName ${SERVICE_NAME} --branchName ${BRANCH} --buildName ${BUILD_NAME} --includeNamespace "*.services*" --token ${SEALIGHTS_TOKEN} --identifyMethodsByFqn 
RUN dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll scan --buildSessionIdFile buildSessionId --workspacePath /app --ignoreGeneratedCode true --token ${SEALIGHTS_TOKEN}

# https://mcr.microsoft.com/product/dotnet/runtime-deps.
FROM mcr.microsoft.com/dotnet/runtime-deps:8.0.0-rc.1-alpine3.18-amd64@sha256:1f58b2f92e2720df01ade6fd1c9e6cc69b79beddbebced5eb128eb3d5e83d8ef

WORKDIR /sealights
COPY --from=builder /sealights/buildSessionId  /sealights/buildSessionId 
RUN mkdir agent logs
RUN wget -nv -O sealights-dotnet-agent-alpine.tar.gz https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-alpine-self-contained.tar.gz
RUN tar -xzf ./sealights-dotnet-agent-alpine.tar.gz --directory /sealights/agent
RUN chmod -R 777 /sealights

WORKDIR /app
COPY --from=builder /cartservice .
EXPOSE 7070
ENV DOTNET_EnableDiagnostics=0 \
    ASPNETCORE_HTTP_PORTS=7070
USER 1000

CMD /sealights/agent/SL.DotNet run --buildSessionIdFile /sealights/buildSessionId --labId ${SL_LAB_ID} --target /app/cartservice --token ${SL_TOKEN}
#[--labId <arg>]
