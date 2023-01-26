locals {
  roles_common = {
    access_name = "access"
    edit_name = "EDIT"
    scan_name = "SCAN"
    admin_name = "ADMIN"
  }
}

resource "keycloak_role" "video_manager_server_access_role_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = keycloak_openid_client.video_manager_server_dev.id
  name = local.roles_common.access_name
}

resource "keycloak_role" "video_manager_server_edit_role_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = keycloak_openid_client.video_manager_server_dev.id
  name = local.roles_common.edit_name
}

resource "keycloak_role" "video_manager_server_scan_role_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = keycloak_openid_client.video_manager_server_dev.id
  name = local.roles_common.scan_name
}

resource "keycloak_role" "video_manager_server_admin_role_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = keycloak_openid_client.video_manager_server_dev.id
  name = local.roles_common.admin_name
}

resource "keycloak_role" "video_manager_server_access_role_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = keycloak_openid_client.video_manager_server_prod.id
  name = local.roles_common.access_name
}

resource "keycloak_role" "video_manager_server_edit_role_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = keycloak_openid_client.video_manager_server_prod.id
  name = local.roles_common.edit_name
}

resource "keycloak_role" "video_manager_server_scan_role_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = keycloak_openid_client.video_manager_server_prod.id
  name = local.roles_common.scan_name
}

resource "keycloak_role" "video_manager_server_admin_role_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = keycloak_openid_client.video_manager_server_prod.id
  name = local.roles_common.admin_name
}