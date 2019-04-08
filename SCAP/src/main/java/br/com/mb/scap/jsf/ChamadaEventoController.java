package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.Aluno;
import br.com.mb.scap.jpa.entities.ChamadaEvento;
import br.com.mb.scap.jpa.entities.DataEvento;
import br.com.mb.scap.jpa.entities.Historico;
import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jpa.session.AlunoFacade;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.ChamadaEventoFacade;
import br.com.mb.scap.jpa.session.HistoricoFacade;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

@Named("chamadaEventoController")
@SessionScoped
public class ChamadaEventoController implements Serializable {

    @EJB
    private HistoricoFacade historicoFacade;

    @EJB
    private AlunoFacade alunoFacade;

    private ChamadaEvento current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.ChamadaEventoFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    //Adicionais
    private List<ChamadaEvento> chamada;
    private Aluno aluno = new Aluno();
    private DataEvento dataEvento = new DataEvento();

    public ChamadaEventoController() {
    }

    public ChamadaEvento getSelected() {
        if (current == null) {
            current = new ChamadaEvento();
            selectedItemIndex = -1;
        }
        return current;
    }

    private ChamadaEventoFacade getFacade() {
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
        current = (ChamadaEvento) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new ChamadaEvento();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage("Cadastro realizado com sucesso!");
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Erro ao cadastrar! Contacte o administrador");
            return null;
        }
    }

    public String prepareEdit() {
        current = (ChamadaEvento) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("ChamadaEventoUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (ChamadaEvento) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("ChamadaEventoDeleted"));
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

    public ChamadaEvento getChamadaEvento(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = ChamadaEvento.class)
    public static class ChamadaEventoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ChamadaEventoController controller = (ChamadaEventoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "chamadaEventoController");
            return controller.getChamadaEvento(getKey(value));
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
            if (object instanceof ChamadaEvento) {
                ChamadaEvento o = (ChamadaEvento) object;
                return getStringKey(o.getIdchamada());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ChamadaEvento.class.getName());
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
                Logger.getLogger(ChamadaEventoController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario != null;
    }

    /**
     * @return the aluno
     */
    public Aluno getAluno() {
        return aluno;
    }

    /**
     * @param aluno the aluno to set
     */
    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    /**
     * @return the chamada
     */
    public List<ChamadaEvento> getChamada() {
        return chamada;
    }

    /**
     * @param chamada the chamada to set
     */
    public void setChamada(List<ChamadaEvento> chamada) {
        this.chamada = chamada;
    }

    public String findExactCPF() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (!aluno.getCpf().isEmpty()) {
            String cpf = getAluno().getCpf();
            try {
                setAluno(alunoFacade.findCPF(cpf));
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Aluno não castradro"));
                return "ChamadaEvento";
            }
            Date d = new Date();
            d.setTime(System.currentTimeMillis());
            System.out.println(d);
            current.setHora(d);
            //System.out.println(dataEvento.getIddataEvento());
            current.setIddataEvento(dataEvento);
            current.setIdaluno(getAluno());
//            System.err.println(ejbFacade.findSituacao(current));
            if (ejbFacade.findChamadaEvento(current) == null) {
                current.setSituacao(Boolean.TRUE);
            } else {
                ChamadaEvento c = ejbFacade.findChamadaEvento(current);
                if (c.getSituacao()) {
                    Historico h = new Historico();
                    h.setIdaluno(c.getIdaluno());
                    h.setIdevento(c.getIddataEvento().getIdevento());
                    long dif = current.getHora().getTime() - c.getHora().getTime();
                    int tempo;
                    tempo = Integer.valueOf(new BigDecimal(dif / 60000.0).setScale(0, BigDecimal.ROUND_UP).toString());
                    h.setTempo(tempo);
                    historicoFacade.create(h);
                }
                current.setSituacao(!ejbFacade.findChamadaEvento(current).getSituacao());
            }

            create();
            getAluno().setCpf("");
            getAluno().setRa("");
            setChamada(ejbFacade.carregaChamadaPalestra(dataEvento));
            //RequestContext.getCurrentInstance().reset("form1:ra");
            return "ChamadaEvento";
        }

        return "";
    }

    public String findExactRA() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (!aluno.getRa().isEmpty()) {
            String nRa = getAluno().getRa();
            if (getAluno().getRa().substring(0, 1).equals("0")) {
                nRa = getAluno().getRa().substring(1);
            }
            if (getAluno().getRa().substring(0, 2).equals("00")) {
                nRa = getAluno().getRa().substring(2);
            }
            try {
                setAluno(alunoFacade.findIdByRa(nRa));
            } catch (Exception e) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Aluno não castradro"));
                return "ChamadaEvento";
            }
            System.out.println(getAluno().getCpf());
            Date d = new Date();
            d.setTime(System.currentTimeMillis());
            System.out.println(d);
            current.setHora(d);
            //System.out.println(dataEvento.getIddataEvento());
            current.setIddataEvento(dataEvento);
            current.setIdaluno(getAluno());
//            System.err.println(ejbFacade.findSituacao(current));
            if (ejbFacade.findChamadaEvento(current) == null) {
                current.setSituacao(Boolean.TRUE);
            } else {
                ChamadaEvento c = ejbFacade.findChamadaEvento(current);
                if (c.getSituacao()) {
                    Historico h = new Historico();
                    h.setIdaluno(c.getIdaluno());
                    h.setIdevento(c.getIddataEvento().getIdevento());
                    long dif = current.getHora().getTime() - c.getHora().getTime();
                    int tempo;
                    tempo = Integer.valueOf(new BigDecimal(dif / 60000.0).setScale(0, BigDecimal.ROUND_UP).toString());
                    h.setTempo(tempo);
                    historicoFacade.create(h);
                }
                current.setSituacao(!ejbFacade.findChamadaEvento(current).getSituacao());
            }

            create();
            setChamada(ejbFacade.carregaChamadaPalestra(dataEvento));
            return "ChamadaEvento";
        }
        return null;
    }

    public void pegaEvento(ValueChangeEvent event) {
        dataEvento = (DataEvento) event.getNewValue();
        setChamada(ejbFacade.carregaChamadaPalestra(dataEvento));
    }

    public Long totalalunos() {
        if (dataEvento.getIddataEvento() != null) {
            return ejbFacade.TotalPalestra(dataEvento.getIddataEvento());
        }
        return null;
    }

    public ArrayList<DataEvento> getEventosAbertos() {
        return new ArrayList<>(ejbFacade.buscaEventosAbertos());
    }

}
