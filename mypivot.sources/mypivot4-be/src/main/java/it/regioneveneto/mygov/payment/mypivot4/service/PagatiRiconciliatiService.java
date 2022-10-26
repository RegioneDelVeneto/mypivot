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
package it.regioneveneto.mygov.payment.mypivot4.service;

import it.regioneveneto.mygov.payment.mypay4.util.Constants;
import it.regioneveneto.mygov.payment.mypay4.util.Utilities;
import it.regioneveneto.mygov.payment.mypay4.util.VerificationUtils;
import it.regioneveneto.mygov.payment.mypivot4.dao.ExportRendicontazioneCompletaDao;
import it.regioneveneto.mygov.payment.mypivot4.dto.PagamentiRiconciliatiTo;
import it.regioneveneto.mygov.payment.mypivot4.model.Ente;
import it.veneto.regione.pagamenti.pivot.ente.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PagatiRiconciliatiService {

    @Autowired
    private EnteService enteService;

    @Autowired
    private ExportRendicontazioneCompletaDao exportRendicontazioneCompletaDao;

    public PivotSILChiediPagatiRiconciliatiRisposta verificaPagatiRiconciliati(PivotSILChiediPagatiRiconciliati bodyrichiesta) {
        PivotSILChiediPagatiRiconciliatiRisposta risposta = new PivotSILChiediPagatiRiconciliatiRisposta();

        Ente ente = enteService.getEnteByCodIpa(bodyrichiesta.getCodIpaEnte());
        List<PagamentiRiconciliatiTo> pagamenti;

        try {
            if (bodyrichiesta.getRichiestaPerData() != null) {
                XMLGregorianCalendar dateExtractionFrom = bodyrichiesta.getRichiestaPerData().getDataDa();
                XMLGregorianCalendar dateExtractionTo = bodyrichiesta.getRichiestaPerData().getDataA();
                /* CONTROLLO SULLA CORRETTEZZA DELLE DATE */
                Date _dtExtractionFrom = Utilities.toDate(dateExtractionFrom);
                Date _dtExtractionTo = Utilities.toDate(dateExtractionTo);
                risposta.setFault(VerificationUtils.checkDateExtractionRendicontazioneCompleta(bodyrichiesta.getCodIpaEnte(), _dtExtractionFrom, _dtExtractionTo));
                if (risposta.getFault() != null)
                    return risposta;

                _dtExtractionFrom = new Date(_dtExtractionFrom.getTime() / 1000 * 1000);
                _dtExtractionTo = new Date(_dtExtractionTo.getTime() / 1000 * 1000);

                final Long enteId = ente.getMygovEnteId();
                final Date dtExtractionFrom = _dtExtractionFrom;
                final Date dtExtractionTo = _dtExtractionTo;
                final List<String> listCodTipo = (bodyrichiesta.getRichiestaPerData().getFiltroTipiDovuto() != null &&
                    !CollectionUtils.isEmpty(bodyrichiesta.getRichiestaPerData().getFiltroTipiDovuto().getIdentificativoTipoDovutos())) ?
                    bodyrichiesta.getRichiestaPerData().getFiltroTipiDovuto().getIdentificativoTipoDovutos() : null;

                pagamenti = exportRendicontazioneCompletaDao.getByDate(enteId, dtExtractionFrom, dtExtractionTo, listCodTipo);
            } else if (bodyrichiesta.getRichiestaPerIUVIUF() != null) {
                /* CONTROLLO ALMENO UNO TRA RiversamentiCumulativi e RiversamentiPuntuali valorizzato */
                CtRiversamentiCumulativi riversamentiCumulativi = bodyrichiesta.getRichiestaPerIUVIUF().getRiversamentiCumulativi();
                CtRiversamentiPuntuali riversamentiPuntuali = bodyrichiesta.getRichiestaPerIUVIUF().getRiversamentiPuntuali();
                List<String> upperFlussos = riversamentiCumulativi.getIdentificativoFlussos().stream().map(String::toUpperCase).collect(Collectors.toList());

                risposta.setFault(VerificationUtils.checkRichiestaPerIUVIUF(bodyrichiesta.getCodIpaEnte(), riversamentiCumulativi, riversamentiPuntuali));
                if (risposta.getFault() != null)
                    return risposta;

                final Long enteId = ente.getMygovEnteId();
                final List<String> listIuf = (upperFlussos != null && !CollectionUtils.isEmpty(upperFlussos)) ? upperFlussos : null;
                final List<String> listIuv = (riversamentiPuntuali != null && !CollectionUtils.isEmpty(riversamentiPuntuali.getIdentificativoUnivocoVersamentos())) ?
                    riversamentiPuntuali.getIdentificativoUnivocoVersamentos() : null;
                final List<String> listCodTipo = (bodyrichiesta.getRichiestaPerIUVIUF().getFiltroTipiDovuto() != null &&
                    !CollectionUtils.isEmpty(bodyrichiesta.getRichiestaPerIUVIUF().getFiltroTipiDovuto().getIdentificativoTipoDovutos())) ?
                    bodyrichiesta.getRichiestaPerIUVIUF().getFiltroTipiDovuto().getIdentificativoTipoDovutos() : null;
                pagamenti = exportRendicontazioneCompletaDao.getByIUVIUF(enteId, listIuf, listIuv, listCodTipo);
            } else {
                risposta.setFault(VerificationUtils.getFaultBean(bodyrichiesta.getCodIpaEnte(), Constants.CODE_PIVOT_RICHIESTA_NON_VALORIZZATA, Constants.DESC_PIVOT_RICHIESTA_NON_VALORIZZATA,null));
                return risposta;
            }
            risposta.setFault(null);
            //group pagamenti by tipoDovuto
            risposta.getPagamentiRiconciliatiPerTipoDovutos().addAll(
                pagamenti.stream()
                    .collect(Collectors.groupingBy(PagamentiRiconciliatiTo::getTipoDovuto))
                    .entrySet().stream()
                    .map(x -> {
                        CtPagamentiRiconciliatiPerTipoDovuto c = new CtPagamentiRiconciliatiPerTipoDovuto();
                        c.setIdentificativoTipoDovuto(x.getKey());
                        c.getPagamentiRiconciliatis().addAll(
                            x.getValue().stream().map(PagamentiRiconciliatiTo::getCtPagamentiRiconciliati).collect(Collectors.toList()));
                        return c;
                    }).collect(Collectors.toList()));
            //add total count
            risposta.setNumeroTotale(BigInteger.valueOf(pagamenti.size()));
            //add max date
            risposta.setDataA(pagamenti.stream().map(x -> x.getCtPagamentiRiconciliati().getData()).max(XMLGregorianCalendar::compare).orElse(null));
            return risposta;

        } catch (Exception ex) {
            log.error("Error executing operation verificaPagatiRiconciliati]", ex);
            risposta.setFault(VerificationUtils.getFaultBean(bodyrichiesta.getCodIpaEnte(), Constants.CODE_PIVOT_SYSTEM_ERROR, ex.getLocalizedMessage(), null));
            return risposta;
        }
    }
}
