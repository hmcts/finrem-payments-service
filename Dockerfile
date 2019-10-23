ARG APP_INSIGHTS_AGENT_VERSION=2.3.1
FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.2

ENV APP finrem-payment-service.jar

COPY build/libs/$APP /opt/app/

WORKDIR /opt/app

EXPOSE 9001

CMD ["finrem-payment-service.jar"]
