data "keycloak_realm" "apps_dev" {
  realm = "apps-dev"
}

data "keycloak_realm" "apps_prod" {
  realm = "apps-prod"
}

locals {
  client_common = {
    client_id = "video-manager-server"
    name = "video-manager-server"
    enabled = true
    access_type = "CONFIDENTIAL"
    service_accounts_enabled = true
  }

  access_role_common = {
    name = "access"
  }
}

resource "keycloak_openid_client" "video_manager_server_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = local.client_common.client_id
  name = local.client_common.name
  enabled = local.client_common.enabled
  access_type = local.client_common.access_type
  service_accounts_enabled = local.client_common.service_accounts_enabled
}

resource "keycloak_openid_client" "video_manager_server_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = local.client_common.client_id
  name = local.client_common.name
  enabled = local.client_common.enabled
  access_type = local.client_common.access_type
  service_accounts_enabled = local.client_common.service_accounts_enabled
}

resource "keycloak_role" "video_manager_server_access_role_dev" {
  realm_id = data.keycloak_realm.apps_dev.id
  client_id = keycloak_openid_client.video_manager_server_dev.id
  name = local.access_role_common.name
}

resource "keycloak_role" "video_manager_server_access_role_prod" {
  realm_id = data.keycloak_realm.apps_prod.id
  client_id = keycloak_openid_client.video_manager_server_prod.id
  name = local.access_role_common.name
}