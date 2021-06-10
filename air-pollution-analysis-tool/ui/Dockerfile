FROM node:15.4.0-alpine as build
WORKDIR /app

ARG MAIN_PROCESSOR_HOST=localhost
ARG MAIN_PROCESSOR_PORT=8080

ENV MAIN_PROCESSOR_HOST_VAR=$MAIN_PROCESSOR_PORT
ENV MAIN_PROCESSOR_PORT_VAR=$MAIN_PROCESSOR_PORT

COPY . .

RUN yarn
RUN yarn build

# production environment

FROM nginx:stable-alpine

COPY --from=build /app/build /usr/share/nginx/html
COPY --from=build /app/nginx/nginx.conf.template /etc/nginx/conf.d/default.conf.template
COPY --from=build /app/docker-entrypoint.sh /etc/docker-entrypoint.sh

RUN chmod +x /etc/docker-entrypoint.sh
ENTRYPOINT ["/etc/docker-entrypoint.sh"]

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]