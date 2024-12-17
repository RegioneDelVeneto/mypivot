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

import it.regioneveneto.mygov.payment.mypay4.dto.myprofile.MyProfileRoleTo;
import it.regioneveneto.mygov.payment.mypay4.dto.myprofile.MyProfileTenantTo;
import it.regioneveneto.mygov.payment.mypay4.security.Operatore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Primary
@Slf4j
public class MyProfileRegionalService implements MyProfileServiceI {
//public class MyProfileRegionalService {

  @Value("${myprofile.baseUrl}")
  public String baseUrl;

  @Value("${myprofile.applCode}")
  public String applCode;

  @Value("${mypivot.codIpaEntePredefinito}")
  private String adminEnteCodIpa;

  private final RestTemplate restTemplate;

  public MyProfileRegionalService(RestTemplateBuilder restTemplateBuilder) {
    log.info("Using MyProfileRegionalService as MyProfileServiceI implementation");
    this.restTemplate = restTemplateBuilder.build();
  }

  private Set<String> getUserRoles(String tenantCode, String userCode){
    String url = baseUrl + String.format("myp-roles/%s/%s/%s/", tenantCode, applCode, userCode);
    MyProfileRoleTo[] roles = this.restTemplate.getForObject(url, MyProfileRoleTo[].class);
    Set<String> roleSet = Arrays.stream(roles).map(MyProfileRoleTo::getRoleCode)
      //.filter(role -> StringUtils.equals(role, "ROLE_ACC"))
      .collect(Collectors.toUnmodifiableSet());
    log.debug("MyProfile - roles for user {} tenant {}: {}", userCode, tenantCode, Arrays.toString(roleSet.toArray()));
    return roleSet;
  }

  private Set<String> getUserTenants(String userCode){
    String url = baseUrl + String.format("tenants/%s/%s/", applCode, userCode);
    MyProfileTenantTo[] tenants = this.restTemplate.getForObject(url, MyProfileTenantTo[].class);
    Set<String> tenantSet = Arrays.stream(tenants).map(MyProfileTenantTo::getTenantCode).collect(Collectors.toUnmodifiableSet());
    log.debug("MyProfile - tenants for user {}: {}", userCode, Arrays.toString(tenantSet.toArray()));
    return tenantSet;
  }

  //@Cacheable(value= CacheService.CACHE_NAME_MY_PROFILE, key="{'userCode',#userCode}", unless="#result==null")
  @Cacheable(value="myProfileCache", key="{'userCode',#userCode}", unless="#result==null")
  public Map<String, Set<String>> getUserTenantsAndRoles(String userCode){
    log.debug("invoking getUserTenantsAndRoles [{}]", userCode);
    return getUserTenants(userCode).stream()
        .map(tenant -> new AbstractMap.SimpleImmutableEntry<>(
            tenant.equals("RDV")?"R_VENETO":tenant,  //R_VENETO is mapped as RDV on MyProfile (why???)
            getUserRoles(tenant, userCode)))
        .collect(Collectors.toUnmodifiableMap(
            AbstractMap.SimpleImmutableEntry::getKey,
            AbstractMap.SimpleImmutableEntry::getValue));
  }

  //@CacheEvict(value=CacheService.CACHE_NAME_MY_PROFILE,key="{'userCode',#userCode}")
  @CacheEvict(value="myProfileCache", key="{'userCode',#userCode}")
  public void clearUserTenantsAndRoles(String userCode){}

  public boolean isSystemAdministrator(Map<String, Set<String>> userTenantsAndRoles) {
    log.debug("userTenantsAndRoles: {}",userTenantsAndRoles);
    return userTenantsAndRoles.getOrDefault(adminEnteCodIpa, Set.of()).contains(Operatore.Role.ROLE_ADMIN.name());
  }

}
