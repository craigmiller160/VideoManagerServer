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

import "keycloak_role.video_manager_server_access_role_dev" "apps-dev/143c2b37-20fb-45f0-9a2a-a8bae1cfca27"
import "keycloak_role.video_manager_server_edit_role_dev" "apps-dev/3eb11d7b-2c5a-423a-ac36-722e1e002ca7"
import "keycloak_role.video_manager_server_scan_role_dev" "apps-dev/b3a4d69f-1ddd-4660-9816-45afec7c0a24"
import "keycloak_role.video_manager_server_admin_role_dev" "apps-dev/6717bcc2-f790-4d79-b138-6597e9813242"
import "keycloak_role.video_manager_server_access_role_prod" "apps-prod/6306eed0-6d0c-4961-a710-fc3a668a0fc5"
import "keycloak_role.video_manager_server_edit_role_prod" "apps-prod/37516b53-e30f-44f2-b5b3-53b03a5381cb"
import "keycloak_role.video_manager_server_scan_role_prod" "apps-prod/317935b1-6ed3-4917-8f89-a5f6158a50c9"
import "keycloak_role.video_manager_server_admin_role_prod" "apps-prod/3dbd7e57-bd2e-458e-84b4-d368808e5820"

plan