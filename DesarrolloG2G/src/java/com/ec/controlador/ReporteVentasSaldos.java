/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ec.controlador;

import com.ec.entidad.CabeceraCompra;
import com.ec.entidad.FacturaReporte;
import com.ec.entidad.Tipoambiente;
import com.ec.seguridad.EnumSesion;
import com.ec.seguridad.UserCredential;
import com.ec.servicio.ServicioAcumuladoVentas;
import com.ec.servicio.ServicioCompra;
import com.ec.servicio.ServicioDetalleCompra;
import com.ec.servicio.ServicioFactura;
import com.ec.servicio.ServicioFacturaReporte;
import com.ec.servicio.ServicioTipoAmbiente;
import com.ec.untilitario.ArchivoUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.activation.MimetypesFileTypeMap;
import javax.mail.internet.ParseException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Filedownload;

/**
 *
 * @author gato
 */
public class ReporteVentasSaldos {

    private static String PATH_BASE = "";
    ServicioTipoAmbiente servicioTipoAmbiente = new ServicioTipoAmbiente();
    private Tipoambiente amb = new Tipoambiente();
    ServicioFacturaReporte servicioFacturaReporte = new ServicioFacturaReporte();

    private List<FacturaReporte> listaDatos = new ArrayList<FacturaReporte>();

    private Date inicio = new Date();
    private Date fin = new Date();

    public ReporteVentasSaldos() {
        Session sess = Sessions.getCurrent();
        UserCredential cre = (UserCredential) sess.getAttribute(EnumSesion.userCredential.getNombre());
        amb = servicioTipoAmbiente.findByEstadoEmpresa(cre.getName());
    }

    @Command
    @NotifyChange({"listaDatos", "inicio","fin"})
    public void procesarDatos() {
        try {
             SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        String URLAPI = "http://qbs.tucontador.ec:"+amb.getAmPuerto().trim()+"/api/reporte-factura?inicio=" + formatter.format(inicio) + "&fin=" + formatter.format(fin);
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(
                    HttpClientBuilder.create().build());
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        String response = restTemplate.getForObject(URLAPI, String.class);
        System.out.println("RESPUESTA WS REPORTE " + response);
        listaDatos = servicioFacturaReporte.findBetweenFechas(inicio, fin);
        } catch (Exception e) {
            Clients.showNotification("WS DESCONECTADO"+e.getMessage(),
                Clients.NOTIFICATION_TYPE_ERROR, null, "middle_center", 2000, true);
        }
    }

    @Command
    @NotifyChange({"listaDatos", "inicio","fin"})
    public void obtenerFacturas() {
        listaDatos = servicioFacturaReporte.findBetweenFechas(inicio, fin);
    
    }

    @Command
    public void exportListboxToExcel() throws Exception {
        try {
            File dosfile = new File(exportarExcel());
            if (dosfile.exists()) {
                FileInputStream inputStream = new FileInputStream(dosfile);
                Filedownload.save(inputStream, new MimetypesFileTypeMap().getContentType(dosfile), dosfile.getName());
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR AL DESCARGAR EL ARCHIVO" + e.getMessage());
        }
    }

    private String exportarExcel() throws FileNotFoundException, IOException, ParseException {
        String directorioReportes = Executions.getCurrent().getDesktop().getWebApp().getRealPath("/reportes");

        Date date = new Date();
        SimpleDateFormat fhora = new SimpleDateFormat("HH:mm");
        SimpleDateFormat sm = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sm.format(date);

        String pathSalida = directorioReportes + File.separator + "reportesaldos.xls";
        System.out.println("Direccion del reporte  " + pathSalida);
        try {
            int j = 0;
            File archivoXLS = new File(pathSalida);
            if (archivoXLS.exists()) {
                archivoXLS.delete();
            }
            archivoXLS.createNewFile();
            FileOutputStream archivo = new FileOutputStream(archivoXLS);
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet s = wb.createSheet("Compras");

            HSSFFont fuente = wb.createFont();
            fuente.setBoldweight((short) 700);
            HSSFCellStyle estiloCelda = wb.createCellStyle();
            estiloCelda.setWrapText(true);
            estiloCelda.setAlignment((short) 2);
            estiloCelda.setFont(fuente);

            HSSFCellStyle estiloCeldaInterna = wb.createCellStyle();
            estiloCeldaInterna.setWrapText(true);
            estiloCeldaInterna.setAlignment((short) 5);
            estiloCeldaInterna.setFont(fuente);

            HSSFCellStyle estiloCelda1 = wb.createCellStyle();
            estiloCelda1.setWrapText(true);
            estiloCelda1.setFont(fuente);

            HSSFRow r = null;

            HSSFCell c = null;
            r = s.createRow(0);

            HSSFCell ch0 = r.createCell(j++);
            ch0.setCellValue(new HSSFRichTextString("Fecha"));
            ch0.setCellStyle(estiloCelda);

            HSSFCell ch1 = r.createCell(j++);
            ch1.setCellValue(new HSSFRichTextString("Fecha vencimiento"));
            ch1.setCellStyle(estiloCelda);

            HSSFCell ch2 = r.createCell(j++);
            ch2.setCellValue(new HSSFRichTextString("Tipo Transaccion"));
            ch2.setCellStyle(estiloCelda);

            HSSFCell ch3 = r.createCell(j++);
            ch3.setCellValue(new HSSFRichTextString("Numero"));
            ch3.setCellStyle(estiloCelda);

            HSSFCell ch4 = r.createCell(j++);
            ch4.setCellValue(new HSSFRichTextString("Nombre empresa"));
            ch4.setCellStyle(estiloCelda);

            HSSFCell ch5 = r.createCell(j++);
            ch5.setCellValue(new HSSFRichTextString("Producto / Servicio"));
            ch5.setCellStyle(estiloCelda);

            HSSFCell ch6 = r.createCell(j++);
            ch6.setCellValue(new HSSFRichTextString("Vendedor"));
            ch6.setCellStyle(estiloCelda);
            
             HSSFCell ch7 = r.createCell(j++);
            ch7.setCellValue(new HSSFRichTextString("Importe sujeto a impuestos"));
            ch7.setCellStyle(estiloCelda);
            
             HSSFCell ch8 = r.createCell(j++);
            ch8.setCellValue(new HSSFRichTextString("Importe del impuesto"));
            ch8.setCellStyle(estiloCelda);
            
             HSSFCell ch9 = r.createCell(j++);
            ch9.setCellValue(new HSSFRichTextString("Total factura "));
            ch9.setCellStyle(estiloCelda);
            
             HSSFCell ch10 = r.createCell(j++);
            ch10.setCellValue(new HSSFRichTextString("Estado"));
            ch10.setCellStyle(estiloCelda);
            
             HSSFCell ch11 = r.createCell(j++);
            ch11.setCellValue(new HSSFRichTextString("Saldo total"));
            ch11.setCellStyle(estiloCelda);
            
            
            
                  HSSFCell ch91 = r.createCell(j++);
            ch91.setCellValue(new HSSFRichTextString("Fecha Cobro 1"));
            ch91.setCellStyle(estiloCelda);
            
             HSSFCell ch102 = r.createCell(j++);
            ch102.setCellValue(new HSSFRichTextString("Valor cobro 1"));
            ch102.setCellStyle(estiloCelda);
            
             HSSFCell ch113 = r.createCell(j++);
            ch113.setCellValue(new HSSFRichTextString("Metodo Pago 1"));
            ch113.setCellStyle(estiloCelda);
            
            
            
            
                  HSSFCell ch94 = r.createCell(j++);
            ch94.setCellValue(new HSSFRichTextString("Fecha Cobro 2"));
            ch94.setCellStyle(estiloCelda);
            
             HSSFCell ch105 = r.createCell(j++);
            ch105.setCellValue(new HSSFRichTextString("Valor cobro 2"));
            ch105.setCellStyle(estiloCelda);
            
             HSSFCell ch116 = r.createCell(j++);
            ch116.setCellValue(new HSSFRichTextString("Metodo Pago 2"));
            ch116.setCellStyle(estiloCelda);
            
            
            
            
                  HSSFCell ch97 = r.createCell(j++);
            ch97.setCellValue(new HSSFRichTextString("Fecha Cobro 3"));
            ch97.setCellStyle(estiloCelda);
            
             HSSFCell ch108 = r.createCell(j++);
            ch108.setCellValue(new HSSFRichTextString("Valor cobro 3"));
            ch108.setCellStyle(estiloCelda);
            
             HSSFCell ch119 = r.createCell(j++);
            ch119.setCellValue(new HSSFRichTextString("Metodo pago 3"));
            ch119.setCellStyle(estiloCelda);
            
            
            
            
                  HSSFCell ch910 = r.createCell(j++);
            ch910.setCellValue(new HSSFRichTextString("Fecha Cobro 4"));
            ch910.setCellStyle(estiloCelda);
 
                  HSSFCell ch911 = r.createCell(j++);
            ch911.setCellValue(new HSSFRichTextString("Valor cobro 4"));
            ch911.setCellStyle(estiloCelda);
            
             HSSFCell ch1012 = r.createCell(j++);
            ch1012.setCellValue(new HSSFRichTextString("Metodo pago 4"));
            ch1012.setCellStyle(estiloCelda);
        
            
            
                  HSSFCell ch914 = r.createCell(j++);
            ch914.setCellValue(new HSSFRichTextString("Fecha Cobro 5"));
            ch914.setCellStyle(estiloCelda);
            
             HSSFCell ch1015 = r.createCell(j++);
            ch1015.setCellValue(new HSSFRichTextString("Valor cobro 5"));
            ch1015.setCellStyle(estiloCelda);
            
             HSSFCell ch1116 = r.createCell(j++);
            ch1116.setCellValue(new HSSFRichTextString("Metodo pago 5"));
            ch1116.setCellStyle(estiloCelda);
            
            
            
            
                  HSSFCell ch917 = r.createCell(j++);
            ch917.setCellValue(new HSSFRichTextString("Fecha Cobro 6"));
            ch917.setCellStyle(estiloCelda);
            
             HSSFCell ch1018= r.createCell(j++);
            ch1018.setCellValue(new HSSFRichTextString("Valor cobro 6"));
            ch1018.setCellStyle(estiloCelda);
            
             HSSFCell ch1119 = r.createCell(j++);
            ch1119.setCellValue(new HSSFRichTextString("Metodo pago 6"));
            ch1119.setCellStyle(estiloCelda);
            
            
            
                  HSSFCell ch920 = r.createCell(j++);
            ch920.setCellValue(new HSSFRichTextString("Fecha Cobro 7 "));
            ch920.setCellStyle(estiloCelda);
            
             HSSFCell ch1021 = r.createCell(j++);
            ch1021.setCellValue(new HSSFRichTextString("Valor cobro 7"));
            ch1021.setCellStyle(estiloCelda);
            
             HSSFCell ch1122 = r.createCell(j++);
            ch1122.setCellValue(new HSSFRichTextString("Metodo pago 7"));
            ch1122.setCellStyle(estiloCelda);
            
            
            
                  HSSFCell ch923 = r.createCell(j++);
            ch923.setCellValue(new HSSFRichTextString("Fecha Cobro 8 "));
            ch923.setCellStyle(estiloCelda);
            
             HSSFCell ch1024 = r.createCell(j++);
            ch1024.setCellValue(new HSSFRichTextString("Valor cobro 8"));
            ch1024.setCellStyle(estiloCelda);
            
             HSSFCell ch1125 = r.createCell(j++);
            ch1125.setCellValue(new HSSFRichTextString("Metodo pago 8"));
            ch1125.setCellStyle(estiloCelda);
            
            
            
                  HSSFCell ch926 = r.createCell(j++);
            ch926.setCellValue(new HSSFRichTextString("Fecha Cobro 9 "));
            ch926.setCellStyle(estiloCelda);
            
             HSSFCell ch1027 = r.createCell(j++);
            ch1027.setCellValue(new HSSFRichTextString("Valor cobro 9"));
            ch1027.setCellStyle(estiloCelda);
            
             HSSFCell ch1128 = r.createCell(j++);
            ch1128.setCellValue(new HSSFRichTextString("Metodo pago 9"));
            ch1128.setCellStyle(estiloCelda);
            
            
            
                  
                  HSSFCell ch929 = r.createCell(j++);
            ch929.setCellValue(new HSSFRichTextString("Fecha Cobro 10 "));
            ch929.setCellStyle(estiloCelda);
            
             HSSFCell ch1030 = r.createCell(j++);
            ch1030.setCellValue(new HSSFRichTextString("Valor cobro 10"));
            ch1030.setCellStyle(estiloCelda);
            
             HSSFCell ch1131 = r.createCell(j++);
            ch1131.setCellValue(new HSSFRichTextString("Metodo pago 10"));
            ch1131.setCellStyle(estiloCelda);
            
            

            int rownum = 1;
            int i = 0;

            for (FacturaReporte item : listaDatos) {
                i = 0;

                r = s.createRow(rownum);

                HSSFCell c0 = r.createCell(i++);
                c0.setCellValue(new HSSFRichTextString(sm.format( item.getFacFecha())));

                HSSFCell c1 = r.createCell(i++);
                c1.setCellValue(new HSSFRichTextString(sm.format( item.getFacFechaVencimiento())));

                HSSFCell c2 = r.createCell(i++);
                c2.setCellValue(new HSSFRichTextString(item.getFacTipoTransaccion()));

                HSSFCell c3 = r.createCell(i++);
                c3.setCellValue(new HSSFRichTextString(item.getFacNumeroText()));

                HSSFCell c4 = r.createCell(i++);
                c4.setCellValue(new HSSFRichTextString(item.getFacCliente()));

                HSSFCell c5 = r.createCell(i++);
                c5.setCellValue(new HSSFRichTextString(item.getFacProducto()));

                HSSFCell c6 = r.createCell(i++);
                c6.setCellValue(new HSSFRichTextString(item.getFacVendedor()));
                
                    HSSFCell c7 = r.createCell(i++);
                c7.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacImporteSujetoImpuesto(), 3).toString()));
                
                
                    HSSFCell c8 = r.createCell(i++);
                c8.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacImporteImpuesto(), 3).toString()));
                
                
                                    HSSFCell c81 = r.createCell(i++);
                c81.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacTotal(), 3).toString()));
                
                
                    HSSFCell c9 = r.createCell(i++);
                c9.setCellValue(new HSSFRichTextString(item.getFacEstadoFactura()));
                
                
                    HSSFCell c10 = r.createCell(i++);
                c10.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacSaldoTotal(), 3).toString()));
                
                
                    HSSFCell c11 = r.createCell(i++);
                c11.setCellValue(new HSSFRichTextString(item.getFacFechaCobro1()!=null?sm.format( item.getFacFechaCobro1()):""));
                    HSSFCell c12 = r.createCell(i++);
                c12.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro1()!=null?item.getFacCobro1():BigDecimal.ZERO, 3).toString()));
                    HSSFCell c13 = r.createCell(i++);
                c13.setCellValue(new HSSFRichTextString(item.getFacMetodoPago1()!=null?item.getFacMetodoPago1():""));
                
                
                    HSSFCell c14 = r.createCell(i++);
                c14.setCellValue(new HSSFRichTextString(item.getFacFechaCobro2()!=null?sm.format( item.getFacFechaCobro2()):""));
                    HSSFCell c15 = r.createCell(i++);
                c15.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro2()!=null?item.getFacCobro2():BigDecimal.ZERO, 3).toString()));
                    HSSFCell c16 = r.createCell(i++);
                c16.setCellValue(new HSSFRichTextString(item.getFacMetodoPago2()!=null?item.getFacMetodoPago2():""));
                
                
                    HSSFCell c17 = r.createCell(i++);
                c17.setCellValue(new HSSFRichTextString(item.getFacFechaCobro3()!=null?sm.format( item.getFacFechaCobro3()):""));
                    HSSFCell c18 = r.createCell(i++);
                c18.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro3()!=null?item.getFacCobro3():BigDecimal.ZERO, 3).toString()));
                    HSSFCell c19 = r.createCell(i++);
                c19.setCellValue(new HSSFRichTextString(item.getFacMetodoPago3()!=null?item.getFacMetodoPago3():""));
                
                
                    HSSFCell c20 = r.createCell(i++);
                c20.setCellValue(new HSSFRichTextString(item.getFacFechaCobro4()!=null?sm.format( item.getFacFechaCobro4()):""));
                    HSSFCell c21 = r.createCell(i++);
                c21.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro4()!=null?item.getFacCobro4():BigDecimal.ZERO, 3).toString()));
                    HSSFCell c22 = r.createCell(i++);
                c22.setCellValue(new HSSFRichTextString(item.getFacMetodoPago4()!=null?item.getFacMetodoPago4():""));
                
                
                
                
                    HSSFCell c23= r.createCell(i++);
                c23.setCellValue(new HSSFRichTextString(item.getFacFechaCobro5()!=null?sm.format( item.getFacFechaCobro5()):""));
                    HSSFCell c24 = r.createCell(i++);
                c24.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro5()!=null?item.getFacCobro5():BigDecimal.ZERO, 3).toString()));
                    HSSFCell c25 = r.createCell(i++);
                c25.setCellValue(new HSSFRichTextString(item.getFacMetodoPago5()!=null?item.getFacMetodoPago5():""));
                
                
                
                    HSSFCell c26 = r.createCell(i++);
                c26.setCellValue(new HSSFRichTextString(item.getFacFechaCobro6()!=null?sm.format( item.getFacFechaCobro6()):""));
                    HSSFCell c27 = r.createCell(i++);
                c27.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro6()!=null?item.getFacCobro6():BigDecimal.ZERO, 3).toString()));
                    HSSFCell c28 = r.createCell(i++);
                c28.setCellValue(new HSSFRichTextString(item.getFacMetodoPago6()!=null?item.getFacMetodoPago6():""));
                
                
                
                    HSSFCell c29 = r.createCell(i++);
                c29.setCellValue(new HSSFRichTextString(item.getFacFechaCobro7()!=null?sm.format( item.getFacFechaCobro7()):""));
                    HSSFCell c30 = r.createCell(i++);
                c30.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro7()!=null?item.getFacCobro7():BigDecimal.ZERO, 3).toString()));
                    HSSFCell c31 = r.createCell(i++);
                c31.setCellValue(new HSSFRichTextString(item.getFacMetodoPago7()!=null?item.getFacMetodoPago7():""));
                
                
                
                    HSSFCell c32 = r.createCell(i++);
                c32.setCellValue(new HSSFRichTextString(item.getFacFechaCobro8()!=null?sm.format( item.getFacFechaCobro8()):""));
                    HSSFCell c33 = r.createCell(i++);
                c33.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro8()!=null?item.getFacCobro8():BigDecimal.ZERO, 3).toString()));
                    HSSFCell c34 = r.createCell(i++);
                c34.setCellValue(new HSSFRichTextString(item.getFacMetodoPago8()!=null?item.getFacMetodoPago8():""));
                
                
                
                    HSSFCell c35 = r.createCell(i++);
                c35.setCellValue(new HSSFRichTextString(item.getFacFechaCobro9()!=null?sm.format( item.getFacFechaCobro9()):""));
                    HSSFCell c36 = r.createCell(i++);
                c36.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro9()!=null?item.getFacCobro9():BigDecimal.ZERO, 3).toString()));
                    HSSFCell c37 = r.createCell(i++);
                c37.setCellValue(new HSSFRichTextString(item.getFacMetodoPago9()!=null?item.getFacMetodoPago9():""));
                
                
                
                    HSSFCell c38 = r.createCell(i++);
                c38.setCellValue(new HSSFRichTextString(item.getFacFechaCobro10()!=null?sm.format( item.getFacFechaCobro10()):""));
      
                    HSSFCell c39 = r.createCell(i++);
                c39.setCellValue(new HSSFRichTextString(ArchivoUtils.redondearDecimales( item.getFacCobro10()!=null?item.getFacCobro10():BigDecimal.ZERO, 3).toString()));
   
                    HSSFCell c40 = r.createCell(i++);
                c40.setCellValue(new HSSFRichTextString(item.getFacMetodoPago10()!=null?item.getFacMetodoPago10():""));
                
                  
                   
                /*autemta la siguiente fila*/
                rownum += 1;

            }
            for (int k = 0; k <= listaDatos.size(); k++) {
                s.autoSizeColumn(k);
            }
            wb.write(archivo);
            archivo.close();

        } catch (IOException e) {
            System.out.println("error " + e.getMessage());
        }
        return pathSalida;

    }

    public Tipoambiente getAmb() {
        return amb;
    }

    public void setAmb(Tipoambiente amb) {
        this.amb = amb;
    }

    public List<FacturaReporte> getListaDatos() {
        return listaDatos;
    }

    public void setListaDatos(List<FacturaReporte> listaDatos) {
        this.listaDatos = listaDatos;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFin() {
        return fin;
    }

    public void setFin(Date fin) {
        this.fin = fin;
    }

}
