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
package it.regioneveneto.mygov.payment.mypay4.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JwtTokenUsageStorage {

  private final static String CACHE_NAME = "jwtTokenUsageCache";

  @Cacheable(value=CACHE_NAME, key="{'usage',#jti}", unless="#result==null")
  public Long getTokenUsageTime(String jti){
    log.debug("getTokenUsageTime: "+jti);
    return null;
  }

  @CachePut(value=CACHE_NAME, key="{'usage',#jti}")
  public Long markTokenUsed(String jti){
    log.debug("markTokenUsed: "+jti);
    return System.currentTimeMillis();
  }

  @Cacheable(value=CACHE_NAME, key="{'rolling',#jti}", unless="#result==null")
  public Long wasTokenRolled(String jti){
    log.debug("getRollingToken: "+jti);
    return null;
  }

  @CachePut(value=CACHE_NAME, key="{'rolling',#jti}")
  public Long markTokenRolled(String jti){
    log.debug("setRollingToken: "+jti);
    return System.currentTimeMillis();
  }
}
