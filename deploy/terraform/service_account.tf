data "keycloak_openid_client" "video_manager_converter_dev" {
  realm_id  = data.keycloak_realm.apps_dev.id
  client_id = "video-manager-converter"
}

data "keycloak_openid_client" "video_manager_converter_prod" {
  realm_id  = data.keycloak_realm.apps_prod.id
  client_id = "video-manager-converter"
}

data "keycloak_role" "video_manager_converter_access_role_dev" {
  realm_id  = data.keycloak_realm.apps_dev.id
  client_id = data.keycloak_openid_client.video_manager_converter_dev.id
  name = local.roles_common.access_name
}

data "keycloak_role" "video_manager_converter_access_role_prod" {
  realm_id  = data.keycloak_realm.apps_prod.id
  client_id = data.keycloak_openid_client.video_manager_converter_prod.id
  name = local.roles_common.access_name
}

resource "keycloak_openid_client_service_account_role" "video_manager_server_converter_access_role_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  service_account_user_id = keycloak_openid_client.video_manager_server_dev.service_account_user_id
  client_id = data.keycloak_openid_client.video_manager_converter_dev.id
  role = local.roles_common.access_name
}

resource "keycloak_openid_client_service_account_role" "video_manager_server_converter_access_role_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  service_account_user_id = keycloak_openid_client.video_manager_server_prod.service_account_user_id
  client_id = data.keycloak_openid_client.video_manager_converter_prod.id
  role = local.roles_common.access_name
}