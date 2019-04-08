package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.CargaHoraria;
import br.com.mb.scap.jpa.entities.Chamada;
import br.com.mb.scap.jpa.entities.DataEvento;
import br.com.mb.scap.jpa.entities.Evento;
import br.com.mb.scap.jpa.entities.Historico;
import br.com.mb.scap.jpa.entities.Instrutor;
import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.ChamadaFacade;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import static org.primefaces.component.focus.Focus.PropertyKeys.context;

@ManagedBean(name = "chamadaController")
@SessionScoped
public class ChamadaController implements Serializable {

    private Chamada current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.ChamadaFacade ejbFacade;
    @EJB
    private br.com.mb.scap.jpa.session.DataEventoFacade dataEventoFacade;
    @EJB
    private br.com.mb.scap.jpa.session.HistoricoFacade historicoFacade;
    @EJB
    private br.com.mb.scap.jpa.session.CargaHorariaFacade cargaHorariaFacade;
    @EJB
    private br.com.mb.scap.jpa.session.EventoFacade eventoFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<Chamada> chamada = new ArrayList<>();
    private List<Evento> eventosInstrutor = new ArrayList<>();
    private List<Chamada> presentes;

    public ChamadaController() {
    }

    @PostConstruct
    public void init() {
        eventosInstrutor = (List<Evento>) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("lista");
    }

    public Chamada getSelected() {
        if (current == null) {
            current = new Chamada();
            current.setIddataEvento(new DataEvento());
            current.getIddataEvento().setIdevento(new Evento());
            presentes = null;
            selectedItemIndex = -1;
        }
        return current;
    }

    private ChamadaFacade getFacade() {
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

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Chamada) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Chamada();
        selectedItemIndex = -1;
        presentes = new ArrayList<>();
        recreateModel();
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("ChamadaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Chamada) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("ChamadaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Chamada) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("ChamadaDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
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

    public Chamada getChamada(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Chamada.class)
    public static class ChamadaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ChamadaController controller = (ChamadaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "chamadaController");
            return controller.getChamada(getKey(value));
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
            if (object instanceof Chamada) {
                Chamada o = (Chamada) object;
                return getStringKey(o.getIdchamada());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Chamada.class.getName());
            }
        }

    }

    public List<DataEvento> getEventos() {
        return dataEventoFacade.controleChamada();
    }

    /**
     * @return the chamada
     */
    public List<Chamada> getChamada() {
        return chamada;
    }

    /**
     * @param chamada the chamada to set
     */
    public void setChamada(List<Chamada> chamada) {
        this.chamada = chamada;
    }

    public void carregaChamada(ValueChangeEvent event) {
        DataEvento e = (DataEvento) event.getNewValue();
        setChamada(dataEventoFacade.carregaChamada(e));
        System.out.println(chamada);
    }

    public void carregaChamadaInstrutor(ValueChangeEvent event) {
        Evento e = (Evento) event.getNewValue();
        System.out.println(e.getIdevento());
        setChamada(dataEventoFacade.carregaChamada(dataEventoFacade.uniqueDataEvento(e.getIdevento())));
        System.out.println(chamada);
    }

    public boolean getInstrutorLogado() {
        getSelected();
        Instrutor instrutor = SessionContext.getInstance().getInstrutorLogado();
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        if (instrutor == null) {
            try {
                externalContext.redirect(externalContext.getRequestContextPath() + "/faces/instrutor/LoginRelatorio.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instrutor != null;
    }

    /**
     * @return the eventosInstrutor
     */
    public List<Evento> getEventosInstrutor() {
        Instrutor i = SessionContext.getInstance().getInstrutorLogado();
        FacesContext context = FacesContext.getCurrentInstance();
        setEventosInstrutor(eventoFacade.findbyInstrutor(i));
        context.getExternalContext().getFlash().put("lista", eventosInstrutor);
        System.out.println(eventosInstrutor);
        return eventosInstrutor;
    }

    /**
     * @param eventosInstrutor the eventosInstrutor to set
     */
    public void setEventosInstrutor(List<Evento> eventosInstrutor) {
        this.eventosInstrutor = eventosInstrutor;
    }

    /**
     * @return the presentes
     */
    public List<Chamada> getPresentes() {
        return presentes;
    }

    /**
     * @param presentes the presentes to set
     */
    public void setPresentes(List<Chamada> presentes) {
        this.presentes = presentes;
    }

    public String salvar() {
        boolean flag = false;
        for (Chamada itemchamada : chamada) {
            for (Chamada item : presentes) {
                flag = false;
                if (itemchamada.equals(item)) {
                    current = ejbFacade.find(item.getIdchamada());
                    item.setPresente(Boolean.TRUE);
                    if (!((current.getIdaluno().equals(item.getIdaluno())) && (current.getPresente().equals(item.getPresente())))) {
                        ejbFacade.edit(item);
                    }
                    flag = true;
                    break;
                }
            }
            if (!flag) {                
                current = ejbFacade.find(itemchamada.getIdchamada());
                System.out.println(current.getIdaluno());
                System.out.println(current.getPresente());
                System.out.println(itemchamada.getIdaluno());
                System.out.println(itemchamada.getPresente());
                if (((current.getIdaluno().equals(itemchamada.getIdaluno())) && (current.getPresente().equals(itemchamada.getPresente())))) {
                    itemchamada.setPresente(flag);
                    ejbFacade.edit(itemchamada);
                }
            }
        }
        JsfUtil.addSuccessMessage("Chamada realizada com sucesso!");
        return logout();
    }

    public String logout() {
        System.out.println(SessionContext.getInstance().getInstrutorLogado());
        System.out.println("Sessao:" + SessionContext.getInstance());

        SessionContext.getInstance().encerrarSessao();
        current = null;
        return "/instrutor/LoginChamada.xhtml?faces-redirect=true";

    }
}
