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
package it.regioneveneto.mygov.payment.mypivot4.mapper;

import it.regioneveneto.mygov.payment.mypivot4.dto.BilancioTo;
import it.veneto.regione.pagamenti.pivot.ente.CtAccertamento;
import it.veneto.regione.pagamenti.pivot.ente.CtBilancio;
import it.veneto.regione.pagamenti.pivot.ente.CtCapitolo;
import it.veneto.regione.pagamenti.pivot.ente.CtTipoDovuto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class BilancioToCtBilancioMapper {

  public static List<CtBilancio> map(List<BilancioTo> bilancioList) {
    List<CtBilancio> bilancios = new ArrayList<>();

    for (BilancioTo bilancio: bilancioList) {
      String codUfficio = bilancio.getCodUfficio();
      String codTipoDovuto = bilancio.getCodTipoDovuto();
      String codCapitolo = bilancio.getCodCapitolo();
      String codAccertamento = bilancio.getCodAccertamento();
      BigDecimal sumImporto = bilancio.getImporto();

      if (CollectionUtils.isEmpty(bilancios)) {
        bilancios.add(populateFullBilancio(codUfficio, codTipoDovuto, codCapitolo, codAccertamento, sumImporto));
      } else {
        boolean ufficioPresente = false;
        int idxUfficio = -1;
        for (CtBilancio ctBilancio : bilancios) {
          if (ctBilancio.getUfficio().equals(codUfficio)) {
            ufficioPresente = true;
            idxUfficio = bilancios.indexOf(ctBilancio);
            break;
          }
        }
        if (ufficioPresente) {
          boolean tipoDovutoPresente = false;
          int idxTipoDovuto = -1;
          CtBilancio ctBilancio = bilancios.get(idxUfficio);
          if (CollectionUtils.isEmpty(ctBilancio.getTipoDovutos())) {
            ctBilancio.getTipoDovutos().add(populateFullTipoDovuto(codTipoDovuto, codCapitolo, codAccertamento, sumImporto));
          } else {
            for (CtTipoDovuto ctTipoDovuto : ctBilancio.getTipoDovutos()) {
              if (ctTipoDovuto.getCodTipoDovuto().equals(codTipoDovuto)) {
                tipoDovutoPresente = true;
                idxTipoDovuto = ctBilancio.getTipoDovutos().indexOf(ctTipoDovuto);
                break;
              }
            }
            if (tipoDovutoPresente) {
              boolean capitoloPresente = false;
              int idxCapitolo = -1;
              CtTipoDovuto ctTipoDovuto = ctBilancio.getTipoDovutos().get(idxTipoDovuto);
              if (CollectionUtils.isEmpty(ctTipoDovuto.getCapitolos())) {
                ctTipoDovuto.getCapitolos().add(populateFullCapitolo(codCapitolo, codAccertamento, sumImporto));
              } else {
                for (CtCapitolo ctCapitolo : ctTipoDovuto.getCapitolos()) {
                  if (ctCapitolo.getCodCapitolo().equals(codCapitolo)) {
                    capitoloPresente = true;
                    idxCapitolo = ctTipoDovuto.getCapitolos().indexOf(ctCapitolo);
                    break;
                  }
                }
                if (capitoloPresente) {
                  boolean accertamentoPresente = false;
                  int idxAccertamento = -1;
                  CtCapitolo ctCapitolo = ctTipoDovuto.getCapitolos().get(idxCapitolo);
                  if (CollectionUtils.isEmpty(ctCapitolo.getAccertamentos())) {
                    ctCapitolo.getAccertamentos().add(populateFullAccertamento(codAccertamento, sumImporto));
                  } else {
                    for (CtAccertamento ctAccertamento : ctCapitolo.getAccertamentos()) {
                      if (ctAccertamento.getCodAccertamento().equals(codAccertamento)) {
                        accertamentoPresente = true;
                        idxAccertamento = ctCapitolo.getAccertamentos()
                            .indexOf(ctAccertamento);
                        break;
                      }
                    }
                    if (accertamentoPresente) {
                      CtAccertamento ctAccertamento = ctCapitolo.getAccertamentos()
                          .get(idxAccertamento);

                      BigDecimal accImporto = ctAccertamento.getImporto();
                      BigDecimal sum = accImporto.add(sumImporto);
                      ctAccertamento.setImporto(sum);
                    } else {
                      ctCapitolo.getAccertamentos().add(populateFullAccertamento(codAccertamento, sumImporto));
                    }
                  }
                } else {
                  ctTipoDovuto.getCapitolos().add(populateFullCapitolo(codCapitolo, codAccertamento, sumImporto));
                }
              }
            } else {
              ctBilancio.getTipoDovutos().add(populateFullTipoDovuto(codTipoDovuto, codCapitolo, codAccertamento, sumImporto));
            }
          }
        } else {
          bilancios.add(populateFullBilancio(codUfficio, codTipoDovuto, codCapitolo, codAccertamento, sumImporto));
        }
      }
    }
    log.info("pivotSILChiediAccertamento: FINE MAPPING RISPOSTA");
    return bilancios;

  }

  private static CtBilancio populateFullBilancio(String codUfficio, String codTipoDovuto, String codCapitolo,
                                          String codAccertamento, BigDecimal sumImporto) {
    CtBilancio ctBilancio = new CtBilancio();
    ctBilancio.setUfficio(codUfficio);
    CtTipoDovuto ctTipoDovuto = new CtTipoDovuto();
    ctTipoDovuto.setCodTipoDovuto(codTipoDovuto);
    CtCapitolo ctCapitolo = new CtCapitolo();
    ctCapitolo.setCodCapitolo(codCapitolo);
    CtAccertamento ctAccertamento = new CtAccertamento();
    ctAccertamento.setCodAccertamento(codAccertamento);
    ctAccertamento.setImporto(sumImporto);
    ctCapitolo.getAccertamentos().add(ctAccertamento);
    ctTipoDovuto.getCapitolos().add(ctCapitolo);
    ctBilancio.getTipoDovutos().add(ctTipoDovuto);
    return ctBilancio;
  }

  private static CtTipoDovuto populateFullTipoDovuto(String codTipoDovuto, String codCapitolo, String codAccertamento,
                                              BigDecimal sumImporto) {
    CtTipoDovuto ctTipoDovuto = new CtTipoDovuto();
    ctTipoDovuto.setCodTipoDovuto(codTipoDovuto);
    CtCapitolo ctCapitolo = new CtCapitolo();
    ctCapitolo.setCodCapitolo(codCapitolo);
    CtAccertamento ctAccertamento = new CtAccertamento();
    ctAccertamento.setCodAccertamento(codAccertamento);
    ctAccertamento.setImporto(sumImporto);
    ctCapitolo.getAccertamentos().add(ctAccertamento);
    ctTipoDovuto.getCapitolos().add(ctCapitolo);
    return ctTipoDovuto;
  }

  private static CtCapitolo populateFullCapitolo(String codCapitolo, String codAccertamento, BigDecimal sumImporto) {
    CtCapitolo ctCapitolo = new CtCapitolo();
    ctCapitolo.setCodCapitolo(codCapitolo);
    CtAccertamento ctAccertamento = new CtAccertamento();
    ctAccertamento.setCodAccertamento(codAccertamento);
    ctAccertamento.setImporto(sumImporto);
    ctCapitolo.getAccertamentos().add(ctAccertamento);
    return ctCapitolo;
  }

  private static CtAccertamento populateFullAccertamento(String codAccertamento, BigDecimal sumImporto) {
    CtAccertamento ctAccertamento = new CtAccertamento();
    ctAccertamento.setCodAccertamento(codAccertamento);
    ctAccertamento.setImporto(sumImporto);
    return ctAccertamento;
  }
}
