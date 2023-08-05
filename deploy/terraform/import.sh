#!/bin/sh

function import {
  terraform \
    import \
    -var="onepassword_token=$ONEPASSWORD_TOKEN"\
    "$1" "$2"
}

function plan {
  terraform plan \
    -var "onepassword_token=$ONEPASSWORD_TOKEN"
}

import "keycloak_openid_client.video_manager_server_dev" "apps-dev/11136690-5205-4e41-8d8b-d2b9f53cf16f"
import "keycloak_openid_client.video_manager_server_prod" "apps-prod/63155423-a119-4f44-ab40-d07c8e1631b1"

import "keycloak_role.video_manager_server_access_role_dev" "apps-dev/"
import "keycloak_role.video_manager_server_edit_role_dev" "apps-dev/"
import "keycloak_role.video_manager_server_scan_role_dev" "apps-dev/"
import "keycloak_role.video_manager_server_admin_role_dev" "apps-dev/"
import "keycloak_role.video_manager_server_access_role_prod" "apps-prod/"
import "keycloak_role.video_manager_server_edit_role_prod" "apps-prod/"
import "keycloak_role.video_manager_server_scan_role_prod" "apps-prod/"
import "keycloak_role.video_manager_server_admin_role_prod" "apps-prod/"

plan