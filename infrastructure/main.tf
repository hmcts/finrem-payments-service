# Temporary fix for template API version error on deployment
provider "azurerm" {
  version = "1.19.0"
}

locals {
  ase_name = "${data.terraform_remote_state.core_apps_compute.ase_name[0]}"
  local_env = "${(var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env}"

  idam_s2s_url           =  "http://${var.idam_s2s_url_prefix}-${local.local_env}.service.core-compute-${local.local_env}.internal"

  previewVaultName = "${var.reform_team}-aat"
  nonPreviewVaultName = "${var.reform_team}-${var.env}"
  vaultName = "${var.env == "preview" ? local.previewVaultName : local.nonPreviewVaultName}"
  vaultUri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"

  asp_name = "${var.env == "prod" ? "finrem-ps-prod" : "${var.raw_product}-${var.env}"}"
  asp_rg = "${var.env == "prod" ? "finrem-ps-prod" : "${var.raw_product}-${var.env}"}"
}

module "finrem-ps" {
  source                          = "git@github.com:hmcts/cnp-module-webapp?ref=master"
  product                         = "${var.product}-${var.component}"
  location                        = "${var.location}"
  env                             = "${var.env}"
  ilbIp                           = "${var.ilbIp}"
  subscription                    = "${var.subscription}"
  appinsights_instrumentation_key = "${var.appinsights_instrumentation_key}"
  capacity                        = "${var.capacity}"
  is_frontend                     = false
  common_tags                     = "${var.common_tags}"
  asp_name                        = "${local.asp_name}"
  asp_rg                          = "${local.asp_rg}"

  app_settings = {
    REFORM_SERVICE_NAME                                   = "${var.reform_service_name}"
    REFORM_TEAM                                           = "${var.reform_team}"
    REFORM_ENVIRONMENT                                    = "${var.env}"
    IDAM_API_URL                                          = "${var.idam_api_url}"
    FEES_API_URL                                          = "${var.fees_api_url}"
    PRD_API_URL                                           = "${var.prd_api_url}"
    PRD_API_OLD_URL                                       = "${var.prd_api_old_url}"
    PAYMENT_API_URL                                       = "${var.payment_api_url}"
    SSL_VERIFICATION_ENABLED                              = "${var.ssl_verification_enabled}"
    SWAGGER_ENABLED                                       = "${var.swagger_enabled}"
    PRD_API_OLD_URL_ENABLED                               = "${var.prd_api_old_enabled}"
    PRD_LEGACY_URL_ENABLED                                = "${var.prd_legacy_url_enabled}"
    AUTH_PROVIDER_SERVICE_CLIENT_BASEURL                  = "${local.idam_s2s_url}"
    AUTH_PROVIDER_SERVICE_CLIENT_MICROSERVICE             = "${var.auth_provider_service_client_microservice}"
    AUTH_PROVIDER_SERVICE_CLIENT_KEY                      = "${data.azurerm_key_vault_secret.finrem-payment-service-s2s-key.value}"
    AUTH_PROVIDER_SERVICE_CLIENT_TOKENTIMETOLIVEINSECONDS = "${var.auth_provider_service_client_tokentimetoliveinseconds}"
    OAUTH2_CLIENT_FINREM                                  = "${data.azurerm_key_vault_secret.idam-secret.value}"
    USERNAME-SOLICITOR                                    = "${data.azurerm_key_vault_secret.username-solicitor.value}"
    PASSWORD-SOLICITOR                                    = "${data.azurerm_key_vault_secret.password-solicitor.value}"
  }
}

data "azurerm_key_vault" "finrem_key_vault" {
  name                = "${local.vaultName}"
  resource_group_name = "${local.vaultName}"
}

data "azurerm_key_vault_secret" "finrem-payment-service-s2s-key" {
  name      = "finrem-payment-service-s2s-key"
  vault_uri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "username-solicitor" {
  name      = "username-solicitor"
  vault_uri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "idam-secret" {
  name      = "idam-secret"
  vault_uri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "password-solicitor" {
  name      = "password-solicitor"
  vault_uri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"
}

data "azurerm_key_vault_secret" "pdf-service-access-key" {
  name      = "pdf-service-access-key"
  vault_uri = "${data.azurerm_key_vault.finrem_key_vault.vault_uri}"
}


