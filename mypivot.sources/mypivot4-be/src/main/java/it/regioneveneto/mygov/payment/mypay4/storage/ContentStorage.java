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

import com.fasterxml.jackson.databind.ObjectMapper;
import it.regioneveneto.mygov.payment.mypay4.exception.MyPayException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ContentStorage {

  private final static String CACHE_NAME = "uploadCache";

  @Autowired
  private ObjectMapper objectMapper;

  @Value("${cache.cacheExpirations."+CACHE_NAME+":${cache.timeoutSeconds}}")
  private long cacheExpirations;

  @Cacheable(value=CACHE_NAME, key="{'file',#storageToken.username,#storageToken.id}", unless="#result==null")
  public byte[] getFileContent(StorageToken storageToken){
    return null;
  }

  @CachePut(value=CACHE_NAME, key="{'file',#uploadToken.username,#uploadToken.id}")
  public byte[] putFileContent(StorageToken storageToken, byte[] fileContent){
    storageToken.uploadTimestamp = System.currentTimeMillis();
    storageToken.expiryTimestamp = storageToken.uploadTimestamp + cacheExpirations * 1000;
    return fileContent;
  }

  @Cacheable(value=CACHE_NAME, key="{'object',#storageToken.username,#storageToken.id}", unless="#result==null")
  public String getObjectAsString(StorageToken storageToken){
    return null;
  }

  public <T> T deserializeString(String serializedObject, Class<T> clazz){
    try{
      return objectMapper.readValue(serializedObject, clazz);
    }catch(Exception e){
      throw new MyPayException("deserializeString", e);
    }
  }

  @CachePut(value=CACHE_NAME, key="{'object',#storageToken.username,#storageToken.id}")
  public String putObject(StorageToken storageToken, Object object){
    String storedObject;
    try{
      storedObject = objectMapper.writeValueAsString(object);
    } catch(Exception e){
      throw new MyPayException("putObject", e);
    }
    storageToken.uploadTimestamp = System.currentTimeMillis();
    storageToken.expiryTimestamp = storageToken.uploadTimestamp + cacheExpirations * 1000;
    return storedObject;
  }

  @CacheEvict(value=CACHE_NAME, key="{'file',#storageToken.username,#storageToken.id}")
  public void deleteStorage(StorageToken storageToken){
  }

  public StorageToken newUploadToken(String username){
    return new StorageToken(username);
  }

  public StorageToken getUploadToken(String username, String tokenId){
    return new StorageToken(username, tokenId);
  }

  public static class StorageToken {
    private final String id;
    private final String username;
    private long expiryTimestamp;
    private long uploadTimestamp;

    private StorageToken(String username){
      this(username,UUID.randomUUID().toString());
    }

    private StorageToken(String username, String tokenId){
      this.id = tokenId;
      this.username = username;
    }

    public String getId(){
      return this.id;
    }

    public long getExpiryTimestamp() {
      return expiryTimestamp;
    }

    public long getUploadTimestamp() {
      return uploadTimestamp;
    }

    public String getUsername() {
      return username;
    }
  }
}
