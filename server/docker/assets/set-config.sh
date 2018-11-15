#!/bin/bash

set -u
set -e
set -x

CONFIG_FILE="ods-configuration.yml"
DEFAULT_COUCHDB_ODSADMIN_PASSWORD="admin"
DEFAULT_COUCHDB_URL="http://localhost:5984/"
DEFAULT_COUCHDB_MAX_CONNECTIONS="100"
DEFAULT_ODS_ADMIN_CONTEXT_PATH="/"
DEFAULT_ODS_CONTEXT_PATH=""
DEFAULT_ODS_ADMIN_PASSWORD="admin"
DEFAULT_NO_VALUE="no-value"
DEFAULT_USER_SERVICE_URL="http://localhost:8090/ods/userservice/api/v1"

if [[ ! -f finished-ods-config ]]; then

	mkdir -p $ODS_LOG_DIR

	if [[ -f $CONFIG_FILE ]]; then
		sed -ie "s/COUCHDB_ODSADMIN_PASSWORD/${COUCHDB_ODSADMIN_PASSWORD:-$DEFAULT_COUCHDB_ODSADMIN_PASSWORD}/" $CONFIG_FILE
		sed -ie "s|COUCHDB_URL|${COUCHDB_URL:-$DEFAULT_COUCHDB_URL}|" $CONFIG_FILE
		sed -ie "s|COUCHDB_MAX_CONNECTIONS|${COUCHDB_MAX_CONNECTIONS:-$DEFAULT_COUCHDB_MAX_CONNECTIONS}|" $CONFIG_FILE
		sed -ie "s|ODS_ADMIN_CONTEXT_PATH|${ODS_ADMIN_CONTEXT_PATH:-$DEFAULT_ODS_ADMIN_CONTEXT_PATH}|" $CONFIG_FILE
		sed -ie "s|ODS_CONTEXT_PATH|${ODS_CONTEXT_PATH:-$DEFAULT_ODS_CONTEXT_PATH}|" $CONFIG_FILE
		sed -ie "s|ODS_ADMIN_PASSWORD|${ODS_ADMIN_PASSWORD:-$DEFAULT_ODS_ADMIN_PASSWORD}|" $CONFIG_FILE
		sed -ie "s/GCM_API_KEY/${GCM_API_KEY:-$DEFAULT_NO_VALUE}/" $CONFIG_FILE
		sed -ie "s/GOOGLE_OAUTH_WEB_CLIENT_ID/${GOOGLE_OAUTH_WEB_CLIENT_ID:-$DEFAULT_NO_VALUE}/" $CONFIG_FILE
		sed -ie "s/GOOGLE_OAUTH_CLIENT_ID_1/${GOOGLE_OAUTH_CLIENT_ID_1:-$DEFAULT_NO_VALUE}/" $CONFIG_FILE
		sed -ie "s/GOOGLE_OAUTH_CLIENT_ID_2/${GOOGLE_OAUTH_CLIENT_ID_2:-$DEFAULT_NO_VALUE}/" $CONFIG_FILE
		sed -ie "s/USER_SERVICE_URL/${USER_SERVICE_URL:-DEFAULT_USER_SERVICE_URL}/" $CONFIG_FILE
	fi
	cat $CONFIG_FILE

	touch finished-ods-config
fi
