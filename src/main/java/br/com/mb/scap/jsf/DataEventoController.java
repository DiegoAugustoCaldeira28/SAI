package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.Chamada;
import br.com.mb.scap.jpa.entities.ChamadaAula;
import br.com.mb.scap.jpa.entities.DataEvento;
import br.com.mb.scap.jpa.entities.Evento;
import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.DataEventoFacade;
import br.com.mb.scap.jsf.util.Curso;
import br.com.mb.scap.jsf.util.GenerateCsv;
import br.com.mb.scap.jsf.util.Relatorio;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@SessionScoped
@Named("dataEventoController")
public class DataEventoController implements Serializable {

    private DataEvento current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.DataEventoFacade ejbFacade;
    @EJB
    private br.com.mb.scap.jpa.session.EventoFacade eventoFacade;
    @EJB
    private br.com.mb.scap.jpa.session.DataEventoFacade dataEventoFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<DataEvento> notMinicurso;

    //Atributos adicionais
    private ArrayList<DataEvento> dataEventosFiltrados;

    public DataEventoController() {
    }

    public DataEvento getSelected() {
        if (current == null) {
            current = new DataEvento();
            selectedItemIndex = -1;
        }
        return current;
    }

    private DataEventoFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(1000) {

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

    public String prepareCreate() {
        current = new DataEvento();
        selectedItemIndex = -1;
        recreateModel();
        return "Create_1";
    }

    public String create() {
        try {
            current.setAberto(Boolean.TRUE);
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("DataEventoCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("DataEventoUpdated"));
            cancelSelected();
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy(DataEvento dataEvento) {
        current = dataEvento;
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        cancelSelected();
        return "List";
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("DataEventoDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
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

    public DataEvento getDataEvento(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    //Funções Adicionais
    public ArrayList<DataEvento> getDataEventosFiltrados() {
        return this.dataEventosFiltrados;
    }

    public void setDataEventosFiltrados(ArrayList<DataEvento> dataEventosFiltrado) {
        this.dataEventosFiltrados = dataEventosFiltrado;
    }

    public void editar(DataEvento dataEvento) {
        this.current = dataEvento;
    }

    public void cancelSelected() {
        this.current = null;
    }

    @FacesConverter(forClass = DataEvento.class)
    public static class DataEventoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DataEventoController controller = (DataEventoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "dataEventoController");
            return controller.getDataEvento(getKey(value));
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
            if (object instanceof DataEvento) {
                DataEvento o = (DataEvento) object;
                return getStringKey(o.getIddataEvento());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + DataEvento.class.getName());
            }
        }

    }

    public boolean getUsuarioLogado() {
        getSelected();
        Usuario usuario = SessionContext.getInstance().getUsuarioLogado();
        ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        if (usuario == null) {
            try {
                externalContext.redirect(externalContext.getRequestContextPath() + "/faces/usuario/Login.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(DataEventoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario != null;
    }

    /**
     * @return the notMinicurso
     */
    public List<DataEvento> getNotMinicurso() {
        return ejbFacade.listAllNotMiniCurso();
    }

    /**
     * @param notMinicurso the notMinicurso to set
     */
    public void setNotMinicurso(List<DataEvento> notMinicurso) {
        this.notMinicurso = notMinicurso;
    }

    public void buscarByDepartamento(String departamento) throws IOException {
        
        ArrayList<Evento> eventos = new ArrayList<>(eventoFacade.findAll());
        ArrayList<DataEvento> dataEventos;

        ArrayList<Relatorio> relatorios;
        ArrayList<Curso> cursos = new ArrayList<>();
        for (Evento evento : eventos) {
            if (evento.getCurso().equals(departamento)) {
                if (evento.getTipo().equalsIgnoreCase("Workshop")
                        || evento.getTipo().equalsIgnoreCase("Maratona de Programacao")
                        || evento.getTipo().equalsIgnoreCase("Mesa Redonda")
                        || evento.getTipo().equalsIgnoreCase("Visita Tecnica")
                        || evento.getTipo().equalsIgnoreCase("Oficina")
                        || evento.getTipo().equalsIgnoreCase("Desafio")
                        || evento.getTipo().equalsIgnoreCase("Atividade 3 Esferas")) {
                    dataEventos = new ArrayList<>(ejbFacade.allDataEvento(evento.getIdevento()));
                    ArrayList<Chamada> chamadas;
                    for (DataEvento dataEvento : dataEventos) {
                        Curso curso = new Curso();
                        curso.setNome(dataEvento.getIdevento().getNome());
                        curso.setCargaHoraria(dataEvento.getIdevento().getCargaHoraria());
                        chamadas = new ArrayList<>(dataEventoFacade.carregaChamada(dataEvento));                        
                        relatorios = new ArrayList<>();
                        for (Chamada chamada : chamadas) {
                            Relatorio relatorio = new Relatorio();
                            relatorio.setNome(chamada.getIdaluno().getNome());
                            relatorio.setEmail(chamada.getIdaluno().getEmail());
                            relatorio.setDocumento(chamada.getIdaluno().getRg());
                            relatorio.setPresente(chamada.getPresente());
                            System.out.println("Relatorio = " + relatorio.toString());
                            relatorios.add(relatorio);
                        }
                        curso.setRelatorios(relatorios);                        
                        cursos.add(curso);
                        System.out.println("Curso = " + curso.toString());
                    }
                } else if (evento.getTipo().equalsIgnoreCase("Minicurso")
                        || evento.getTipo().equalsIgnoreCase("Minicurso Computacional")) {
                    dataEventos = new ArrayList<>(ejbFacade.allDataEvento(evento.getIdevento()));
                    ArrayList<ChamadaAula> chamadas;
                    for (DataEvento dataEvento : dataEventos) {
                        Curso curso = new Curso();
                        curso.setNome(dataEvento.getIdevento().getNome());
                        curso.setCargaHoraria(dataEvento.getIdevento().getCargaHoraria());
                        chamadas = new ArrayList<>(dataEventoFacade.carregaChamadaAula(dataEvento));
                        relatorios = new ArrayList<>();
                        for (ChamadaAula chamada : chamadas) {
                            Relatorio relatorio = new Relatorio();
                            relatorio.setNome(chamada.getIdaluno().getNome());
                            relatorio.setEmail(chamada.getIdaluno().getEmail());
                            relatorio.setDocumento(chamada.getIdaluno().getRg());
                            if (chamada.getFaltas() == 0) {
                                relatorio.setPresente(Boolean.TRUE);
                            } else {
                                relatorio.setPresente(Boolean.FALSE);
                            }
                            relatorios.add(relatorio);
                        }
                        curso.setRelatorios(relatorios);
                        cursos.add(curso);
                    }
                }
            }

        }
        GenerateCsv.generateCsvFileRelatorio(cursos);
        System.out.println("Concluido");
    }
    
    public void gerarLista() throws IOException{
        buscarByDepartamento("DEPARTAMENTO DE ENGENHARIA ELETRONICA/ELETRICA");
    }

}
