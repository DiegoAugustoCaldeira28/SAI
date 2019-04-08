/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mb.scap.jsf.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GenerateCsv {

    public static void generateCsvFile(ArrayList<Developer> developers) throws IOException {
        String csvFile = "C:\\Users\\Diego Caldeira\\Documents\\developer.csv";
        FileWriter writer = new FileWriter(csvFile);

        for (Developer developer : developers) {
            List<String> list = new ArrayList<>();
            list.add(developer.getNomeParticipante());
            list.add(developer.getEmailParticipante());
            list.add(developer.getDocumentoParticipante());
            list.add(developer.getNomeEvento());
            list.add(developer.getNomeDepartamento());
            list.add(developer.getNomeCampus());
            list.add(developer.getPeriodoEvento());
            list.add(developer.getNomeCoordenador());
            list.add(developer.getEmentaTXT());
            list.add("");
            for (Curso curso : developer.getCursos()) {
                list.add(curso.getNome());
                String carga = "" + curso.getCargaHoraria();
                list.add(carga);
            }
            String carga = "" + developer.getSoma();
            list.add(carga);
            CSVUtils.writeLine(writer, list);
        }

        writer.flush();
        writer.close();
    }

    public static void generateCsvFileRelatorio(ArrayList<Curso> cursosByCurso) throws IOException {

        for (Curso curso : cursosByCurso) {
            String csvFile = "C:\\Users\\Diego Caldeira\\Documents\\Arquivo\\" + curso.getNome() + ".csv";
            FileWriter writer = new FileWriter(csvFile);            
            for (Relatorio relatorio : curso.getRelatorios()) {
                List<String> list = new ArrayList<>();
                list.add(relatorio.getNome());
                list.add(relatorio.getEmail());
                list.add(relatorio.getDocumento());
                if (relatorio.getPresente()) {
                    list.add("1");
                } else {
                    list.add("0");
                }
                CSVUtils.writeLine(writer, list);
                
            }
            writer.flush();
            writer.close();
        }
    }
}
