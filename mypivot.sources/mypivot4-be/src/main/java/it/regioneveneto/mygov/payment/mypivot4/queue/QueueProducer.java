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
package it.regioneveneto.mygov.payment.mypivot4.queue;

import it.regioneveneto.mygov.payment.mypay4.config.JmsConfig;
import it.regioneveneto.mygov.payment.mypay4.logging.LogExecution;
import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static it.regioneveneto.mygov.payment.mypay4.util.Constants.TIPO_FLUSSO.*;

@Component
@ConditionalOnBean(JmsConfig.class)
@Slf4j
public class QueueProducer implements InitializingBean {
  @Autowired
  JmsTemplate jmsTemplate;

  @Value("${queue.export-pagati:}")
  private String exportPagatiQueue;

  private boolean exportPagatiQueueEnabled = true;

  @Value("${queue.rendicontazione-standard:}")
  private String rendicontazioneStandardQueue;
  private boolean rendicontazioneStandardQueueEnabled = true;

  @Value("${queue.tesoreria:}")
  private String tesoreriaQueue;
  private boolean tesoreriaQueueEnabled = true;

  @Value("${queue.export-dovuti:}")
  private String exportDovutiQueue;
  private boolean exportDovutiQueueEnabled = true;


  @Value("${spring.artemis.host:null}")
  private String artemisHost;
  @Value("${spring.artemis.port:null}")
  private Long artemisPort;

  @Value("${spring.artemis.user:null}")
  private String artemisUser;

  @Override
  public void afterPropertiesSet() {
    log.info("ActiveMQ - Artemis - host: {} - port: {} - user: {}", artemisHost, artemisPort, artemisUser);
    exportPagatiQueueEnabled = StringUtils.isNotBlank(exportPagatiQueue);
    rendicontazioneStandardQueueEnabled = StringUtils.isNotBlank(rendicontazioneStandardQueue);
    tesoreriaQueueEnabled = StringUtils.isNotBlank(tesoreriaQueue);
    exportDovutiQueueEnabled = StringUtils.isNotBlank(exportDovutiQueue);
    log.info("ActiveMQ - exportPagatiQueueEnabled: {} - rendicontazioneStandardQueueEnabled: {} - tesoreriaQueueEnabled: {} - exportDovutiQueueEnabled: {}"
      , exportPagatiQueueEnabled, rendicontazioneStandardQueueEnabled, tesoreriaQueueEnabled, exportDovutiQueueEnabled);
  }

  @LogExecution(params = LogExecution.ParamMode.ON)
  public String enqueueFlussoUpload(Constants.TIPO_FLUSSO TIPO, String msg) {
    if (TIPO.equals(EXPORT_PAGATI))
      return exportPagatiQueueEnabled ? this.enqueueImpl(exportPagatiQueue, msg) : null;
    else if (TIPO.equals(RENDICONTAZIONE_STANDARD))
      return rendicontazioneStandardQueueEnabled ? this.enqueueImpl(rendicontazioneStandardQueue, msg) : null;
    else if (Arrays.asList(TESORERIA, GIORNALE_DI_CASSA, GIORNALE_DI_CASSA_OPI, ESTRATTO_CONTO_POSTE).contains(TIPO))
      return tesoreriaQueueEnabled ? this.enqueueImpl(tesoreriaQueue, msg) : null;
    else if(TIPO.equals(DOVUTI))
      return exportDovutiQueueEnabled ? this.enqueueImpl(exportDovutiQueue, msg) : null;
    return null;
  }

  public String enqueueTest(String msg){
    return this.enqueueImpl("TEST_QUEUE", UUID.randomUUID() +" - msg: "+ msg);
  }

  private String enqueueImpl(String queueName, String msg){
    final AtomicReference<Message> message = new AtomicReference<>();
    jmsTemplate.convertAndSend(queueName, msg, m -> {
      message.set(m);
      return m;
    });
    String messageId;
    try {
      messageId = message.get().getJMSMessageID();
    } catch (JMSException e) {
      log.warn("cannot retrieve JSM message id", e);
      messageId="?";
    }
    log.info("JMS message sent, queue name:{} - msgId:{}", queueName, messageId);
    return messageId;
  }
}
