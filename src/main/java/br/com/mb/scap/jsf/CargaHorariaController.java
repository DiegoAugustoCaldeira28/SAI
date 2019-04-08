package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.CargaHoraria;
import br.com.mb.scap.jpa.entities.Chamada;
import br.com.mb.scap.jpa.entities.ChamadaAula;
import br.com.mb.scap.jpa.entities.Evento;
import br.com.mb.scap.jpa.entities.Historico;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.CargaHorariaFacade;
import br.com.mb.scap.jpa.session.ChamadaAulaFacade;
import br.com.mb.scap.jpa.session.ChamadaFacade;
import br.com.mb.scap.jpa.session.EventoFacade;
import br.com.mb.scap.jpa.session.HistoricoFacade;
import br.com.mb.scap.jsf.util.Curso;
import br.com.mb.scap.jsf.util.Developer;
import br.com.mb.scap.jsf.util.GenerateCsv;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import jdk.nashorn.internal.runtime.arrays.ArrayLikeIterator;

@Named("cargaHorariaController")
@SessionScoped
public class CargaHorariaController implements Serializable {

    private CargaHoraria current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.CargaHorariaFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    @EJB
    private HistoricoFacade historicoFacade;
    @EJB
    private EventoFacade eventoFacade;
    @EJB
    private ChamadaFacade chamadaFacade;
    @EJB
    private ChamadaAulaFacade chamadaAulaFacade;

    public CargaHorariaController() {
    }

    public CargaHoraria getSelected() {
        if (current == null) {
            current = new CargaHoraria();
            selectedItemIndex = -1;
        }
        return current;
    }

    private CargaHorariaFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (CargaHoraria) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new CargaHoraria();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("CargaHorariaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (CargaHoraria) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("CargaHorariaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (CargaHoraria) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("CargaHorariaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public CargaHoraria getCargaHoraria(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = CargaHoraria.class)
    public static class CargaHorariaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CargaHorariaController controller = (CargaHorariaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cargaHorariaController");
            return controller.getCargaHoraria(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof CargaHoraria) {
                CargaHoraria o = (CargaHoraria) object;
                return getStringKey(o.getIdcargaHoraria());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + CargaHoraria.class.getName());
            }
        }

    }

    public void cargaHistorico() {
        ArrayList<Historico> historicos = new ArrayList<>(historicoFacade.findAll());
        int contHisto = 0;
        int contChamaAula = 0;
        int contChamaAulaE = 0;
        int contChama = 0;
        int contChamaE = 0;
        for (Historico historico : historicos) {
            System.out.println(historico.getIdevento().getTempoMinimo());
            if (historico.getIdevento().getTempoMinimo() != 0) {
                if (historico.getIdevento().getTempoMinimo() < historico.getTempo()) {
                    CargaHoraria carga = new CargaHoraria();
                    carga.setIdaluno(historico.getIdaluno());
                    carga.setIdevento(historico.getIdevento());
                    contHisto++;
                    ejbFacade.create(carga);
                } else {
                    System.out.println("Alunos: " + historico.getIdaluno().getNome() + " se fudeo");
                    System.out.println("Alunos: " + historico.getIdaluno().getRa());
                }
            } else {
                CargaHoraria carga = new CargaHoraria();
                carga.setIdaluno(historico.getIdaluno());
                carga.setIdevento(historico.getIdevento());
                contHisto++;
                ejbFacade.create(carga);
            }
        }
        ArrayList<Chamada> chamadas = new ArrayList<>(chamadaFacade.findAll());
        for (Chamada chamada : chamadas) {
            if (chamada.getPresente()) {
                CargaHoraria carga = new CargaHoraria();
                carga.setIdaluno(chamada.getIdaluno());
                carga.setIdevento(chamada.getIddataEvento().getIdevento());
                contChama++;
                ejbFacade.create(carga);
            } else {
                contChamaE++;
            }
        }

        ArrayList<ChamadaAula> chamadaAulas = new ArrayList<>(chamadaAulaFacade.findAll());
        System.out.println();
        for (ChamadaAula chamadaAula : chamadaAulas) {
            if (chamadaAula.getFaltas() == 0) {
                CargaHoraria carga = new CargaHoraria();
                carga.setIdaluno(chamadaAula.getIdaluno());
                carga.setIdevento(chamadaAula.getIddataEvento().getIdevento());
                contChamaAula++;
                ejbFacade.create(carga);
            } else {
                contChamaAulaE++;
            }
        }
        System.out.println("Tamanho Chamada Aula " + chamadaAulas.size());
        System.out.println("Presentes Chamada Aula " + contChamaAula);
        System.out.println("Faltantes Chamada Aula " + contChamaAulaE);
        System.out.println("Tamanho Chamada " + chamadas.size());
        System.out.println("Presentes Chamada Aula " + contChama);
        System.out.println("Faltantes Chamada Aula " + contChamaE);
        System.out.println("Tamanho Historico " + contHisto);
    }

    public void gerarCSV() throws IOException {
        ArrayList<CargaHoraria> alunos = new ArrayList<>(ejbFacade.findCargaAluno());
        ArrayList<Developer> developers = new ArrayList<>();
        Developer developer = new Developer();
        float soma = 0;
        int cont = 0;
        int contErro = 0;
        for (CargaHoraria aluno : alunos) {
            developer.setNomeParticipante(aluno.getIdaluno().getNome());
            developer.setEmailParticipante(aluno.getIdaluno().getEmail());
            developer.setDocumentoParticipante(aluno.getIdaluno().getCpf());
            developer.setNomeEvento("SAI");
            developer.setNomeDepartamento(aluno.getIdaluno().getCurso());
            developer.setNomeCampus("Ponta Grossa");
            developer.setPeriodoEvento("17 a 21 de setembro de 2018");
            developer.setNomeCoordenador("Cesar Chornobai");
            developer.setEmentaTXT("Palestras e mini cursos diversos");
            ArrayList<CargaHoraria> cursosAluno = new ArrayList<>(ejbFacade.findCursoAluno(aluno.getIdaluno().getIdaluno()));
            ArrayList<Curso> cursos = new ArrayList<>();
            soma = 0;
            for (CargaHoraria cursoAluno : cursosAluno) {               
                if (!((cursoAluno.getIdevento().getNome().equalsIgnoreCase("[SATI] Desenvolvimento de Web - PARTE 2/3")) || (cursoAluno.getIdevento().getNome().equalsIgnoreCase("[SATI] Desenvolvimento de Web - PARTE 3/3")))) {
                    Curso curso = new Curso();
                    curso.setNome(cursoAluno.getIdevento().getNome());
                    curso.setCargaHoraria(cursoAluno.getIdevento().getCargaHoraria());
                    cursos.add(curso);
                    soma += cursoAluno.getIdevento().getCargaHoraria();
                    cont++;
                }else{
                    contErro++;                    
                }
            }
            developer.setCursos(cursos);
            developer.setSoma(soma);
            developers.add(developer);
            developer = new Developer();
        }
        GenerateCsv.generateCsvFile(developers);
        System.out.println("Total gerado" + cont);
        System.out.println("Total errado" + contErro);
    }
}
