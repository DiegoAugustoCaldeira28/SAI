package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.Chamada;
import br.com.mb.scap.jpa.entities.ChamadaAula;
import br.com.mb.scap.jpa.entities.DataEvento;
import br.com.mb.scap.jpa.entities.Evento;
import br.com.mb.scap.jpa.entities.Instrutor;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.ChamadaAulaFacade;
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
import javax.inject.Named;
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
import org.primefaces.event.CellEditEvent;

@ManagedBean(name = "chamadaAulaController")
@SessionScoped
public class ChamadaAulaController implements Serializable {

    private ChamadaAula current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.ChamadaAulaFacade ejbFacade;
    @EJB
    private br.com.mb.scap.jpa.session.DataEventoFacade dataEventoFacade;
    @EJB
    private br.com.mb.scap.jpa.session.EventoFacade eventoFacade;
    private List<Evento> eventosInstrutor = new ArrayList<>();
    private PaginationHelper pagination;
    private int selectedItemIndex;
    private List<ChamadaAula> chamada = new ArrayList<>();

    public ChamadaAulaController() {
    }

    @PostConstruct
    public void init() {
        eventosInstrutor = (List<Evento>) FacesContext.getCurrentInstance().getExternalContext().getFlash().get("listaMini");
    }

    public ChamadaAula getSelected() {
        if (current == null) {
            current = new ChamadaAula();
            current.setIddataEvento(new DataEvento());
            current.getIddataEvento().setIdevento(new Evento());
            selectedItemIndex = -1;
        }
        return current;
    }

    private ChamadaAulaFacade getFacade() {
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
        current = (ChamadaAula) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new ChamadaAula();
        selectedItemIndex = -1;
        recreateModel();
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("ChamadaAulaCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (ChamadaAula) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("ChamadaAulaUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (ChamadaAula) getItems().getRowData();
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/Bundle").getString("ChamadaAulaDeleted"));
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

    public ChamadaAula getChamadaAula(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    public List<DataEvento> getEventos() {
        return dataEventoFacade.controleChamadaAula();
    }

    /**
     * @return the chamada
     */
    public List<ChamadaAula> getChamada() {
        return chamada;
    }

    /**
     * @param chamada the chamada to set
     */
    public void setChamadaAula(List<ChamadaAula> chamada) {
        this.chamada = chamada;
    }

    public void carregaChamadaInstrutor(ValueChangeEvent event) {
        Evento e = (Evento) event.getNewValue();
        System.out.println(e.getIdevento());
        setChamadaAula(dataEventoFacade.carregaChamadaAula(dataEventoFacade.uniqueDataEvento(e.getIdevento())));
        System.out.println(chamada);
    }

    public boolean getInstrutorLogado() {
        getSelected();
        Instrutor instrutor = SessionContext.getInstance().getInstrutorLogado();
        ExternalContext externalContext = FacesContext.getCurrentInstance()
                .getExternalContext();
        if (instrutor == null) {
            try {
                externalContext.redirect(externalContext.getRequestContextPath() + "/faces/instrutor/LoginRelatorio.xhtml");
            } catch (IOException ex) {
                Logger.getLogger(UsuarioController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return instrutor != null;
    }

    @FacesConverter(forClass = ChamadaAula.class)
    public static class ChamadaAulaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ChamadaAulaController controller = (ChamadaAulaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "chamadaAulaController");
            return controller.getChamadaAula(getKey(value));
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
            if (object instanceof ChamadaAula) {
                ChamadaAula o = (ChamadaAula) object;
                return getStringKey(o.getIdchamadaAula());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + ChamadaAula.class.getName());
            }
        }

    }

    public void carregaChamada(ValueChangeEvent event) {
        DataEvento e = (DataEvento) event.getNewValue();
        setChamadaAula(dataEventoFacade.carregaChamadaAula(e));
        System.out.println(chamada);
    }

    /**
     * @return the eventosInstrutor
     */
    public List<Evento> getEventosInstrutor() {
        Instrutor i = SessionContext.getInstance().getInstrutorLogado();
        FacesContext context = FacesContext.getCurrentInstance();
        setEventosInstrutor(eventoFacade.findbyInstrutorMini(i));
        context.getExternalContext().getFlash().put("listaMini", eventosInstrutor);
        System.out.println(eventosInstrutor);
        return eventosInstrutor;
    }

    /**
     * @param eventosInstrutor the eventosInstrutor to set
     */
    public void setEventosInstrutor(List<Evento> eventosInstrutor) {
        this.eventosInstrutor = eventosInstrutor;
    }

    public void onCellEdit(CellEditEvent event) {
        int newValue = (int) event.getNewValue();
        current = chamada.get(event.getRowIndex());
        current.setFaltas(newValue);
        getFacade().edit(current);
    }

    public String salvar() {
        for (ChamadaAula item : chamada) {
            current = ejbFacade.find(item.getIdchamadaAula());
            if (!current.equals(item)) {
                ejbFacade.edit(item);
            }
        }
        JsfUtil.addSuccessMessage("Chamada realizada com sucesso!");
        return "/instrutor/LoginChamada.xhtml";
    }

    

    
}
