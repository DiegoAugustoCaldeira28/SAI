package br.com.mb.scap.jsf;

import br.com.mb.scap.jpa.entities.EventosInstrutores;
import br.com.mb.scap.jpa.entities.Usuario;
import br.com.mb.scap.jsf.util.JsfUtil;
import br.com.mb.scap.jsf.util.PaginationHelper;
import br.com.mb.scap.jpa.session.EventosInstrutoresFacade;
import br.com.mb.scap.jsf.util.SessionContext;
import java.io.IOException;

import java.io.Serializable;
import java.util.ArrayList;
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
@Named("eventosInstrutoresController")
public class EventosInstrutoresController implements Serializable {

    private EventosInstrutores current;
    private DataModel items = null;
    @EJB
    private br.com.mb.scap.jpa.session.EventosInstrutoresFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    //Adicionais
    private ArrayList<EventosInstrutores> eventosInstrutoresFiltrados;

    public EventosInstrutoresController() {
    }

    public EventosInstrutores getSelected() {
        if (current == null) {
            current = new EventosInstrutores();
            selectedItemIndex = -1;
        }
        return current;
    }

    private EventosInstrutoresFacade getFacade() {
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
        current = new EventosInstrutores();
        selectedItemIndex = -1;
        recreateModel();
        return "Create_1";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("EventosInstrutoresCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("EventosInstrutoresUpdated"));
            cancelSelected();
            return "List";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/resources/messages_pt_BR").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy(EventosInstrutores eventosInstrutores) {
        current = eventosInstrutores;
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
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/resources/messages_pt_BR").getString("EventosInstrutoresDeleted"));
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

    public EventosInstrutores getEventosInstrutores(java.lang.Integer id) {
        return ejbFacade.find(id);
    }

    /**
     * @return the eventosInstrutoresFiltrados
     */
    public ArrayList<EventosInstrutores> getEventosInstrutoresFiltrados() {
        return eventosInstrutoresFiltrados;
    }

    /**
     * @param eventosInstrutoresFiltrados the eventosInstrutoresFiltrados to set
     */
    public void setEventosInstrutoresFiltrados(ArrayList<EventosInstrutores> eventosInstrutoresFiltrados) {
        this.eventosInstrutoresFiltrados = eventosInstrutoresFiltrados;
    }

    public void editar(EventosInstrutores eventosInstrutores) {
        this.current = eventosInstrutores;
    }

    public void cancelSelected() {
        this.current = null;
    }

    @FacesConverter(forClass = EventosInstrutores.class)
    public static class EventosInstrutoresControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EventosInstrutoresController controller = (EventosInstrutoresController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "eventosInstrutoresController");
            return controller.getEventosInstrutores(getKey(value));
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
            if (object instanceof EventosInstrutores) {
                EventosInstrutores o = (EventosInstrutores) object;
                return getStringKey(o.getIdeventosInstrutor());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + EventosInstrutores.class.getName());
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
                Logger.getLogger(EventosInstrutoresController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return usuario != null;
    }

}
