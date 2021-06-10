#!/usr/bin/env sh
set -eu

envsubst '${MAIN_PROCESSOR_HOST_VAR} ${MAIN_PROCESSOR_PORT_VAR}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

exec "$@"