/**
 *     MyPivot - Accounting reconciliation system of Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.regioneveneto.mygov.payment.mypay4.service.myprofile;

import it.regioneveneto.mygov.payment.mypay4.dto.myprofile.MyProfileRoleResponseTo;
import it.regioneveneto.mygov.payment.mypay4.dto.myprofile.MyProfileRoleTo;
import it.regioneveneto.mygov.payment.mypay4.dto.myprofile.MyProfileTenantResponseTo;
import it.regioneveneto.mygov.payment.mypay4.dto.myprofile.MyProfileTenantTo;
import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MyProfileDedicatedService implements MyProfileServiceI {

  @Value("${myprofile.baseUrl}")
  public String baseUrl;

  @Value("${myprofile.applCode}")
  public String applCode;

  private final RestTemplate restTemplate;

  public MyProfileDedicatedService(RestTemplateBuilder restTemplateBuilder) {
    log.info("Using MyProfileDedicatedService as MyProfileServiceI implementation");
    this.restTemplate = restTemplateBuilder.build();
  }

  private Set<String> getUserRoles(String tenantCode, String userCode){
    String url = baseUrl + String.format("roles/%s/%s/%s.json", userCode, tenantCode, applCode);
    MyProfileRoleResponseTo response = this.restTemplate.getForObject(url, MyProfileRoleResponseTo.class);
    if(!StringUtils.equalsIgnoreCase(response.getMessage(), "OK")) {
      log.error("error invoking MyProfile to get userRoles message[{}] userCode[{}], tenantCode[{}]", response.getMessage(), userCode, tenantCode);
      throw new MyPayException("error invoking MyProfile to get userRoles");
    }
    Set<String> roleSet = response.getResultRoles().stream().map(MyProfileRoleTo::getRoleCode).collect(Collectors.toUnmodifiableSet());
    log.debug("MyProfile - roles for user {} tenant {}: {}", userCode, tenantCode, Arrays.toString(roleSet.toArray()));
    return roleSet;
  }

  private Set<String> getUserTenants(String userCode){
    String url = baseUrl + String.format("tenants/%s/%s.json", userCode, applCode);
    MyProfileTenantResponseTo response = this.restTemplate.getForObject(url, MyProfileTenantResponseTo.class);
    if(!StringUtils.equalsIgnoreCase(response.getMessage(), "OK")) {
      log.error("error invoking MyProfile to get tenants message[{}] userCode[{}]", response.getMessage(), userCode);
      throw new MyPayException("error invoking MyProfile to get tenants");
    }
    Set<String> tenantSet = response.getResultTenants().stream().map(MyProfileTenantTo::getTenantCode).collect(Collectors.toUnmodifiableSet());
    log.debug("MyProfile - tenants for user {}: {}", userCode, Arrays.toString(tenantSet.toArray()));
    return tenantSet;
  }

  @Cacheable(value="myProfileCache", key="{'userCode',#userCode}", unless="#result==null")
  public Map<String, Set<String>> getUserTenantsAndRoles(String userCode){
    return getUserTenants(userCode).stream()
        .map(tenant -> new AbstractMap.SimpleImmutableEntry<>(tenant, getUserRoles(tenant, userCode)))
        .collect(Collectors.toUnmodifiableMap(
            AbstractMap.SimpleImmutableEntry::getKey,
            AbstractMap.SimpleImmutableEntry::getValue));
  }

  @CacheEvict(value="myProfileCache",key="{'userCode',#userCode}")
  public void clearUserTenantsAndRoles(String userCode){}

}
