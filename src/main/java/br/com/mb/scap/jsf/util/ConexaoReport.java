/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jsf.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;

/**
 *
 * @author matheus
 */
public class ConexaoReport {

    private final FacesContext context;
    private Connection conexao;

    public ConexaoReport() {
        this.context = FacesContext.getCurrentInstance();
    }

    public File geraRelatorio(String arquivo, String tipo, Map<String, Object> parametros) {
        try {
            String pdfResultado = FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "/resources/report/comprovantes/Comprovante" + tipo + parametros.get("id") + ".pdf";
            File pasta = new File(FacesContext.getCurrentInstance().getExternalContext().getRealPath("")
                    + "/resources/report/comprovantes");
            if (!pasta.exists()) {
                pasta.mkdirs();
            }
            File pdf = new File(pdfResultado);
            InputStream relatorio = new FileInputStream(FacesContext.getCurrentInstance().getExternalContext().getRealPath("") + "/resources/report/" + arquivo);
            String pathLogo = FacesContext.getCurrentInstance().getExternalContext().
                    getRealPath("") + "/resources/report/imagens/logo.png";
            parametros.put("logo", pathLogo);
            JasperReport report = (JasperReport) JRLoader.loadObject(relatorio);
            JasperPrint print = JasperFillManager.fillReport(report, parametros, getConexao());
            JasperExportManager.exportReportToPdfFile(print, pdf.getAbsolutePath());

            return pdf;
        } catch (FileNotFoundException | JRException ex) {
            Logger.getLogger(ConexaoReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Connection getConexao() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexao = DriverManager.getConnection("jdbc:mysql://localhost:3306/sati", "root", "tony2013");
            System.out.println("Conexão com banco");
            return conexao;
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(ConexaoReport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conexao;
    }

    public void fechaConexao() {
        try {
            this.conexao.close();
            System.out.println("Conexao Fechada");
        } catch (SQLException ex) {
            Logger.getLogger(ConexaoReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
