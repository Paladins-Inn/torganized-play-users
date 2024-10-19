FROM quay.io/kaiserpfalzedv/java-runner:21-latest AS runner

USER 0
RUN apk -U upgrade

USER 1001
COPY --chown=root:root --chmod=0555 target/app.jar /deployments
